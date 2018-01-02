package com.besafx.app.rest;

import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Payment;
import com.besafx.app.entity.PaymentBook;
import com.besafx.app.entity.Person;
import com.besafx.app.service.PaymentBookService;
import com.besafx.app.service.PaymentService;
import com.besafx.app.service.PersonService;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/paymentBook/")
public class PaymentBookRest {

    private final static Logger log = LoggerFactory.getLogger(PaymentBookRest.class);

    private final String FILTER_TABLE = "**,branch[id,code,name],lastPerson[id,contact[id,firstName,forthName]],payments[id,code]";
    private final String FILTER_PAYMENT_BOOK_COMBO = "id,code,fromCode,toCode,maxCode,note,branch[id,name]";

    @Autowired
    private PersonService personService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentBookService paymentBookService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PAYMENT_BOOK_CREATE')")
    public String create(@RequestBody PaymentBook paymentBook, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        paymentBook.setLastUpdate(new Date());
        paymentBook.setLastPerson(person);
        paymentBook = paymentBookService.save(paymentBook);
        notificationService.notifyAll(Notification.builder().message("تم انشاء دفتر السندات بنجاح").type("success").build());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), paymentBook);
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PAYMENT_BOOK_UPDATE')")
    public String update(@RequestBody PaymentBook paymentBook, Principal principal) {
        PaymentBook object = paymentBookService.findOne(paymentBook.getId());
        if (object != null) {
            if(!object.getPayments().isEmpty()){
                throw new CustomException("لا يمكن التعديل على دفتر سندات تم تفعيله سابقاً");
            }
            Person person = personService.findByEmail(principal.getName());
            paymentBook.setLastUpdate(new Date());
            paymentBook.setLastPerson(person);
            paymentBook = paymentBookService.save(paymentBook);
            notificationService.notifyAll(Notification.builder().message("تم تعديل بيانات دفتر السندات رقم " + paymentBook.getCode() + " بنجاح.").type("warn").build());
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), paymentBook);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PAYMENT_BOOK_DELETE')")
    public void delete(@PathVariable Long id) {
        PaymentBook object = paymentBookService.findOne(id);
        if (object != null) {
            paymentService.delete(object.getPayments());
            paymentBookService.delete(object);
            notificationService.notifyAll(Notification.builder().message("تم حذف حساب بنك بنجاح").type("error").build());
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAll() {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), Lists.newArrayList(paymentBookService.findAll()));
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), paymentBookService.findOne(id));
    }

    @RequestMapping(value = "findByBranch/{branchId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByBranch(@PathVariable Long branchId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), paymentBookService.findByBranchId(branchId));
    }

    @RequestMapping(value = "findByBranchCombo/{branchId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByBranchCombo(@PathVariable Long branchId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_PAYMENT_BOOK_COMBO), paymentBookService.findByBranchId(branchId));
    }

    @RequestMapping(value = "fetchTableData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchTableData(Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                person.getBranches()
                        .stream()
                        .flatMap(branch -> branch.getPaymentBooks().stream())
                        .collect(Collectors.toList()));
    }

    @RequestMapping(value = "fetchTableDataCombo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchTableDataCombo(Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_PAYMENT_BOOK_COMBO),
                person.getBranches()
                        .stream()
                        .flatMap(branch -> branch.getPaymentBooks().stream())
                        .collect(Collectors.toList()));
    }

}
