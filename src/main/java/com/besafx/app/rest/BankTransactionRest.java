package com.besafx.app.rest;

import com.besafx.app.auditing.PersonAwareUserDetails;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.BankTransaction;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Seller;
import com.besafx.app.init.Initializer;
import com.besafx.app.search.BankTransactionSearch;
import com.besafx.app.service.BankTransactionService;
import com.besafx.app.service.SellerService;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Date;

@RestController
@RequestMapping(value = "/api/bankTransaction/")
public class BankTransactionRest {

    private final static Logger LOG = LoggerFactory.getLogger(BankTransactionRest.class);

    private final String FILTER_TABLE = "" +
            "**," +
            "bank[id,name]," +
            "seller[id,contact[id,shortName]]," +
            "transactionType[id,name]," +
            "person[id,contact[id,shortName]]";

    @Autowired
    private BankTransactionService bankTransactionService;

    @Autowired
    private BankTransactionSearch bankTransactionSearch;

    @Autowired
    private SellerService sellerService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping(value = "createDeposit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_DEPOSIT_CREATE')")
    @Transactional
    public String createDeposit(
            @RequestBody BankTransaction bankTransaction) {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        bankTransaction.setBank(Initializer.bank);
        bankTransaction.setTransactionType(Initializer.transactionTypeDeposit);
        bankTransaction.setDate(new DateTime().toDate());
        bankTransaction.setPerson(caller);
        StringBuilder builder = new StringBuilder();
        builder.append("إيداع مبلغ نقدي بقيمة ");
        builder.append(bankTransaction.getAmount());
        builder.append("ريال سعودي، ");
        builder.append(" لـ / ");
        builder.append(bankTransaction.getSeller().getContact().getShortName());
        builder.append("، " + bankTransaction.getNote());
        bankTransaction.setNote(builder.toString());
        bankTransaction = bankTransactionService.save(bankTransaction);
        notificationService.notifyAll(Notification
                                              .builder()
                                              .message(builder.toString())
                                              .type("success").build());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), bankTransaction);
    }

    @PostMapping(value = "createWithdraw", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_WITHDRAW_CREATE')")
    @Transactional
    public String createWithdraw(
            @RequestBody BankTransaction bankTransaction) {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        bankTransaction.setBank(Initializer.bank);
        bankTransaction.setTransactionType(Initializer.transactionTypeWithdraw);
        bankTransaction.setDate(new DateTime().toDate());
        bankTransaction.setPerson(caller);
        StringBuilder builder = new StringBuilder();
        builder.append("سحب مبلغ نقدي بقيمة ");
        builder.append(bankTransaction.getAmount());
        builder.append("ريال سعودي، ");
        builder.append(" من / ");
        builder.append(bankTransaction.getSeller().getContact().getShortName());
        builder.append("، " + bankTransaction.getNote());
        bankTransaction.setNote(builder.toString());
        bankTransaction = bankTransactionService.save(bankTransaction);
        notificationService.notifyAll(Notification
                                              .builder()
                                              .message(builder.toString())
                                              .type("success").build());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), bankTransaction);
    }

    @GetMapping(value = "createTransfer/{amount}/{fromSellerId}/{toSellerId}/{note}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TRANSFER_CREATE')")
    @Transactional
    public void createTransfer(
            @PathVariable(value = "amount") Double amount,
            @PathVariable(value = "fromSellerId") Long fromSellerId,
            @PathVariable(value = "toSellerId") Long toSellerId,
            @PathVariable(value = "note") String note) {
        Seller fromSeller = sellerService.findOne(fromSellerId);
        Seller toSeller = sellerService.findOne(toSellerId);
        if (fromSeller == null || toSeller == null) {
            throw new CustomException("فضلا تأكد من بيانات المستثمرين");
        }
        if (amount <= 0) {
            throw new CustomException("لا يمكن قبول تحويل القيمة الصفرية");
        }

        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();

        {
            LOG.info("القيام بعملية السحب أولا من المرسل");
            BankTransaction bankTransactionWithdraw = new BankTransaction();
            bankTransactionWithdraw.setBank(Initializer.bank);
            bankTransactionWithdraw.setSeller(fromSeller);
            bankTransactionWithdraw.setAmount(amount);
            bankTransactionWithdraw.setTransactionType(Initializer.transactionTypeWithdrawTransfer);
            bankTransactionWithdraw.setDate(new DateTime().toDate());
            bankTransactionWithdraw.setPerson(caller);
            StringBuilder builder = new StringBuilder();
            builder.append("سحب مبلغ نقدي بقيمة ");
            builder.append(bankTransactionWithdraw.getAmount());
            builder.append("ريال سعودي، ");
            builder.append(" من / ");
            builder.append(bankTransactionWithdraw.getSeller().getContact().getShortName());
            builder.append("، عملية تحويل إلى " + toSeller.getContact().getShortName());
            builder.append("، " + note);
            bankTransactionWithdraw.setNote(builder.toString());
            bankTransactionService.save(bankTransactionWithdraw);
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message(builder.toString())
                                                  .type("success").build());
        }

        {
            LOG.info("القيام بعملية الإيداع ثانيا إلى المرسل إليه");
            BankTransaction bankTransactionDeposit = new BankTransaction();
            bankTransactionDeposit.setBank(Initializer.bank);
            bankTransactionDeposit.setSeller(toSeller);
            bankTransactionDeposit.setAmount(amount);
            bankTransactionDeposit.setTransactionType(Initializer.transactionTypeDepositTransfer);
            bankTransactionDeposit.setDate(new DateTime().toDate());
            bankTransactionDeposit.setPerson(caller);
            StringBuilder builder = new StringBuilder();
            builder.append("إيداع مبلغ نقدي بقيمة ");
            builder.append(bankTransactionDeposit.getAmount());
            builder.append("ريال سعودي، ");
            builder.append(" لـ / ");
            builder.append(bankTransactionDeposit.getSeller().getContact().getShortName());
            builder.append("، عملية تحويل من " + fromSeller.getContact().getShortName());
            builder.append("، " + note);
            bankTransactionDeposit.setNote(builder.toString());
            bankTransactionService.save(bankTransactionDeposit);
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message(builder.toString())
                                                  .type("success").build());
        }

    }

    @GetMapping(value = "createWithdrawCash/{amount}/{note}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_WITHDRAW_CASH_CREATE')")
    @Transactional
    public String createWithdrawCash(
            @PathVariable(value = "amount") Double amount,
            @PathVariable(value = "note") String note) {
        if (amount <= 0) {
            throw new CustomException("لا يمكن قبول القيمة الصفرية");
        }
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setBank(Initializer.bank);
        bankTransaction.setSeller(caller.getCompany().getSeller());
        bankTransaction.setAmount(amount);
        bankTransaction.setTransactionType(Initializer.transactionTypeWithdrawCash);
        bankTransaction.setDate(new DateTime().toDate());
        bankTransaction.setPerson(caller);
        StringBuilder builder = new StringBuilder();
        builder.append("سحب مبلغ نقدي بقيمة ");
        builder.append(bankTransaction.getAmount());
        builder.append("ريال سعودي، ");
        builder.append(" من / ");
        builder.append(bankTransaction.getSeller().getContact().getShortName());
        builder.append("، " + note);
        bankTransaction.setNote(builder.toString());
        bankTransaction = bankTransactionService.save(bankTransaction);
        notificationService.notifyAll(Notification
                                              .builder()
                                              .message(builder.toString())
                                              .type("success").build());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), bankTransaction);
    }

    @GetMapping(value = "findOne/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                                       bankTransactionService.findOne(id));
    }

    @GetMapping(value = "findMyBankTransactions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findMyBankTransactions() {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                                       bankTransactionService.findBySeller(caller.getCompany().getSeller()));
    }

    @GetMapping(value = "filter", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String filter(
            @RequestParam(value = "codeFrom", required = false) final Integer codeFrom,
            @RequestParam(value = "codeTo", required = false) final Integer codeTo,
            @RequestParam(value = "dateFrom", required = false) final Date dateFrom,
            @RequestParam(value = "dateTo", required = false) final Date dateTo,
            @RequestParam(value = "sellerName", required = false) final String sellerName,
            @RequestParam(value = "sellerMobile", required = false) final String sellerMobile,
            @RequestParam(value = "sellerIdentityNumber", required = false) final String sellerIdentityNumber,
            Pageable pageable) {
        return SquigglyUtils.stringify(
                Squiggly.init(
                        new ObjectMapper(),
                        "**,".concat("content[").concat(FILTER_TABLE).concat("]")),
                bankTransactionSearch.filter(
                        codeFrom,
                        codeTo,
                        dateFrom,
                        dateTo,
                        sellerName,
                        sellerMobile,
                        sellerIdentityNumber,
                        pageable));
    }
}
