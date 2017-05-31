package com.besafx.app.rest;
import com.besafx.app.entity.BillBuy;
import com.besafx.app.entity.Person;
import com.besafx.app.search.BillBuySearch;
import com.besafx.app.service.BillBuyService;
import com.besafx.app.service.PersonService;
import com.besafx.app.ws.Notification;
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
@RequestMapping(value = "/api/billBuy/")
public class BillBuyRest {

    @Autowired
    private PersonService personService;

    @Autowired
    private BillBuyService billBuyService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BillBuySearch billBuySearch;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BILL_BUY_CREATE')")
    public BillBuy create(@RequestBody BillBuy billBuy, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        billBuy.setLastUpdate(new Date());
        billBuy.setLastPerson(person);
        billBuy = billBuyService.save(billBuy);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على قاعدة البيانات")
                .message("تم انشاء فاتورة جديدة بنجاح")
                .type("success")
                .icon("fa-plus-square")
                .build(), principal.getName());
        return billBuy;
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BILL_BUY_UPDATE')")
    public BillBuy update(@RequestBody BillBuy billBuy, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        BillBuy object = billBuyService.findOne(billBuy.getId());
        if (object != null) {
            billBuy.setLastUpdate(new Date());
            billBuy.setLastPerson(person);
            billBuy = billBuyService.save(billBuy);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على قاعدة البيانات")
                    .message("تم تعديل بيانات الفاتورة بنجاح")
                    .type("warn")
                    .icon("fa-edit")
                    .build(), principal.getName());
            return billBuy;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BILL_BUY_DELETE')")
    public void delete(@PathVariable Long id, Principal principal) {
        BillBuy object = billBuyService.findOne(id);
        if (object != null) {
            billBuyService.delete(id);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على قاعدة البيانات")
                    .message("تم حذف الفاتورة بنجاح")
                    .type("error")
                    .icon("fa-trash")
                    .build(), principal.getName());
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<BillBuy> findAll() {
        return Lists.newArrayList(billBuyService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BillBuy findOne(@PathVariable Long id) {
        return billBuyService.findOne(id);
    }

    @RequestMapping(value = "filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<BillBuy> filter(
            @RequestParam(value = "codeFrom", required = false) final Long codeFrom,
            @RequestParam(value = "codeTo", required = false) final Long codeTo,
            @RequestParam(value = "dateFrom", required = false) final Long dateFrom,
            @RequestParam(value = "dateTo", required = false) final Long dateTo,
            @RequestParam(value = "amountFrom", required = false) final Long amountFrom,
            @RequestParam(value = "amountTo", required = false) final Long amountTo,
            @RequestParam(value = "branchId", required = false) final Long branchId) {
        return billBuySearch.search(codeFrom, codeTo, dateFrom, dateTo, amountFrom, amountTo, branchId);
    }

}
