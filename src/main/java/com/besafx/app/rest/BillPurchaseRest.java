package com.besafx.app.rest;

import com.besafx.app.auditing.PersonAwareUserDetails;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.*;
import com.besafx.app.init.Initializer;
import com.besafx.app.search.BillPurchaseSearch;
import com.besafx.app.service.*;
import com.besafx.app.util.DateConverter;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ListIterator;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/billPurchase/")
public class BillPurchaseRest {

    private final static Logger LOG = LoggerFactory.getLogger(BillPurchaseRest.class);

    private final String FILTER_TABLE = "" +
            "**," +
            "supplier[id,contact[id,mobile,shortName]]," +
            "-billPurchaseProducts," +
            "-billPurchasePayments," +
            "-billPurchaseAttaches," +
            "person[id,contact[id,shortName]]";

    private final String FILTER_DETAILS = "" +
            "**," +
            "supplier[id,contact[id,mobile,shortName]]," +
            "billPurchaseProducts[**,-billPurchase,productPurchase[id,product[id,name]]]," +
            "billPurchasePayments[**,person[id,contact[id,shortName]],-billPurchase,-billPurchasePremium,-bankTransaction]," +
            "billPurchaseAttaches[**,-billPurchase,attach[**,person[id,contact[shortName]]]]," +
            "person[id,contact[id,shortName]]";

    @Autowired
    private BillPurchaseService billPurchaseService;

    @Autowired
    private BillPurchaseProductService billPurchaseProductService;

    @Autowired
    private BankTransactionService bankTransactionService;

    @Autowired
    private BillPurchasePaymentService billPurchasePaymentService;

    @Autowired
    private BillPurchaseSearch billPurchaseSearch;

    @Autowired
    private BankService bankService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ProductService productService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BILL_PURCHASE_CREATE')")
    @Transactional
    public String create(@RequestBody BillPurchase billPurchase) {
        BillPurchase tempBillPurchase = billPurchaseService.findByCode(billPurchase.getCode());
        if (tempBillPurchase != null) {
            throw new CustomException("عفواً، رقم الفاتورة المدخل غير متاح، حاول برقم آخر");
        }
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        billPurchase.setDate(new DateTime().toDate());
        billPurchase.setPerson(caller);
        billPurchase = billPurchaseService.save(billPurchase);

        LOG.info("ربط الأصناف المطلوبة مع الفاتورة");
        ListIterator<BillPurchaseProduct> billPurchaseProductListIterator = billPurchase.getBillPurchaseProducts().listIterator();
        while (billPurchaseProductListIterator.hasNext()) {
            BillPurchaseProduct billPurchaseProduct = billPurchaseProductListIterator.next();
            billPurchaseProduct.setDate(new DateTime().toDate());
            billPurchaseProduct.setBillPurchase(billPurchase);
            billPurchaseProductListIterator.set(billPurchaseProductService.save(billPurchaseProduct));
        }

        StringBuilder builder = new StringBuilder();
        builder.append("تم إنشاء الفاتورة بنجاح بمجموع أسعار = ");
        builder.append(billPurchase.getTotalPrice());
        builder.append("، وخصم بمقدار ");
        builder.append(billPurchase.getDiscount());
        builder.append("، وقيمة مضافة بمقدار ");
        builder.append(billPurchase.getTotalVat());
        builder.append("، وأصناف عدد " + billPurchase.getBillPurchaseProducts().size() + " صنف");
        notificationService.notifyAll(Notification
                                              .builder()
                                              .message(builder.toString())
                                              .type("success").build());

        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), billPurchase);
    }

    @PostMapping(value = "createOld", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BILL_PURCHASE_CREATE')")
    @Transactional
    public String createOld(@RequestBody String wrapperUtil) {

        LOG.info(wrapperUtil);

        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();

        JSONObject jsonObject_wrapper = new JSONObject(wrapperUtil);

        JSONObject jsonObject_billPurchase = jsonObject_wrapper.getJSONObject("obj1");

        LOG.info("إنشاء رأس الفاتورة");
        BillPurchase billPurchase = new BillPurchase();
        billPurchase.setCode(jsonObject_billPurchase.getLong("code"));
        BillPurchase tempBillPurchase = billPurchaseService.findByCode(billPurchase.getCode());
        if (tempBillPurchase != null) {
            throw new CustomException("عفواً، رقم الفاتورة المدخل غير متاح، حاول برقم آخر");
        }
        billPurchase.setDiscount(jsonObject_billPurchase.getDouble("discount"));
        billPurchase.setWrittenDate(DateConverter.parseJsonStringDate(jsonObject_billPurchase.getString("writtenDate")));
        billPurchase.setSupplier(supplierService.findOne(jsonObject_billPurchase.getJSONObject("supplier").getLong("id")));
        billPurchase.setDate(new DateTime().toDate());
        billPurchase.setPerson(caller);
        billPurchase = billPurchaseService.save(billPurchase);

        LOG.info("ربط الأصناف بالفاتورة");
        JSONArray jsonArray_productPurchases = jsonObject_wrapper.getJSONArray("obj2");
        for (int i = 0; i < jsonArray_productPurchases.length(); i++) {

            JSONObject jsonObject_productPurchase = jsonArray_productPurchases.getJSONObject(i);

            BillPurchaseProduct billPurchaseProduct = new BillPurchaseProduct();
            billPurchaseProduct.setBillPurchase(billPurchase);
            billPurchaseProduct.setProduct(productService.findOne(jsonObject_productPurchase.getJSONObject("product").getLong("id")));
            billPurchaseProduct.setDate(billPurchase.getWrittenDate());
            billPurchaseProduct.setQuantity(jsonObject_productPurchase.getDouble("quantity"));
            billPurchaseProduct.setUnitPurchasePrice(jsonObject_productPurchase.getDouble("unitPurchasePrice"));
            billPurchaseProduct.setUnitSellPrice(jsonObject_productPurchase.getDouble("unitSellPrice"));
            billPurchaseProduct.setUnitVat(jsonObject_productPurchase.getDouble("unitVat"));
            billPurchase.getBillPurchaseProducts().add(billPurchaseProductService.save(billPurchaseProduct));
        }

        LOG.info("إنشاء الدفعة المالية");
        if (jsonObject_billPurchase.getDouble("paid") > 0) {
            BillPurchasePayment billPurchasePayment = new BillPurchasePayment();
            BillPurchasePayment topBillPurchasePayment = billPurchasePaymentService.findTopByOrderByCodeDesc();
            if (topBillPurchasePayment == null) {
                billPurchasePayment.setCode(1);
            } else {
                billPurchasePayment.setCode(topBillPurchasePayment.getCode() + 1);
            }
            billPurchasePayment.setBillPurchase(billPurchase);
            billPurchasePayment.setAmount(jsonObject_billPurchase.getDouble("paid"));
            billPurchasePayment.setDate(billPurchase.getWrittenDate());

            LOG.info("عملية سحب من حساب المؤسسة بالمبالغ الواصلة");
            BankTransaction bankTransactionWithdraw = new BankTransaction();
            {
                bankTransactionWithdraw.setAmount(billPurchasePayment.getAmount());
                bankTransactionWithdraw.setDate(billPurchasePayment.getDate());
                bankTransactionWithdraw.setBank(Initializer.company.getBank());
                bankTransactionWithdraw.setTransactionType(Initializer.transactionTypeWithdraw);
                bankTransactionWithdraw.setPerson(caller);
                StringBuilder builder = new StringBuilder();
                builder.append("سحب مبلغ نقدي بقيمة ");
                builder.append(bankTransactionWithdraw.getAmount());
                builder.append("ريال سعودي، ");
                builder.append(" من حساب المؤسسة / ");
                builder.append(Initializer.company.getName());
                builder.append("، دفعة مالية بتاريخ ");
                builder.append(DateConverter.getDateInFormat(billPurchasePayment.getDate()));
                builder.append("، للفاتورة رقم / " + billPurchase.getCode());
                bankTransactionWithdraw.setNote(builder.toString());
            }

            billPurchasePayment.setBankTransaction(bankTransactionService.save(bankTransactionWithdraw));
            billPurchasePayment.setPerson(caller);
            billPurchasePayment.setNote(bankTransactionWithdraw.getNote());
            billPurchase.getBillPurchasePayments().add(billPurchasePaymentService.save(billPurchasePayment));

        }

        StringBuilder builder = new StringBuilder();
        builder.append("تم إنشاء الفاتورة بنجاح بمجموع أسعار = ");
        builder.append(billPurchase.getTotalPrice());
        builder.append("، وخصم بمقدار ");
        builder.append(billPurchase.getDiscount());
        builder.append("، وقيمة مضافة بمقدار ");
        builder.append(billPurchase.getTotalVat());
        builder.append("، وأصناف عدد " + billPurchase.getBillPurchaseProducts().size() + " صنف");
        notificationService.notifyAll(Notification
                                              .builder()
                                              .message(builder.toString())
                                              .type("success").build());

        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), billPurchase);
    }

    @DeleteMapping(value = "delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BILL_PURCHASE_DELETE')")
    @Transactional
    public void delete(@PathVariable Long id) {
        BillPurchase billPurchase = billPurchaseService.findOne(id);
        if (billPurchase != null) {
            LOG.info("حذف كل سلع الفاتورة");
            billPurchaseProductService.delete(billPurchase.getBillPurchaseProducts());
            LOG.info("حذف كل معاملات البنك لدفعات الفاتورة");
            bankTransactionService.delete(billPurchase
                                                  .getBillPurchasePayments()
                                                  .stream()
                                                  .map(BillPurchasePayment::getBankTransaction)
                                                  .collect(Collectors.toList())
                                         );
            LOG.info("حذف كل دفعات الفاتورة");
            billPurchasePaymentService.delete(billPurchase.getBillPurchasePayments());
            LOG.info("حذف الفاتورة");
            billPurchaseService.delete(id);
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message("تم حذف الفاتورة وكل ما يتعلق به من حسابات بنجاح")
                                                  .type("error").build());
        }
    }

    @GetMapping(value = "findOne/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_DETAILS),
                                       billPurchaseService.findOne(id));
    }

    @GetMapping(value = "findBySupplier/{supplierId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findBySupplier(@PathVariable(value = "supplierId") Long supplierId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_DETAILS),
                                       billPurchaseService.findBySupplier(supplierService.findOne(supplierId)));
    }

    @GetMapping(value = "filter", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String filter(
            //BillPurchase Filters
            @RequestParam(value = "codeFrom", required = false) final Integer codeFrom,
            @RequestParam(value = "codeTo", required = false) final Integer codeTo,
            @RequestParam(value = "dateFrom", required = false) final Long dateFrom,
            @RequestParam(value = "dateTo", required = false) final Long dateTo,
            //Supplier Filters
            @RequestParam(value = "supplierCodeFrom", required = false) final Integer supplierCodeFrom,
            @RequestParam(value = "supplierCodeTo", required = false) final Integer supplierCodeTo,
            @RequestParam(value = "supplierRegisterDateFrom", required = false) final Long supplierRegisterDateFrom,
            @RequestParam(value = "supplierRegisterDateTo", required = false) final Long supplierRegisterDateTo,
            @RequestParam(value = "supplierName", required = false) final String supplierName,
            @RequestParam(value = "supplierMobile", required = false) final String supplierMobile,
            @RequestParam(value = "filterCompareType", required = false) final String filterCompareType,
            Pageable pageable) {
        return SquigglyUtils.stringify(
                Squiggly.init(
                        new ObjectMapper(),
                        "**,".concat("content[").concat(FILTER_TABLE).concat("]")),
                billPurchaseSearch.filter(
                        codeFrom,
                        codeTo,
                        dateFrom,
                        dateTo,
                        supplierCodeFrom,
                        supplierCodeTo,
                        supplierRegisterDateFrom,
                        supplierRegisterDateTo,
                        supplierName,
                        supplierMobile,
                        filterCompareType,
                        pageable));
    }
}
