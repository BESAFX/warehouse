package com.besafx.app.rest;

import com.besafx.app.config.CustomException;
import com.besafx.app.entity.BillBuyType;
import com.besafx.app.entity.Person;
import com.besafx.app.service.BillBuyTypeService;
import com.besafx.app.service.PersonService;
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

import java.security.Principal;
import java.util.Date;

@RestController
@RequestMapping(value = "/api/billBuyType/")
public class BillBuyTypeRest {

    private final static Logger log = LoggerFactory.getLogger(BillBuyTypeRest.class);

    private final String FILTER_TABLE = "**,lastPerson[id,contact[id,firstName,forthName]]";

    @Autowired
    private PersonService personService;

    @Autowired
    private BillBuyTypeService billBuyTypeService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BILL_BUY_TYPE_CREATE')")
    public String create(@RequestBody BillBuyType billBuyType, Principal principal) {
        BillBuyType topBillBuyType = billBuyTypeService.findTopByOrderByCodeDesc();
        if (topBillBuyType == null) {
            billBuyType.setCode(1);
        } else {
            billBuyType.setCode(topBillBuyType.getCode() + 1);
        }
        Person person = personService.findByEmail(principal.getName());
        billBuyType.setLastUpdate(new Date());
        billBuyType.setLastPerson(person);
        billBuyType = billBuyTypeService.save(billBuyType);
        notificationService.notifyAll(Notification.builder().message("تم حساب فاتورة جديد بنجاح").type("success").build());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), billBuyType);
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BILL_BUY_TYPE_UPDATE')")
    public String update(@RequestBody BillBuyType billBuyType, Principal principal) {
        if (billBuyTypeService.findByCodeAndIdIsNot(billBuyType.getCode(), billBuyType.getId()) != null) {
            throw new CustomException("هذا الكود مستخدم سابقاً، فضلاً قم بتغير الكود.");
        }
        BillBuyType object = billBuyTypeService.findOne(billBuyType.getId());
        if (object != null) {
            Person person = personService.findByEmail(principal.getName());
            billBuyType.setLastUpdate(new Date());
            billBuyType.setLastPerson(person);
            billBuyType = billBuyTypeService.save(billBuyType);
            notificationService.notifyAll(Notification.builder().message("تم تعديل حساب الفاتورة بنجاح").type("success").build());
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), billBuyType);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BILL_BUY_TYPE_DELETE')")
    public void delete(@PathVariable Long id, Principal principal) {
        BillBuyType object = billBuyTypeService.findOne(id);
        if (object != null) {
            billBuyTypeService.delete(id);
            notificationService.notifyAll(Notification.builder().message("تم حذف حساب الفاتورة بنجاح").type("success").build());
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAll() {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), Lists.newArrayList(billBuyTypeService.findAll()));
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), billBuyTypeService.findOne(id));
    }

}
