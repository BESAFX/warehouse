package com.besafx.app.rest;

import com.besafx.app.entity.BillPurchaseProduct;
import com.besafx.app.service.BillPurchaseProductService;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping(value = "/api/billPurchaseProduct/")
public class BillPurchaseProductRest {

    private final static Logger LOG = LoggerFactory.getLogger(BillPurchaseProductRest.class);

    private final String FILTER_TABLE = "" +
            "**," +
            "productPurchase[id,product[id,name]]," +
            "-billPurchase";

    @Autowired
    private BillPurchaseProductService billPurchaseProductService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BILL_PURCHASE_PRODUCT_CREATE')")
    @Transactional
    public String create(@RequestBody BillPurchaseProduct billPurchaseProduct) {
        billPurchaseProduct = billPurchaseProductService.save(billPurchaseProduct);
        notificationService.notifyAll(Notification
                                              .builder()
                                              .message("تم اضافة سلعة للفاتورة بنجاح")
                                              .type("success").build());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), billPurchaseProduct);
    }

    @PostMapping(value = "createBatch", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BILL_PURCHASE_PRODUCT_CREATE')")
    @Transactional
    public String createBatch(@RequestBody List<BillPurchaseProduct> billPurchaseProducts) {
        billPurchaseProducts = Lists.newArrayList(billPurchaseProductService.save(billPurchaseProducts));
        notificationService.notifyAll(Notification
                                              .builder()
                                              .message("تم اضافة عدد من السلع للفاتورة بنجاح")
                                              .type("success").build());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), billPurchaseProducts);
    }

    @DeleteMapping(value = "delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BILL_PURCHASE_PRODUCT_DELETE')")
    @Transactional
    public void delete(@PathVariable Long id) {
        BillPurchaseProduct billPurchaseProduct = billPurchaseProductService.findOne(id);
        if (billPurchaseProduct != null) {
            billPurchaseProductService.delete(id);
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message("تم حذف سلع من العقد بنجاح")
                                                  .type("error").build());
        }
    }

    @GetMapping(value = "findAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAll() {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                                       Lists.newArrayList(billPurchaseProductService.findAll()));
    }

    @GetMapping(value = "findOne/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                                       billPurchaseProductService.findOne(id));
    }

    @GetMapping(value = "findByBillPurchase/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByBillPurchase(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                                       billPurchaseProductService.findByBillPurchaseId(id));
    }
}
