package com.besafx.app.rest;

import com.besafx.app.auditing.PersonAwareUserDetails;
import com.besafx.app.config.CustomException;
import com.besafx.app.config.SendSMS;
import com.besafx.app.entity.BankTransaction;
import com.besafx.app.entity.BillPurchasePayment;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Supplier;
import com.besafx.app.entity.projection.BankTransactionAmount;
import com.besafx.app.init.Initializer;
import com.besafx.app.search.SupplierSearch;
import com.besafx.app.service.*;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/supplier/")
public class SupplierRest {

    private final static Logger LOG = LoggerFactory.getLogger(SupplierRest.class);

    private final String FILTER_TABLE = "" +
            "**," +
            "-billPurchases";

    private final String FILTER_DETAILS = "" +
            "**," +
            "-billPurchases";

    private final String FILTER_COMBO = "" +
            "id," +
            "code," +
            "contact[id,shortName,mobile]";

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private SupplierSearch supplierSearch;

    @Autowired
    private ContactService contactService;

    @Autowired
    private BankService bankService;

    @Autowired
    private BillPurchaseService billPurchaseService;

    @Autowired
    private BankTransactionService bankTransactionService;

    @Autowired
    private BillPurchaseProductService billPurchaseProductService;

    @Autowired
    private BillPurchasePaymentService billPurchasePaymentService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SendSMS sendSMS;

    @PostMapping(value = "create/{openCash}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_SUPPLIER_CREATE')")
    @Transactional
    public String create(@PathVariable(value = "openCash") Double openCash, @RequestBody Supplier supplier) {
        Supplier topSupplier = supplierService.findTopByOrderByCodeDesc();
        if (topSupplier == null) {
            supplier.setCode(1);
        } else {
            supplier.setCode(topSupplier.getCode() + 1);
        }
        supplier.setRegisterDate(new DateTime().toDate());
        supplier.setEnabled(true);
        supplier.setContact(contactService.save(supplier.getContact()));
        supplier.setBank(bankService.save(supplier.getBank()));
        supplier = supplierService.save(supplier);

        notificationService.notifyAll(Notification
                                              .builder()
                                              .message("تم انشاء حساب مورد جديد بنجاح")
                                              .type("success").build());

        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        LOG.info("إنشاء الرصيد الافتتاحي وعمل عملية إيداع بالمبلغ");
        if (openCash > 0) {
            BankTransaction bankTransaction = new BankTransaction();
            bankTransaction.setAmount(openCash);
            bankTransaction.setDate(new DateTime().toDate());
            bankTransaction.setBank(supplier.getBank());
            bankTransaction.setTransactionType(Initializer.transactionTypeDeposit);
            bankTransaction.setPerson(caller);
            StringBuilder builder = new StringBuilder();
            builder.append("إيداع مبلغ نقدي بقيمة ");
            builder.append(openCash);
            builder.append("ريال سعودي، ");
            builder.append(" للمورد / ");
            builder.append(supplier.getContact().getShortName());
            builder.append("، رصيد افتتاحي");
            bankTransaction.setNote(builder.toString());

            bankTransactionService.save(bankTransaction);

            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message(builder.toString())
                                                  .type("success").build());
        }
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), supplier);
    }

    @PutMapping(value = "update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_SUPPLIER_UPDATE')")
    @Transactional
    public String update(@RequestBody Supplier supplier) {
        if (supplierService.findByCodeAndIdIsNot(supplier.getCode(), supplier.getId()) != null) {
            throw new CustomException("هذا الكود مستخدم سابقاً، فضلاً قم بتغير الكود.");
        }
        Supplier object = supplierService.findOne(supplier.getId());
        if (object != null) {
            supplier.setContact(contactService.save(supplier.getContact()));
            supplier.setBank(bankService.save(supplier.getBank()));
            supplier = supplierService.save(supplier);
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message("تم تعديل بيانات المورد بنجاح")
                                                  .type("success").build());
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), supplier);
        } else {
            return null;
        }
    }

    @DeleteMapping(value = "delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_SUPPLIER_DELETE')")
    @Transactional
    public void delete(@PathVariable Long id) {
        Supplier supplier = supplierService.findOne(id);
        if (supplier != null) {

            LOG.info("حذف كل سلع فواتير الشراء");
            billPurchaseProductService.delete(supplier.getBillPurchases()
                                                      .stream()
                                                      .flatMap(contract -> contract.getBillPurchaseProducts().stream())
                                                      .collect(Collectors.toList()));

            LOG.info("حذف كل دفعات فواتير الشراء");
            billPurchasePaymentService.delete(supplier.getBillPurchases()
                                                      .stream()
                                                      .flatMap(contract -> contract.getBillPurchasePayments().stream())
                                                      .collect(Collectors.toList()));

            LOG.info("حذف كل معاملات البنك لدفعات فواتير الشراء");
            bankTransactionService.delete(
                    supplier.getBillPurchases()
                            .stream()
                            .flatMap(contract -> contract.getBillPurchasePayments().stream())
                            .map(BillPurchasePayment::getBankTransaction)
                            .collect(Collectors.toList()));

            LOG.info("حذف فواتير الشراء");
            billPurchaseService.delete(supplier.getBillPurchases());

            LOG.info("حذف بيانات الاتصال");
            contactService.delete(supplier.getContact());

            LOG.info("حذف كل المعاملات المالية للحساب البنكي");
            bankTransactionService.delete(supplier.getBank().getBankTransactions());

            LOG.info("حذف الحساب البنكي");
            bankService.delete(supplier.getBank());

            LOG.info("حذف المورد");
            supplierService.delete(supplier);

            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message("تم حذف المورد وكل ما يتعلق به من فواتير وحسابات ومعاملات مالية بنجاح")
                                                  .type("error").build());
        }
    }

    @PostMapping(value = "sendMessage/{supplierIds}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_SMS_SEND')")
    @Transactional
    public void sendMessage(@RequestBody String content, @PathVariable List<Long> supplierIds) throws Exception {
        ListIterator<Long> listIterator = supplierIds.listIterator();
        while (listIterator.hasNext()){
            Long id = listIterator.next();
            Supplier supplier = supplierService.findOne(id);
            String message = content.replaceAll("#remain#", supplier.getBillsRemain().toString());
            Future<String> task = sendSMS.sendMessage(supplier.getContact().getMobile(), message);
            String taskResult = task.get();
            StringBuilder builder = new StringBuilder();
            builder.append("الرقم / ");
            builder.append(supplier.getContact().getMobile());
            builder.append("<br/>");
            builder.append(" محتوى الرسالة : ");
            builder.append(message);
            builder.append("<br/>");
            builder.append(" ، نتيجة الإرسال: ");
            builder.append(taskResult);
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message(builder.toString())
                                                  .type("information").build());
        }
    }

    @GetMapping(value = "findOne/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_DETAILS),
                                       supplierService.findOne(id));
    }

    @GetMapping(value = "findAllCombo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAllCombo() {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_COMBO),
                                       supplierService.findAll(new Sort(Sort.Direction.ASC, "contact.shortName")));
    }

    @GetMapping(value = "findSupplierBalance/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Transactional
    public String findSupplierBalance(@PathVariable(value = "id") Long id) {
        Supplier supplier = supplierService.findOne(id);

        Double depositAmount = bankTransactionService
                .findByBankAndTransactionTypeIn(supplier.getBank(), Lists.newArrayList(
                        Initializer.transactionTypeDeposit,
                        Initializer.transactionTypeDepositTransfer), BankTransactionAmount.class)
                .stream().mapToDouble(BankTransactionAmount::getAmount).sum();

        Double withdrawAmount = bankTransactionService
                .findByBankAndTransactionTypeIn(supplier.getBank(), Lists.newArrayList(
                        Initializer.transactionTypeWithdraw,
                        Initializer.transactionTypeWithdrawTransfer,
                        Initializer.transactionTypeExpense), BankTransactionAmount.class)
                .stream().mapToDouble(BankTransactionAmount::getAmount).sum();

        LOG.info("مجموع الإيداعات = " + depositAmount);
        supplier.setTotalDeposits(depositAmount);

        LOG.info("مجموع السحبيات = " + withdrawAmount);
        supplier.setTotalWithdraws(withdrawAmount);

        supplier.setBalance(depositAmount - withdrawAmount);

        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), supplier);
    }

    @GetMapping(value = "findAllSupplierBalance", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Transactional
    public String findAllSupplierBalance() {
        List<Supplier> suppliers = Lists.newArrayList(supplierService.findAll());
        ListIterator<Supplier> bankListIterator = suppliers.listIterator();
        while (bankListIterator.hasNext()) {

            Supplier supplier = bankListIterator.next();

            Double depositAmount = bankTransactionService
                    .findByBankAndTransactionTypeIn(supplier.getBank(), Lists.newArrayList(
                            Initializer.transactionTypeDeposit,
                            Initializer.transactionTypeDepositTransfer), BankTransactionAmount.class)
                    .stream().mapToDouble(BankTransactionAmount::getAmount).sum();

            Double withdrawAmount = bankTransactionService
                    .findByBankAndTransactionTypeIn(supplier.getBank(), Lists.newArrayList(
                            Initializer.transactionTypeWithdraw,
                            Initializer.transactionTypeWithdrawTransfer,
                            Initializer.transactionTypeExpense), BankTransactionAmount.class)
                    .stream().mapToDouble(BankTransactionAmount::getAmount).sum();

            LOG.info("مجموع الإيداعات = " + depositAmount);
            supplier.setTotalDeposits(depositAmount);

            LOG.info("مجموع السحبيات = " + withdrawAmount);
            supplier.setTotalWithdraws(withdrawAmount);

            supplier.setBalance(depositAmount - withdrawAmount);
        }
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), suppliers);
    }

    @GetMapping(value = "filter", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String filter(
            @RequestParam(value = "codeFrom", required = false) final Integer codeFrom,
            @RequestParam(value = "codeTo", required = false) final Integer codeTo,
            @RequestParam(value = "registerDateFrom", required = false) final Long registerDateFrom,
            @RequestParam(value = "registerDateTo", required = false) final Long registerDateTo,
            @RequestParam(value = "name", required = false) final String name,
            @RequestParam(value = "mobile", required = false) final String mobile,
            @RequestParam(value = "phone", required = false) final String phone,
            @RequestParam(value = "nationality", required = false) final String nationality,
            @RequestParam(value = "identityNumber", required = false) final String identityNumber,
            @RequestParam(value = "qualification", required = false) final String qualification,
            @RequestParam(value = "filterCompareType", required = false) final String filterCompareType,
            Pageable pageable) {
        return SquigglyUtils.stringify(
                Squiggly.init(
                        new ObjectMapper(),
                        "**,".concat("content[").concat(FILTER_TABLE).concat("]")),
                supplierSearch.filter(
                        codeFrom,
                        codeTo,
                        registerDateFrom,
                        registerDateTo,
                        name,
                        mobile,
                        phone,
                        nationality,
                        identityNumber,
                        qualification,
                        filterCompareType,
                        pageable));
    }
}
