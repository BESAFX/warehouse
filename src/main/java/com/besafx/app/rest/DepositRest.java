package com.besafx.app.rest;

import com.besafx.app.entity.Deposit;
import com.besafx.app.entity.Person;
import com.besafx.app.search.DepositSearch;
import com.besafx.app.service.BankService;
import com.besafx.app.service.DepositService;
import com.besafx.app.service.PersonService;
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
@RequestMapping(value = "/api/deposit/")
public class DepositRest {

    @Autowired
    private PersonService personService;

    @Autowired
    private BankService bankService;

    @Autowired
    private DepositService depositService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private DepositSearch depositSearch;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_DEPOSIT_CREATE')")
    public Deposit create(@RequestBody Deposit deposit, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        deposit.setLastUpdate(new Date());
        deposit.setLastPerson(person);
        deposit.getBank().setStock(deposit.getBank().getStock() + deposit.getAmount());
        deposit.setBank(bankService.save(deposit.getBank()));
        deposit = depositService.save(deposit);
        return deposit;
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_DEPOSIT_DELETE')")
    public void delete(@PathVariable Long id) {
        Deposit object = depositService.findOne(id);
        if (object != null) {
            depositService.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Deposit> findAll() {
        return Lists.newArrayList(depositService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Deposit findOne(@PathVariable Long id) {
        return depositService.findOne(id);
    }

    @RequestMapping(value = "filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Deposit> filter(
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

        return depositSearch.search(code, amountFrom, amountTo, fromName, dateFrom, dateTo, bankCode, bankName, bankBranch, bankStockFrom, bankStockTo);
    }

}
