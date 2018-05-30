package com.besafx.app.rest;

import com.besafx.app.auditing.PersonAwareUserDetails;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.*;
import com.besafx.app.init.Initializer;
import com.besafx.app.search.ContractSearch;
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
@RequestMapping(value = "/api/contract/")
public class ContractRest {

    private final static Logger LOG = LoggerFactory.getLogger(ContractRest.class);

    private final String FILTER_TABLE = "" +
            "**," +
            "customer[id,contact[id,mobile,shortName]]," +
            "supplier[id,contact[id,mobile,shortName]]," +
            "-sponsor1," +
            "-sponsor2," +
            "-billPurchaseProducts," +
            "-contractPremiums," +
            "-contractPayments," +
            "-contractAttaches," +
            "person[id,contact[id,shortName]]";

    private final String FILTER_DETAILS = "" +
            "**," +
            "supplier[id,contact[id,mobile,shortName]]," +
            "customer[id,contact[id,mobile,identityNumber,address,shortName]]," +
            "sponsor1[id,contact[id,mobile,shortName]]," +
            "sponsor2[id,contact[id,mobile,shortName]]," +
            "billPurchaseProducts[**,-billPurchase,productPurchase[id,product[id,name]]]," +
            "contractPremiums[**,-billPurchase,-contractPayments]," +
            "contractPayments[**,person[id,contact[id,shortName]],-billPurchase,-contractPremium,-bankTransaction]," +
            "contractAttaches[**,-billPurchase,attach[**,person[id,contact[shortName]]]]," +
            "person[id,contact[id,shortName]]";

    @Autowired
    private ContractService contractService;

    @Autowired
    private BillPurchaseProductService billPurchaseProductService;

    @Autowired
    private BankTransactionService bankTransactionService;

    @Autowired
    private BillPurchasePaymentService billPurchasePaymentService;

    @Autowired
    private ContractPremiumService contractPremiumService;

    @Autowired
    private ContractSearch contractSearch;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductPurchaseService productPurchaseService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_CONTRACT_CREATE')")
    @Transactional
    public String create(@RequestBody BillPurchase billPurchase) {
        BillPurchase tempBillPurchase = contractService.findByCode(billPurchase.getCode());
        if (tempBillPurchase != null) {
            throw new CustomException("عفواً، رقم العقد المدخل غير متاح، حاول برقم آخر");
        }
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        billPurchase.setPerson(caller);
        billPurchase.setDate(new DateTime().toDate());
        billPurchase = contractService.save(billPurchase);
        LOG.info("ربط الأصناف المطلوبة مع العقد");
        ListIterator<BillPurchaseProduct> contractProductListIterator = billPurchase.getContractProducts().listIterator();
        while (contractProductListIterator.hasNext()) {
            BillPurchaseProduct billPurchaseProduct = contractProductListIterator.next();
            billPurchaseProduct.setBillPurchase(billPurchase);
            contractProductListIterator.set(billPurchaseProductService.save(billPurchaseProduct));
        }
        LOG.info("ربط الأقساط مع العقد");
        ListIterator<ContractPremium> contractPremiumListIterator = billPurchase.getContractPremiums().listIterator();
        while (contractPremiumListIterator.hasNext()) {
            ContractPremium contractPremium = contractPremiumListIterator.next();
            contractPremium.setContract(billPurchase);
            contractPremiumListIterator.set(contractPremiumService.save(contractPremium));
        }
        StringBuilder builder = new StringBuilder();
        builder.append("تم إنشاء العقد بنجاح بمجموع أسعار = ");
        builder.append(billPurchase.getTotalPrice());
        builder.append("، وأصناف عدد " + billPurchase.getContractProducts().size() + " صنف");
        builder.append("، تسدد على " + billPurchase.getContractPremiums().size() + " قسط");
        notificationService.notifyAll(Notification
                                              .builder()
                                              .message(builder.toString())
                                              .type("success").build());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), billPurchase);
    }

    @PostMapping(value = "createOld", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_CONTRACT_CREATE')")
    @Transactional
    public String createOld(@RequestBody String wrapperUtil) {

        LOG.info(wrapperUtil);

        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();

        JSONObject jsonObject_wrapper = new JSONObject(wrapperUtil);

        JSONObject jsonObject_contract = jsonObject_wrapper.getJSONObject("obj1");

        LOG.info("إنشاء العقد");
        BillPurchase billPurchase = new BillPurchase();
        billPurchase.setCode(jsonObject_contract.getLong("code"));
        BillPurchase tempBillPurchase = contractService.findByCode(billPurchase.getCode());
        if (tempBillPurchase != null) {
            throw new CustomException("عفواً، رقم العقد المدخل غير متاح، حاول برقم آخر");
        }
        billPurchase.setDiscount(jsonObject_contract.getDouble("discount"));
        billPurchase.setPaperFees(jsonObject_contract.has("paperFees") ? jsonObject_contract.getDouble("paperFees") : null);
        billPurchase.setCommissionFees(jsonObject_contract.has("commissionFees") ? jsonObject_contract.getDouble("commissionFees") : null);
        billPurchase.setLawFees(jsonObject_contract.has("lawFees") ? jsonObject_contract.getDouble("lawFees") : null);
        billPurchase.setWrittenDate(DateConverter.parseJsonStringDate(jsonObject_contract.getString("writtenDate")));
        billPurchase.setCustomer(customerService.findOne(jsonObject_contract.getJSONObject("customer").getLong("id")));
        billPurchase.setSupplier(supplierService.findOne(jsonObject_contract.getJSONObject("supplier").getLong("id")));
        billPurchase.setSponsor1(jsonObject_contract.has("sponsor1") ? customerService.findOne(jsonObject_contract.getJSONObject("sponsor1").getLong("id")) : null);
        billPurchase.setSponsor2(jsonObject_contract.has("sponsor2") ? customerService.findOne(jsonObject_contract.getJSONObject("sponsor2").getLong("id")) : null);
        billPurchase.setPerson(caller);
        billPurchase = contractService.save(billPurchase);

        if(jsonObject_contract.getDouble("paid") > 0){

            LOG.info("إنشاء القسط");
            ContractPremium contractPremium = new ContractPremium();
            contractPremium.setContract(billPurchase);
            contractPremium.setAmount(jsonObject_contract.getDouble("paid"));
            contractPremium.setDueDate(billPurchase.getWrittenDate());
            contractPremium = contractPremiumService.save(contractPremium);
            billPurchase.getContractPremiums().add(contractPremium);

            LOG.info("إنشاء الدفعة المالية");
            BillPurchasePayment billPurchasePayment = new BillPurchasePayment();
            BillPurchasePayment topBillPurchasePayment = billPurchasePaymentService.findTopByOrderByCodeDesc();
            if (topBillPurchasePayment == null) {
                billPurchasePayment.setCode(1);
            } else {
                billPurchasePayment.setCode(topBillPurchasePayment.getCode() + 1);
            }
            billPurchasePayment.setBillPurchase(billPurchase);
            billPurchasePayment.setContractPremium(contractPremium);
            billPurchasePayment.setAmount(contractPremium.getAmount());
            billPurchasePayment.setDate(contractPremium.getDueDate());

            LOG.info("عملية سداد للدفعة");
            BankTransaction bankTransaction = new BankTransaction();
            {
                bankTransaction.setAmount(billPurchasePayment.getAmount());
                bankTransaction.setBank(Initializer.bank);
                bankTransaction.setSupplier(billPurchase.getSupplier());
                bankTransaction.setTransactionType(Initializer.transactionTypeDepositPayment);
                bankTransaction.setDate(billPurchasePayment.getDate());
                bankTransaction.setPerson(caller);
                StringBuilder builder = new StringBuilder();
                builder.append("إيداع مبلغ نقدي بقيمة ");
                builder.append(bankTransaction.getAmount());
                builder.append("ريال سعودي، ");
                builder.append(" لـ / ");
                builder.append(bankTransaction.getSupplier().getContact().getShortName());
                builder.append("، قسط مستحق بتاريخ ");
                builder.append(DateConverter.getDateInFormat(billPurchasePayment.getContractPremium().getDueDate()));
                builder.append("، للعقد رقم / " + billPurchase.getCode());
                bankTransaction.setNote(builder.toString());
            }

            billPurchasePayment.setBankTransaction(bankTransactionService.save(bankTransaction));
            billPurchasePayment.setPerson(caller);
            billPurchasePayment.setNote(bankTransaction.getNote());
            billPurchase.getContractPayments().add(billPurchasePaymentService.save(billPurchasePayment));

        }

        LOG.info("شراء الأصناف");
        JSONArray jsonArray_productPurchases = jsonObject_wrapper.getJSONArray("obj2");
        for(int i = 0; i < jsonArray_productPurchases.length(); i++){

            JSONObject jsonObject_productPurchase = jsonArray_productPurchases.getJSONObject(i);

            ProductPurchase productPurchase = new ProductPurchase();
            ProductPurchase topProductPurchase = productPurchaseService.findTopByOrderByCodeDesc();
            if (topProductPurchase == null) {
                productPurchase.setCode(1);
            } else {
                productPurchase.setCode(topProductPurchase.getCode() + 1);
            }
            productPurchase.setDate(billPurchase.getWrittenDate());
            productPurchase.setSupplier(billPurchase.getSupplier());
            productPurchase.setProduct(productService.findOne(jsonObject_productPurchase.getJSONObject("product").getLong("id")));
            productPurchase.setQuantity(jsonObject_productPurchase.getDouble("quantity"));
            productPurchase.setUnitPurchasePrice(jsonObject_productPurchase.getDouble("unitPurchasePrice"));

            LOG.info("إنشاء عملية السحب للشراء");
            BankTransaction bankTransactionWithdrawPurchase = new BankTransaction();
            {
                bankTransactionWithdrawPurchase.setBank(Initializer.bank);
                bankTransactionWithdrawPurchase.setSupplier(productPurchase.getSupplier());
                bankTransactionWithdrawPurchase.setAmount(productPurchase.getQuantity() * productPurchase.getUnitPurchasePrice());
                bankTransactionWithdrawPurchase.setTransactionType(Initializer.transactionTypeWithdrawPurchase);
                bankTransactionWithdrawPurchase.setDate(productPurchase.getDate());
                bankTransactionWithdrawPurchase.setPerson(caller);
                StringBuilder builder = new StringBuilder();
                builder.append("سحب مبلغ نقدي بقيمة ");
                builder.append(bankTransactionWithdrawPurchase.getAmount());
                builder.append("ريال سعودي، ");
                builder.append(" من / ");
                builder.append(bankTransactionWithdrawPurchase.getSupplier().getContact().getShortName());
                builder.append("، قيمة شراء " + productPurchase.getProduct().getName());
                builder.append("، عدد /  " + productPurchase.getQuantity());
                builder.append("، بسعر الوحدة /  " + productPurchase.getUnitPurchasePrice());
                builder.append(" ، " + (productPurchase.getNote() == null ? "" : productPurchase.getNote()));
                bankTransactionWithdrawPurchase.setNote(builder.toString());
            }

            productPurchase.setBankTransaction(bankTransactionService.save(bankTransactionWithdrawPurchase));
            productPurchase.setPerson(caller);
            productPurchase.setNote(bankTransactionWithdrawPurchase.getNote());
            productPurchase = productPurchaseService.save(productPurchase);

            LOG.info("ربط الأصناف بالعقود");
            BillPurchaseProduct billPurchaseProduct = new BillPurchaseProduct();
            billPurchaseProduct.setBillPurchase(billPurchase);
            billPurchaseProduct.setProductPurchase(productPurchase);
            billPurchaseProduct.setQuantity(jsonObject_productPurchase.getDouble("quantity"));
            billPurchaseProduct.setUnitSellPrice(jsonObject_productPurchase.getDouble("unitSellPrice"));
            billPurchaseProduct.setUnitVat(jsonObject_productPurchase.getDouble("unitVat"));
            billPurchase.getContractProducts().add(billPurchaseProductService.save(billPurchaseProduct));
        }

        StringBuilder builder = new StringBuilder();
        builder.append("تم إنشاء العقد بنجاح بمجموع أسعار = ");
        builder.append(billPurchase.getTotalPrice());
        builder.append("، وأصناف عدد " + billPurchase.getContractProducts().size() + " صنف");
        builder.append("، تسدد على " + billPurchase.getContractPremiums().size() + " قسط");
        notificationService.notifyAll(Notification
                                              .builder()
                                              .message(builder.toString())
                                              .type("success").build());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), billPurchase);
    }

    @DeleteMapping(value = "delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_CONTRACT_DELETE')")
    @Transactional
    public void delete(@PathVariable Long id) {
        BillPurchase billPurchase = contractService.findOne(id);
        if (billPurchase != null) {
            LOG.info("حذف كل سلع العقد");
            billPurchaseProductService.delete(billPurchase.getContractProducts());
            LOG.info("حذف كل معاملات البنك لدفعات العقد");
            bankTransactionService.delete(
                    billPurchase
                            .getContractPayments()
                            .stream()
                            .map(BillPurchasePayment::getBankTransaction)
                            .collect(Collectors.toList())
                                         );
            LOG.info("حذف كل دفعات العقد");
            billPurchasePaymentService.delete(billPurchase.getContractPayments());
            LOG.info("حذف كل أقساط العقد");
            contractPremiumService.delete(billPurchase.getContractPremiums());
            LOG.info("حذف العقد");
            contractService.delete(id);
            notificationService.notifyAll(
                    Notification
                            .builder()
                            .message("تم حذف العقد وكل ما يتعلق به من حسابات بنجاح")
                            .type("error").build());
        }
    }

    @GetMapping(value = "findOne/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_DETAILS),
                                       contractService.findOne(id));
    }

    @GetMapping(value = "findMyContracts", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findMyContracts() {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_DETAILS),
                                       contractService.findBySupplier(caller.getCompany().getSupplier()));
    }

    @GetMapping(value = "findBySupplier/{supplierId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findBySupplier(@PathVariable(value = "supplierId") Long supplierId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_DETAILS),
                                       contractService.findBySupplier(supplierService.findOne(supplierId)));
    }

    @GetMapping(value = "filter", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String filter(
            //BillPurchase Filters
            @RequestParam(value = "codeFrom", required = false) final Integer codeFrom,
            @RequestParam(value = "codeTo", required = false) final Integer codeTo,
            @RequestParam(value = "dateFrom", required = false) final Long dateFrom,
            @RequestParam(value = "dateTo", required = false) final Long dateTo,
            //Customer Filters
            @RequestParam(value = "customerCodeFrom", required = false) final Integer customerCodeFrom,
            @RequestParam(value = "customerCodeTo", required = false) final Integer customerCodeTo,
            @RequestParam(value = "customerRegisterDateFrom", required = false) final Long customerRegisterDateFrom,
            @RequestParam(value = "customerRegisterDateTo", required = false) final Long customerRegisterDateTo,
            @RequestParam(value = "customerName", required = false) final String customerName,
            @RequestParam(value = "customerMobile", required = false) final String customerMobile,
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
                contractSearch.filter(
                        codeFrom,
                        codeTo,
                        dateFrom,
                        dateTo,
                        customerCodeFrom,
                        customerCodeTo,
                        customerRegisterDateFrom,
                        customerRegisterDateTo,
                        customerName,
                        customerMobile,
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
