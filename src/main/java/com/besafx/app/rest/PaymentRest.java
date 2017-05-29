package com.besafx.app.rest;

import com.besafx.app.entity.Payment;
import com.besafx.app.entity.Person;
import com.besafx.app.search.PaymentSearch;
import com.besafx.app.service.AccountService;
import com.besafx.app.service.BranchService;
import com.besafx.app.service.PaymentService;
import com.besafx.app.service.PersonService;
import com.besafx.app.util.ArabicLiteralNumberParser;
import com.besafx.app.ws.NotificationService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/api/payment")
public class PaymentRest {

    @Autowired
    private PersonService personService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PaymentSearch paymentSearch;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PAYMENT_CREATE')")
    public Payment create(@RequestBody Payment payment, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        payment.setLastUpdate(new Date());
        payment.setLastPerson(person);
        payment.setAmountString(ArabicLiteralNumberParser.literalValueOf(payment.getAmountNumber()));
        payment = paymentService.save(payment);
        return payment;
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PAYMENT_UPDATE')")
    public Payment update(@RequestBody Payment payment, Principal principal) {
        Payment object = paymentService.findOne(payment.getId());
        if (object != null) {
            Person person = personService.findByEmail(principal.getName());
            payment.setLastUpdate(new Date());
            payment.setLastPerson(person);
            payment = paymentService.save(payment);
            return payment;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PAYMENT_DELETE')")
    public void delete(@PathVariable Long id) {
        Payment object = paymentService.findOne(id);
        if (object != null) {
            paymentService.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Payment> findAll() {
        return Lists.newArrayList(paymentService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Payment findOne(@PathVariable Long id) {
        return paymentService.findOne(id);
    }

    @RequestMapping(value = "findByAccount/{accountId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Payment> findByAccount(@PathVariable Long accountId) {
        return paymentService.findByAccount(accountService.findOne(accountId));
    }

    @RequestMapping(value = "findByBranch/{branchId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Payment> findByBranch(@PathVariable Long branchId) {
        return paymentService.findByAccountCourseMasterBranch(branchService.findOne(branchId));
    }

    @RequestMapping(value = "findByAccountBranch/{branchId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Payment> findByAccountBranch(@PathVariable Long branchId) {
        return paymentService.findByAccountCourseMasterBranch(branchService.findOne(branchId));
    }

    @RequestMapping(value = "findPaidPriceByAccount/{accountId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Double findPaidPriceByAccount(@PathVariable(value = "accountId") Long accountId) {
        return paymentService.findByAccount(accountService.findOne(accountId)).stream().mapToDouble(row -> row.getAmountNumber()).sum();
    }

    @RequestMapping(value = "findByCode/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Payment findByCode(@PathVariable Integer code) {
        return paymentService.findByCode(code);
    }

    @RequestMapping(value = "findByCodeAndBranch/{code}/{branchId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Payment findByCodeAndAccountCourseMasterBranch(@PathVariable(value = "code") Integer code, @PathVariable(value = "branchId") Long branchId) {
        return paymentService.findByCodeAndAccountCourseMasterBranch(code, branchService.findOne(branchId));
    }

    @RequestMapping(value = "filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Payment> filter(
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
            @RequestParam(value = "type", required = false) final String type) {

        return paymentSearch.search(paymentCodeFrom, paymentCodeTo, paymentDateFrom, paymentDateTo, amountFrom, amountTo, firstName, secondName, thirdName, forthName, dateFrom, dateTo, studentIdentityNumber, studentMobile, coursePriceFrom, coursePriceTo, course, master, branch, type);
    }
}
