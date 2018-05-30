package com.besafx.app.rest;

import com.besafx.app.auditing.PersonAwareUserDetails;
import com.besafx.app.entity.BankTransaction;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.ProductPurchase;
import com.besafx.app.init.Initializer;
import com.besafx.app.search.ProductPurchaseSearch;
import com.besafx.app.service.BankTransactionService;
import com.besafx.app.service.ProductPurchaseService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/productPurchase/")
public class ProductPurchaseRest {

    private final static Logger LOG = LoggerFactory.getLogger(ProductPurchaseRest.class);

    private final String FILTER_TABLE = "" +
            "**," +
            "-billPurchaseProducts," +
            "product[id,name]," +
            "-bankTransaction," +
            "supplier[id,contact[id,shortName]]," +
            "person[id,contact[id,shortName]]";

    @Autowired
    private ProductPurchaseService productPurchaseService;

    @Autowired
    private ProductPurchaseSearch productPurchaseSearch;

    @Autowired
    private BankTransactionService bankTransactionService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PRODUCT_PURCHASE_CREATE')")
    @Transactional
    public String create(@RequestBody ProductPurchase productPurchase) {
        LOG.info("إنشاء حركة الشراء");
        ProductPurchase topProductPurchase = productPurchaseService.findTopByOrderByCodeDesc();
        if (topProductPurchase == null) {
            productPurchase.setCode(1);
        } else {
            productPurchase.setCode(topProductPurchase.getCode() + 1);
        }
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        productPurchase.setDate(new DateTime().toDate());
        productPurchase.setPerson(caller);
        productPurchase = productPurchaseService.save(productPurchase);

        LOG.info("إنشاء عملية السحب للشراء");
        BankTransaction bankTransactionWithdrawPurchase = new BankTransaction();
        bankTransactionWithdrawPurchase.setBank(Initializer.bank);
        bankTransactionWithdrawPurchase.setSupplier(productPurchase.getSupplier());
        bankTransactionWithdrawPurchase.setAmount(productPurchase.getQuantity() * productPurchase.getUnitPurchasePrice());
        bankTransactionWithdrawPurchase.setTransactionType(Initializer.transactionTypeWithdrawPurchase);
        bankTransactionWithdrawPurchase.setDate(new DateTime().toDate());
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

        productPurchase.setBankTransaction(bankTransactionService.save(bankTransactionWithdrawPurchase));
        productPurchase = productPurchaseService.save(productPurchase);

        notificationService.notifyAll(Notification
                                              .builder()
                                              .message(builder.toString())
                                              .type("success").build());

        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), productPurchase);
    }

    @DeleteMapping(value = "delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PRODUCT_PURCHASE_DELETE')")
    @Transactional
    public void delete(@PathVariable Long id) {
        ProductPurchase productPurchase = productPurchaseService.findOne(id);
        if (productPurchase != null) {
            bankTransactionService.delete(productPurchase.getBankTransaction());
            productPurchaseService.delete(id);
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message("تم حذف المخزون وكل ما يتعلق به من حسابات بنجاح")
                                                  .type("error").build());
        }
    }

    @GetMapping(value = "findOne/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                                       productPurchaseService.findOne(id));
    }

    @GetMapping(value = "findMyProductPurchases", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findMyProductPurchases() {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                                       productPurchaseService.findBySupplier(caller.getCompany().getSupplier()));
    }

    @GetMapping(value = "findBySupplier/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findBySupplier(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                                       productPurchaseService.findBySupplierId(id));
    }

    @GetMapping(value = "findBySupplierAndRemainFull/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findBySupplierAndRemainFull(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                                       productPurchaseService
                                               .findBySupplierId(id)
                                               .stream()
                                               .filter(productPurchase -> productPurchase.getRemain() > 0)
                                               .collect(Collectors.toList())
                                      );
    }

    @GetMapping(value = "findBySupplierAndRemainEmpty/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findBySupplierAndRemainEmpty(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                                       productPurchaseService
                                               .findBySupplierId(id)
                                               .stream()
                                               .filter(productPurchase -> productPurchase.getRemain() <= 0)
                                               .collect(Collectors.toList())
                                      );
    }

    @GetMapping(value = "filter", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String filter(
            //ProductPurchase Filters
            @RequestParam(value = "codeFrom", required = false) final Integer codeFrom,
            @RequestParam(value = "codeTo", required = false) final Integer codeTo,
            @RequestParam(value = "dateFrom", required = false) final Long dateFrom,
            @RequestParam(value = "dateTo", required = false) final Long dateTo,
            //Product Filters
            @RequestParam(value = "productCodeFrom", required = false) final Integer productCodeFrom,
            @RequestParam(value = "productCodeTo", required = false) final Integer productCodeTo,
            @RequestParam(value = "productRegisterDateFrom", required = false) final Long productRegisterDateFrom,
            @RequestParam(value = "productRegisterDateTo", required = false) final Long productRegisterDateTo,
            @RequestParam(value = "productName", required = false) final String productName,
            @RequestParam(value = "productParentId", required = false) final Long productParentId,
            //Supplier Filters
            @RequestParam(value = "supplierName", required = false) final String supplierName,
            @RequestParam(value = "supplierMobile", required = false) final String supplierMobile,
            @RequestParam(value = "supplierIdentityNumber", required = false) final String supplierIdentityNumber,
            Pageable pageable) {
        return SquigglyUtils.stringify(
                Squiggly.init(
                        new ObjectMapper(),
                        "**,".concat("content[").concat(FILTER_TABLE).concat("]")),
                productPurchaseSearch.filter(
                        codeFrom,
                        codeTo,
                        dateFrom,
                        dateTo,
                        productCodeFrom,
                        productCodeTo,
                        productRegisterDateFrom,
                        productRegisterDateTo,
                        productName,
                        productParentId,
                        supplierName,
                        supplierMobile,
                        supplierIdentityNumber,
                        pageable));
    }
}
