package com.besafx.app.rest;

import com.besafx.app.entity.AccountCondition;
import com.besafx.app.entity.Person;
import com.besafx.app.service.AccountConditionService;
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
@RequestMapping(value = "/api/accountCondition/")
public class AccountConditionRest {

    @Autowired
    private PersonService personService;

    @Autowired
    private AccountConditionService accountConditionService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ACCOUNT_CONDITION_CREATE')")
    public AccountCondition create(@RequestBody AccountCondition accountCondition, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        accountCondition.setLastUpdate(new Date());
        accountCondition.setPerson(person);
        accountCondition = accountConditionService.save(accountCondition);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على تسجيل الطلاب")
                .message("تم انشاء حالة جديدة للطالب بنجاح")
                .type("success")
                .icon("fa-plus-square")
                .build(), principal.getName());
        return accountCondition;
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AccountCondition> findAll() {
        return Lists.newArrayList(accountConditionService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AccountCondition findOne(@PathVariable Long id) {
        return accountConditionService.findOne(id);
    }

    @RequestMapping(value = "findByAccount/{accountId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AccountCondition> findByAccount(@PathVariable(value = "accountId") Long accountId) {
        return accountConditionService.findByAccountId(accountId);
    }

}
