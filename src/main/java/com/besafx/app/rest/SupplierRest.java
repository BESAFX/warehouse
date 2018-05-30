package com.besafx.app.rest;

import com.besafx.app.async.TransactionalService;
import com.besafx.app.auditing.PersonAwareUserDetails;
import com.besafx.app.config.CustomException;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/supplier/")
public class SupplierRest {

    private final static Logger LOG = LoggerFactory.getLogger(SupplierRest.class);

    private final String FILTER_TABLE = "" +
            "**," +
            "-productPurchases," +
            "-billPurchases," +
            "-bankTransactions," +
            "supplier[id]";

    private final String FILTER_DETAILS = "" +
            "**," +
            "productPurchases[**,product[id,name],-bankTransaction,-supplier,-billPurchaseProducts,person[id,contact[id,shortName]]]," +
            "-billPurchases," +
            "-bankTransactions," +
            "supplier[id]";

    private final String FILTER_COMBO = "" +
            "id," +
            "code," +
            "contact[id,shortName,mobile]";

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private SupplierSearch supplierSearch;

    @Autowired
    private BillPurchaseService billPurchaseService;

    @Autowired
    private BankTransactionService bankTransactionService;

    @Autowired
    private BillPurchaseProductService billPurchaseProductService;

    @Autowired
    private BillPurchasePaymentService billPurchasePaymentService;

    @Autowired
    private ContractPremiumService contractPremiumService;

    @Autowired
    private ProductPurchaseService productPurchaseService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private TransactionalService transactionalService;

    @Autowired
    private NotificationService notificationService;

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
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        supplier.setSupplier(caller.getCompany().getSupplier());
        supplier.setRegisterDate(new DateTime().toDate());
        supplier.setEnabled(true);
        supplier.setContact(billPurchaseService.save(supplier.getContact()));
        supplier = supplierService.save(supplier);
        notificationService.notifyAll(Notification
                                              .builder()
                                              .message("تم انشاء حساب مورد جديد بنجاح")
                                              .type("success").build());
        LOG.info("إنشاء الرصيد الافتتاحي وعمل عملية إيداع بالمبلغ");
        if (openCash > 0) {
            BankTransaction bankTransaction = new BankTransaction();
            bankTransaction.setAmount(openCash);
            bankTransaction.setBank(Initializer.bank);
            bankTransaction.setSupplier(supplier);
            bankTransaction.setTransactionType(Initializer.transactionTypeDeposit);
            bankTransaction.setDate(new DateTime().toDate());
            bankTransaction.setPerson(caller);
            StringBuilder builder = new StringBuilder();
            builder.append("إيداع مبلغ نقدي بقيمة ");
            builder.append(openCash);
            builder.append("ريال سعودي، ");
            builder.append(" لـ / ");
            builder.append(supplier.getShortName());
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
            supplier.setContact(billPurchaseService.save(supplier.getContact()));
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

            LOG.info("رفض العملية فى حال كان هو المورد الرئيسي");
            if (Initializer.company.getSupplier().getId().equals(supplier.getId())) {
                throw new CustomException("لا يمكن حذف المورد الرئيسي للبرنامج");
            }

            LOG.info("حذف كل سلع العقود");
            billPurchaseProductService.delete(supplier.getBillPurchases()
                                                      .stream()
                                                      .flatMap(contract -> contract.getContractProducts().stream())
                                                      .collect(Collectors.toList()));

            LOG.info("حذف كل معاملات البنك لدفعات العقود");
            bankTransactionService.delete(
                    supplier.getBillPurchases()
                          .stream()
                          .flatMap(contract -> contract.getContractPayments().stream())
                          .map(BillPurchasePayment::getBankTransaction)
                          .collect(Collectors.toList()));

            LOG.info("حذف كل دفعات العقود");
            billPurchasePaymentService.delete(supplier.getBillPurchases()
                                                      .stream()
                                                      .flatMap(contract -> contract.getContractPayments().stream())
                                                      .collect(Collectors.toList()));

            LOG.info("حذف كل أقساط العقود");
            contractPremiumService.delete(supplier.getBillPurchases()
                                                .stream()
                                                .flatMap(contract -> contract.getContractPremiums().stream())
                                                .collect(Collectors.toList()));

            LOG.info("حذف العقود");
            contractService.delete(supplier.getBillPurchases());

            LOG.info("حذف حركات الشراء لهذا المورد");
            bankTransactionService.delete(supplier.getProductPurchases()
                                                .stream()
                                                .map(ProductPurchase::getBankTransaction)
                                                .collect(Collectors.toList()));

            LOG.info("حذف المشتريات لهذا المورد");
            productPurchaseService.delete(supplier.getProductPurchases());

            LOG.info("تفريغ كل المعاملات المالية لهذا المورد");
            transactionalService.setBankTransactionsSupplierToNull(supplier);

            LOG.info("حذف بيانات الاتصال");
            billPurchaseService.delete(supplier.getContact());

            LOG.info("حذف المورد");
            supplierService.delete(supplier);

            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message("تم حذف المورد وكل ما يتعلق به من عقود وحسابات بنجاح")
                                                  .type("error").build());
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
    public String findSupplierBalance(@PathVariable(value = "id") Long id) {
        Supplier supplier = supplierService.findOne(id);

        Double depositAmount = bankTransactionService
                .findBySupplierAndTransactionTypeIn(supplier,
                                                  Lists.newArrayList(
                                                          Initializer.transactionTypeDeposit,
                                                          Initializer.transactionTypeDepositPayment,
                                                          Initializer.transactionTypeDepositTransfer),
                                                  BankTransactionAmount.class)
                .stream().mapToDouble(BankTransactionAmount::getAmount).sum();

        Double withdrawAmount = bankTransactionService
                .findBySupplierAndTransactionTypeIn(supplier,
                                                  Lists.newArrayList(
                                                          Initializer.transactionTypeWithdraw,
                                                          Initializer.transactionTypeWithdrawCash,
                                                          Initializer.transactionTypeWithdrawPurchase,
                                                          Initializer.transactionTypeWithdrawTransfer),
                                                  BankTransactionAmount.class)
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
    public String findAllSupplierBalance() {
        List<Supplier> suppliers = Lists.newArrayList(supplierService.findAll());
        ListIterator<Supplier> bankListIterator = suppliers.listIterator();
        while (bankListIterator.hasNext()) {

            Supplier supplier = bankListIterator.next();

            Double depositAmount = bankTransactionService
                    .findBySupplierAndTransactionTypeIn(supplier,
                                                      Lists.newArrayList(
                                                              Initializer.transactionTypeDeposit,
                                                              Initializer.transactionTypeDepositPayment,
                                                              Initializer.transactionTypeDepositTransfer),
                                                      BankTransactionAmount.class)
                    .stream().mapToDouble(BankTransactionAmount::getAmount).sum();

            Double withdrawAmount = bankTransactionService
                    .findBySupplierAndTransactionTypeIn(supplier,
                                                      Lists.newArrayList(
                                                              Initializer.transactionTypeWithdraw,
                                                              Initializer.transactionTypeWithdrawCash,
                                                              Initializer.transactionTypeWithdrawPurchase,
                                                              Initializer.transactionTypeWithdrawTransfer),
                                                      BankTransactionAmount.class)
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
