package com.besafx.app.rest;

import com.besafx.app.config.CustomException;
import com.besafx.app.entity.BillBuyType;
import com.besafx.app.entity.Person;
import com.besafx.app.service.BillBuyTypeService;
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
@RequestMapping(value = "/api/billBuyType/")
public class BillBuyTypeRest {

    @Autowired
    private PersonService personService;

    @Autowired
    private BillBuyTypeService billBuyTypeService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BILL_BUY_TYPE_CREATE')")
    public BillBuyType create(@RequestBody BillBuyType billBuyType, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        billBuyType.setLastUpdate(new Date());
        billBuyType.setLastPerson(person);
        billBuyType = billBuyTypeService.save(billBuyType);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على قاعدة البيانات")
                .message("تم اضافة حساب فاتورة جديد بنجاح")
                .type("success")
                .icon("fa-database")
                .build(), principal.getName());
        notificationService.notifyAllExceptMe(Notification
                .builder()
                .title("العمليات على قاعدة البيانات")
                .message("تم اضافة حساب فاتورة جديد بواسطة " + principal.getName())
                .type("warning")
                .icon("fa-database")
                .build());
        return billBuyType;
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BILL_BUY_TYPE_UPDATE')")
    public BillBuyType update(@RequestBody BillBuyType billBuyType, Principal principal) {
        BillBuyType object = billBuyTypeService.findOne(billBuyType.getId());
        if (object != null) {
            Person person = personService.findByEmail(principal.getName());
            billBuyType.setLastUpdate(new Date());
            billBuyType.setLastPerson(person);
            billBuyType = billBuyTypeService.save(billBuyType);

            return billBuyType;
        } else {
            throw new CustomException("عفواً، لا يوجد هذا الحساب");
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BILL_BUY_TYPE_DELETE')")
    public void delete(@PathVariable Long id) {
        BillBuyType object = billBuyTypeService.findOne(id);
        if (object != null) {
            billBuyTypeService.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<BillBuyType> findAll() {
        return Lists.newArrayList(billBuyTypeService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BillBuyType findOne(@PathVariable Long id) {
        return billBuyTypeService.findOne(id);
    }

}
