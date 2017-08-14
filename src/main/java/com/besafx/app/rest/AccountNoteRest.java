package com.besafx.app.rest;

import com.besafx.app.entity.AccountNote;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Views;
import com.besafx.app.service.AccountNoteService;
import com.besafx.app.service.PersonService;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/accountNote/")
public class AccountNoteRest {

    @Autowired
    private AccountNoteService accountNoteService;

    @Autowired
    private PersonService personService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ACCOUNT_NOTE_CREATE')")
    public AccountNote create(@RequestBody AccountNote accountNote, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        accountNote.setPerson(person);
        accountNote.setDate(new DateTime().toDate());
        accountNote = accountNoteService.save(accountNote);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على ملاحظات الطلاب")
                .message("تم حفظ ملاحظة جديدة بنجاح")
                .type("success")
                .icon("fa-plus-square")
                .build(), principal.getName());
        return accountNote;
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AccountNote> findAll() {
        return Lists.newArrayList(accountNoteService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AccountNote findOne(@PathVariable Long id) {
        return accountNoteService.findOne(id);
    }

    @RequestMapping(value = "findByAccount/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @JsonView(Views.Summery.class)
    public List<AccountNote> findByAccount(@PathVariable Long id) {
        return accountNoteService.findByAccountId(id);
    }

}
