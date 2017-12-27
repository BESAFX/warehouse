package com.besafx.app.rest;

import com.besafx.app.entity.AccountNote;
import com.besafx.app.entity.Person;
import com.besafx.app.service.AccountNoteService;
import com.besafx.app.service.PersonService;
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
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(value = "/api/accountNote/")
public class AccountNoteRest {

    private final static Logger log = LoggerFactory.getLogger(AccountAttachRest.class);

    private final String FILTER_TABLE = "**,account[id],person[id,contact[id,firstName,forthName]]";

    @Autowired
    private AccountNoteService accountNoteService;

    @Autowired
    private PersonService personService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ACCOUNT_NOTE_CREATE')")
    public String create(@RequestBody AccountNote accountNote, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        accountNote.setPerson(person);
        accountNote.setDate(new DateTime().toDate());
        accountNote = accountNoteService.save(accountNote);
        notificationService.notifyOne(Notification.builder().message("تم حفظ ملاحظة جديدة بنجاح").type("success").build(), principal.getName());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), accountNote);
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAll() {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), Lists.newArrayList(accountNoteService.findAll()));
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), accountNoteService.findOne(id));
    }

    @RequestMapping(value = "findByAccount/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByAccount(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), accountNoteService.findByAccountId(id));
    }

}
