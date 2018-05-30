package com.besafx.app.rest;

import com.besafx.app.auditing.PersonAwareUserDetails;
import com.besafx.app.entity.BankTransaction;
import com.besafx.app.entity.BillPurchase;
import com.besafx.app.entity.BillPurchasePayment;
import com.besafx.app.entity.Person;
import com.besafx.app.init.Initializer;
import com.besafx.app.search.BillPurchasePaymentSearch;
import com.besafx.app.service.BankTransactionService;
import com.besafx.app.service.BillPurchasePaymentService;
import com.besafx.app.service.BillPurchaseService;
import com.besafx.app.util.DateConverter;
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

@RestController
@RequestMapping(value = "/api/billPurchasePayment/")
public class BillPurchasePaymentRest {

    private final static Logger LOG = LoggerFactory.getLogger(BillPurchasePaymentRest.class);

    private final String FILTER_TABLE = "" +
            "**," +
            "-billPurchase," +
            "-bankTransaction," +
            "person[id,contact[id,shortName]]";

    private final String FILTER_DETAILS = "" +
            "**," +
            "billPurchase[id,code,totalPrice,supplier[id,contact[id,shortName]],customer[id,contact[id,shortName,mobile]]]," +
            "-bankTransaction," +
            "person[id,contact[id,shortName]]";

    @Autowired
    private BillPurchasePaymentService billPurchasePaymentService;

    @Autowired
    private BillPurchasePaymentSearch billPurchasePaymentSearch;

    @Autowired
    private BillPurchaseService billPurchaseService;

    @Autowired
    private BankTransactionService bankTransactionService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BILL_PURCHASE_PAYMENT_CREATE')")
    @Transactional
    public String create(@RequestBody BillPurchasePayment billPurchasePayment) {
        BillPurchasePayment topBillPurchasePayment = billPurchasePaymentService.findTopByOrderByCodeDesc();
        if (topBillPurchasePayment == null) {
            billPurchasePayment.setCode(1);
        } else {
            billPurchasePayment.setCode(topBillPurchasePayment.getCode() + 1);
        }
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        BillPurchase billPurchase = billPurchaseService.findOne(billPurchasePayment.getBillPurchase().getId());
        billPurchasePayment.setDate(new DateTime().toDate());
        billPurchasePayment.setPerson(caller);

        LOG.info("عملية سداد للدفعة");
        if (billPurchasePayment.getAmount() > 0) {
            BankTransaction bankTransaction = new BankTransaction();
            bankTransaction.setAmount(billPurchasePayment.getAmount());
            bankTransaction.setBank(Initializer.bank);
            bankTransaction.setTransactionType(Initializer.transactionTypeDepositPayment);
            bankTransaction.setDate(new DateTime().toDate());
            bankTransaction.setPerson(caller);
            StringBuilder builder = new StringBuilder();
            builder.append("إيداع مبلغ نقدي بقيمة ");
            builder.append(bankTransaction.getAmount());
            builder.append("ريال سعودي، ");
            builder.append(" للحساب البنكي / ");
            builder.append(bankTransaction.getBank().getName());
            builder.append("، دفعة مالية بتاريخ ");
            builder.append(DateConverter.getDateInFormat(billPurchasePayment.getDate()));
            builder.append("، للفاتورة رقم / " + billPurchase.getCode());
            bankTransaction.setNote(builder.toString());

            billPurchasePayment.setBankTransaction(bankTransactionService.save(bankTransaction));
            billPurchasePayment = billPurchasePaymentService.save(billPurchasePayment);

            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message(builder.toString())
                                                  .type("success").build());
        }
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), billPurchasePayment);
    }

    @DeleteMapping(value = "delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BILL_PURCHASE_PAYMENT_DELETE')")
    @Transactional
    public void delete(@PathVariable Long id) {
        BillPurchasePayment billPurchasePayment = billPurchasePaymentService.findOne(id);
        if (billPurchasePayment != null) {
            bankTransactionService.delete(billPurchasePayment.getBankTransaction());
            billPurchasePaymentService.delete(id);
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message("تم حذف الدفعة وكل ما يتعلق بها من حسابات بنجاح")
                                                  .type("error").build());
        }
    }

    @GetMapping(value = "findOne/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                                       billPurchasePaymentService.findOne(id));
    }

    @GetMapping(value = "findByBillPurchase/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByBillPurchase(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                                       billPurchasePaymentService.findByBillPurchaseId(id));
    }

    @GetMapping(value = "findByDateBetween/{startDate}/{endDate}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByDateBetween(@PathVariable(value = "startDate") Long startDate, @PathVariable(value = "endDate") Long endDate) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_DETAILS),
                                       billPurchasePaymentService.findByDateBetween(
                                               new DateTime(startDate).withTimeAtStartOfDay().toDate(),
                                               new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate()
                                                                                   ));
    }

    @GetMapping(value = "filter", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String filter(
            //BillPurchasePayment Filters
            @RequestParam(value = "dateFrom", required = false) final Long dateFrom,
            @RequestParam(value = "dateTo", required = false) final Long dateTo,
            //BillPurchase Filters
            @RequestParam(value = "billPurchaseCodeFrom", required = false) final Integer billPurchaseCodeFrom,
            @RequestParam(value = "billPurchaseCodeTo", required = false) final Integer billPurchaseCodeTo,
            @RequestParam(value = "billPurchaseDateFrom", required = false) final Long billPurchaseDateFrom,
            @RequestParam(value = "billPurchaseDateTo", required = false) final Long billPurchaseDateTo,
            //Customer Filters
            @RequestParam(value = "customerName", required = false) final String customerName,
            @RequestParam(value = "customerMobile", required = false) final String customerMobile,
            //Supplier Filters
            @RequestParam(value = "supplierName", required = false) final String supplierName,
            @RequestParam(value = "supplierMobile", required = false) final String supplierMobile,
            @RequestParam(value = "filterCompareType", required = false) final String filterCompareType,
            Pageable pageable) {
        return SquigglyUtils.stringify(
                Squiggly.init(
                        new ObjectMapper(),
                        "**,".concat("content[").concat(FILTER_DETAILS).concat("]")),
                billPurchasePaymentSearch.filter(
                        dateFrom,
                        dateTo,
                        billPurchaseCodeFrom,
                        billPurchaseCodeTo,
                        billPurchaseDateFrom,
                        billPurchaseDateTo,
                        customerName,
                        customerMobile,
                        supplierName,
                        supplierMobile,
                        filterCompareType,
                        pageable));
    }
}
