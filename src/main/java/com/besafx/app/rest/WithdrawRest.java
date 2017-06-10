package com.besafx.app.rest;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Withdraw;
import com.besafx.app.search.WithdrawSearch;
import com.besafx.app.service.BankService;
import com.besafx.app.service.PersonService;
import com.besafx.app.service.WithdrawService;
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
@RequestMapping(value = "/api/withdraw/")
public class WithdrawRest {

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
    public Withdraw create(@RequestBody Withdraw withdraw, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        withdraw.setLastUpdate(new Date());
        withdraw.setLastPerson(person);
        withdraw.getBank().setStock(withdraw.getBank().getStock() - withdraw.getAmount());
        withdraw.setBank(bankService.save(withdraw.getBank()));
        withdraw = withdrawService.save(withdraw);
        return withdraw;
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_WITHDRAW_DELETE')")
    public void delete(@PathVariable Long id) {
        Withdraw object = withdrawService.findOne(id);
        if (object != null) {
            withdrawService.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Withdraw> findAll() {
        return Lists.newArrayList(withdrawService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Withdraw findOne(@PathVariable Long id) {
        return withdrawService.findOne(id);
    }

    @RequestMapping(value = "filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Withdraw> filter(
            @RequestParam(value = "code", required = false) final Long code,
            @RequestParam(value = "amountFrom", required = false) final Long amountFrom,
            @RequestParam(value = "amountTo", required = false) final Long amountTo,
            @RequestParam(value = "fromName", required = false) final String fromName,
            @RequestParam(value = "dateFrom,", required = false) final Long dateFrom,
            @RequestParam(value = "dateTo,", required = false) final Long dateTo,
            @RequestParam(value = "bankCode", required = false) final Long bankCode,
            @RequestParam(value = "bankName", required = false) final String bankName,
            @RequestParam(value = "bankBranch", required = false) final String bankBranch,
            @RequestParam(value = "bankStockFrom", required = false) final Long bankStockFrom,
            @RequestParam(value = "bankStockTo", required = false) final Long bankStockTo) {
        return withdrawSearch.search(code, amountFrom, amountTo, fromName, dateFrom, dateTo, bankCode, bankName, bankBranch, bankStockFrom, bankStockTo);
    }

}
