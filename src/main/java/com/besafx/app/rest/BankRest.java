package com.besafx.app.rest;

import com.besafx.app.entity.Bank;
import com.besafx.app.entity.projection.BankTransactionAmount;
import com.besafx.app.init.Initializer;
import com.besafx.app.service.BankService;
import com.besafx.app.service.BankTransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.ListIterator;

@RestController
@RequestMapping(value = "/api/bank/")
public class BankRest {

    private final static Logger LOG = LoggerFactory.getLogger(BankRest.class);

    private final String FILTER_TABLE = "" +
            "**," +
            "-bankTransactions";

    @Autowired
    private BankService bankService;

    @Autowired
    private BankTransactionService bankTransactionService;

    @GetMapping(value = "findAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAll() {
        List<Bank> banks = Lists.newArrayList(bankService.findAll());
        ListIterator<Bank> bankListIterator = banks.listIterator();
        while (bankListIterator.hasNext()) {
            Bank bank = bankListIterator.next();

            Double depositAmount = bankTransactionService
                    .findByTransactionTypeIn(Lists.newArrayList(
                            Initializer.transactionTypeDeposit,
                            Initializer.transactionTypeDepositTransfer),
                                             BankTransactionAmount.class)
                    .stream().mapToDouble(BankTransactionAmount::getAmount).sum();

            Double withdrawAmount = bankTransactionService
                    .findByTransactionTypeIn(Lists.newArrayList(
                            Initializer.transactionTypeWithdraw,
                            Initializer.transactionTypeWithdrawTransfer,
                            Initializer.transactionTypeExpense),
                                             BankTransactionAmount.class)
                    .stream().mapToDouble(BankTransactionAmount::getAmount).sum();

            LOG.info("مجموع الإيداعات = " + depositAmount);
            bank.setTotalDeposits(depositAmount);

            LOG.info("مجموع السحبيات = " + withdrawAmount);
            bank.setTotalWithdraws(withdrawAmount);

            bank.setBalance(depositAmount - withdrawAmount);
        }
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), banks);
    }
}
