package com.besafx.app.rest;

import com.besafx.app.config.CustomException;
import com.besafx.app.config.DropboxManager;
import com.besafx.app.entity.Account;
import com.besafx.app.entity.Attach;
import com.besafx.app.entity.Payment;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.wrapper.PaymentAttachWrapper;
import com.besafx.app.search.PaymentSearch;
import com.besafx.app.service.*;
import com.besafx.app.util.ArabicLiteralNumberParser;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

@RestController
@RequestMapping(value = "/api/payment")
public class PaymentRest {

    private final static Logger log = LoggerFactory.getLogger(AccountRest.class);

    public static final String FILTER_TABLE = "**,lastPerson[id,contact[id,firstName,forthName]],account[id,registerDate,code,student[id,contact[id,firstName,secondName,thirdName,forthName]],course[id,code,master[id,code,name,masterCategory[id,name],branch[id,code]]]]";

    @Autowired
    private PersonService personService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PaymentSearch paymentSearch;

    @Autowired
    private DropboxManager dropboxManager;

    @Autowired
    private AttachService attachService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PAYMENT_CREATE')")
    public String create(@RequestBody Payment payment, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        if (payment.getType().equals("مصروفات")) {
            if (paymentService.findByCodeAndLastPersonBranch(payment.getCode(), person.getBranch()) != null) {
                throw new CustomException("لا يمكن تكرار رقم السند على مستوى الفرع، حيث لكل فرع دفتر سندات صرف خاص به");
            }
        } else {
            if (paymentService.findByCodeAndAccountCourseMasterBranch(payment.getCode(), payment.getAccount().getCourse().getMaster().getBranch()) != null) {
                throw new CustomException("لا يمكن تكرار رقم السند على مستوى الفرع، حيث لكل فرع دفتر سندات قبض خاص به");
            }
        }
        payment.setLastPerson(person);
        payment.setLastUpdate(new Date());
        payment.setAmountString(ArabicLiteralNumberParser.literalValueOf(payment.getAmountNumber()));
        payment = paymentService.save(payment);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على سندات القبض")
                .message("تم انشاء سند قبض بنجاح")
                .type("success")
                .icon("fa-plus-square")
                .build(), principal.getName());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), payment);
    }

    @RequestMapping(value = "createWrapper", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PAYMENT_CREATE')")
    public String createWrapper(@RequestBody PaymentAttachWrapper wrapper, Principal principal) throws Exception {
        Person person = personService.findByEmail(principal.getName());
        if (wrapper.getPayment().getType().equals("مصروفات")) {
            if (paymentService.findByCodeAndLastPersonBranch(wrapper.getPayment().getCode(), person.getBranch()) != null) {
                throw new CustomException("لا يمكن تكرار رقم السند على مستوى الفرع، حيث لكل فرع دفتر سندات صرف خاص به");
            }
        } else {
            if (paymentService.findByCodeAndAccountCourseMasterBranch(wrapper.getPayment().getCode(), wrapper.getPayment().getAccount().getCourse().getMaster().getBranch()) != null) {
                throw new CustomException("لا يمكن تكرار رقم السند على مستوى الفرع، حيث لكل فرع دفتر سندات قبض خاص به");
            }
        }
        wrapper.getPayment().setLastPerson(person);
        wrapper.getPayment().setLastUpdate(new Date());
        wrapper.getPayment().setAmountString(ArabicLiteralNumberParser.literalValueOf(wrapper.getPayment().getAmountNumber()));
        wrapper.setPayment(paymentService.save(wrapper.getPayment()));
        log.info("STARTING UPLOAD ATTACH...");
        Attach attach = new Attach();
        attach.setName(wrapper.getFile().getName());
        attach.setSize(wrapper.getFile().length());
        attach.setDate(new DateTime().toDate());
        attach.setPerson(personService.findByEmail(principal.getName()));
        Future<Boolean> uploadTask = dropboxManager.uploadFile(wrapper.getFile(), "/Smart Offer/Payments/" + wrapper.getPayment().getId() + "/" + wrapper.getFile().getName());
        if (uploadTask.get()) {
            Future<String> shareTask = dropboxManager.shareFile("/Smart Offer/Payments/" + wrapper.getPayment().getId() + "/" + wrapper.getFile().getName());
            attach.setLink(shareTask.get());
            attach = attachService.save(attach);
            wrapper.getPayment().setAttach(attach);
            log.info("ENDING UPLOAD ATTACH...");
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), paymentService.save(wrapper.getPayment()));
        } else {
            return null;
        }
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PAYMENT_UPDATE')")
    public String update(@RequestBody Payment payment, Principal principal) {
        Payment object = paymentService.findOne(payment.getId());
        if (object != null) {
            Person person = personService.findByEmail(principal.getName());
            payment.setDate(new Date());
            payment.setLastPerson(person);
            payment = paymentService.save(payment);
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), payment);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PAYMENT_DELETE')")
    public void delete(@PathVariable Long id, Principal principal) {
        Payment object = paymentService.findOne(id);
        if (object != null) {
            paymentService.delete(id);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على سندات القبض")
                    .message("تم حذف سند القبض رقم " + object.getCode() + " بنجاح")
                    .type("success")
                    .icon("fa-trash")
                    .build(), principal.getName());
        }
    }

    @RequestMapping(value = "deleteByCourse/{courseId}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PAYMENT_DELETE')")
    public void deleteByCourse(@PathVariable Long courseId, Principal principal) {
        List<Account> accounts = accountService.findByCourseId(courseId);
        if (!accounts.isEmpty()) {
            List<Payment> payments = paymentService.findByAccountIn(accounts);
            paymentService.delete(payments);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على الدورات")
                    .message("تم حذف سندات كل طلاب الدورة بنجاح")
                    .type("success")
                    .icon("fa-trash")
                    .build(), principal.getName());
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAll() {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), Lists.newArrayList(paymentService.findAll()));
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), paymentService.findOne(id));
    }

    @RequestMapping(value = "findByAccount/{accountId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByAccount(@PathVariable Long accountId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), paymentService.findByAccountId(accountId));
    }

    @RequestMapping(value = "filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String filter(
            @RequestParam(value = "paymentCodeFrom", required = false) final String paymentCodeFrom,
            @RequestParam(value = "paymentCodeTo", required = false) final String paymentCodeTo,
            @RequestParam(value = "paymentDateFrom", required = false) final Long paymentDateFrom,
            @RequestParam(value = "paymentDateTo", required = false) final Long paymentDateTo,
            @RequestParam(value = "amountFrom", required = false) final Long amountFrom,
            @RequestParam(value = "amountTo", required = false) final Long amountTo,
            @RequestParam(value = "firstName", required = false) final String firstName,
            @RequestParam(value = "secondName", required = false) final String secondName,
            @RequestParam(value = "thirdName", required = false) final String thirdName,
            @RequestParam(value = "forthName", required = false) final String forthName,
            @RequestParam(value = "dateFrom", required = false) final Long dateFrom,
            @RequestParam(value = "dateTo", required = false) final Long dateTo,
            @RequestParam(value = "studentIdentityNumber", required = false) final String studentIdentityNumber,
            @RequestParam(value = "studentMobile", required = false) final String studentMobile,
            @RequestParam(value = "coursePriceFrom", required = false) final Long coursePriceFrom,
            @RequestParam(value = "coursePriceTo", required = false) final Long coursePriceTo,
            @RequestParam(value = "course", required = false) final Long course,
            @RequestParam(value = "master", required = false) final Long master,
            @RequestParam(value = "branch", required = false) final Long branch,
            @RequestParam(value = "personBranch", required = false) final Long personBranch,
            @RequestParam(value = "type", required = false) final String type) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), paymentSearch.search(paymentCodeFrom, paymentCodeTo, paymentDateFrom, paymentDateTo, amountFrom, amountTo, firstName, secondName, thirdName, forthName, dateFrom, dateTo, studentIdentityNumber, studentMobile, coursePriceFrom, coursePriceTo, course, master, branch, personBranch, type));
    }
}
