package com.besafx.app.rest;

import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Payment;
import com.besafx.app.entity.PaymentOut;
import com.besafx.app.entity.Person;
import com.besafx.app.search.PaymentOutSearch;
import com.besafx.app.service.PaymentOutService;
import com.besafx.app.service.PersonService;
import com.besafx.app.util.ArabicLiteralNumberParser;
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
import java.util.List;

@RestController
@RequestMapping(value = "/api/paymentOut/")
public class PaymentOutRest {

    private final static Logger log = LoggerFactory.getLogger(PaymentOutRest.class);

    private final String FILTER_TABLE = "**,branch[id,code,name],person[id,contact[id,firstName,forthName]]";

    @Autowired
    private PersonService personService;

    @Autowired
    private PaymentOutService paymentOutService;

    @Autowired
    private PaymentOutSearch paymentOutSearch;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PAYMENT_OUT_CREATE')")
    public String create(@RequestBody PaymentOut paymentOut, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        if (paymentOutService.findByCodeAndBranch(paymentOut.getCode(), person.getBranch()) != null) {
            throw new CustomException("لا يمكن تكرار رقم السند على مستوى الفرع، حيث لكل فرع دفتر سندات صرف خاص به");
        }
        paymentOut.setDate(new Date());
        paymentOut.setPerson(person);
        paymentOut.setAmountString(ArabicLiteralNumberParser.literalValueOf(paymentOut.getAmountNumber()));
        paymentOut = paymentOutService.save(paymentOut);
        notificationService.notifyOne(Notification.builder().message("تم انشاء سند صرف بنجاح").type("success").build(), principal.getName());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), paymentOut);
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PAYMENT_OUT_UPDATE')")
    public String update(@RequestBody PaymentOut paymentOut, Principal principal) {
        PaymentOut object = paymentOutService.findOne(paymentOut.getId());
        if (object != null) {
            Person person = personService.findByEmail(principal.getName());
            paymentOut.setDate(new Date());
            paymentOut.setPerson(person);
            paymentOut = paymentOutService.save(paymentOut);
            notificationService.notifyOne(Notification.builder().message("تم تعديل بيانات السند بنجاح").type("information").build(), principal.getName());
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), paymentOut);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PAYMENT_OUT_DELETE')")
    public void delete(@PathVariable Long id, Principal principal) {
        PaymentOut object = paymentOutService.findOne(id);
        if (object != null) {
            paymentOutService.delete(id);
            notificationService.notifyOne(Notification.builder().message("تم حذف حساب بنك بنجاح").type("error").build(), principal.getName());
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAll() {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), Lists.newArrayList(paymentOutService.findAll()));
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), paymentOutService.findOne(id));
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
            @RequestParam(value = "dateFrom", required = false) final Long dateFrom,
            @RequestParam(value = "dateTo", required = false) final Long dateTo,
            @RequestParam(value = "branchId", required = false) final Long branchId) {
        List<PaymentOut> list =  paymentOutSearch.search(paymentCodeFrom, paymentCodeTo, paymentDateFrom, paymentDateTo, amountFrom, amountTo, dateFrom, dateTo, branchId);
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), list);
    }

}
