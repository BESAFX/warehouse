package com.besafx.app.rest;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Withdraw;
import com.besafx.app.search.WithdrawSearch;
import com.besafx.app.service.BankService;
import com.besafx.app.service.PersonService;
import com.besafx.app.service.WithdrawService;
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
@RequestMapping(value = "/api/withdraw/")
public class WithdrawRest {

    private final static Logger log = LoggerFactory.getLogger(WithdrawRest.class);

    private final String FILTER_TABLE = "**,bank[id],lastPerson[id,contact[id,firstName,forthName]]";

    @Autowired
    private PersonService personService;

    @Autowired
    private BankService bankService;

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private WithdrawSearch withdrawSearch;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_WITHDRAW_CREATE')")
    public synchronized String create(@RequestBody Withdraw withdraw, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        withdraw.setLastUpdate(new Date());
        withdraw.setLastPerson(person);
        withdraw.getBank().setStock(withdraw.getBank().getStock() - withdraw.getAmount());
        withdraw.setBank(bankService.save(withdraw.getBank()));
        withdraw = withdrawService.save(withdraw);
        notificationService.notifyOne(Notification.builder().message("تم سحب مبلغ بمقدار " + ArabicLiteralNumberParser.literalValueOf(withdraw.getAmount()) + " ريال سعودي إلي الحساب رقم " + withdraw.getCode() + " بنجاح.").type("success").build(), principal.getName());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), withdraw);
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAll() {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), Lists.newArrayList(withdrawService.findAll()));
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), withdrawService.findOne(id));
    }

    @RequestMapping(value = "filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String filter(
            @RequestParam(value = "code", required = false) final Long code,
            @RequestParam(value = "amountFrom", required = false) final Long amountFrom,
            @RequestParam(value = "amountTo", required = false) final Long amountTo,
            @RequestParam(value = "fromName", required = false) final String fromName,
            @RequestParam(value = "dateFrom,", required = false) final Long dateFrom,
            @RequestParam(value = "dateTo,", required = false) final Long dateTo,
            @RequestParam(value = "bankCode", required = false) final Long bankCode,
            @RequestParam(value = "bankName", required = false) final String bankName,
            @RequestParam(value = "bankBranchName", required = false) final String bankBranchName,
            @RequestParam(value = "bankStockFrom", required = false) final Long bankStockFrom,
            @RequestParam(value = "bankStockTo", required = false) final Long bankStockTo,
            @RequestParam(value = "branchId", required = false) final Long branchId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                withdrawSearch.search(code, amountFrom, amountTo, fromName, dateFrom, dateTo, bankCode, bankName, bankBranchName, bankStockFrom, bankStockTo, branchId));
    }

}
