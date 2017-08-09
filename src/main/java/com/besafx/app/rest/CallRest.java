package com.besafx.app.rest;

import com.besafx.app.entity.Call;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Views;
import com.besafx.app.service.CallService;
import com.besafx.app.service.PersonService;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/call/")
public class CallRest {

    @Autowired
    private CallService callService;

    @Autowired
    private PersonService personService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Call create(@RequestBody Call call, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        call.setPerson(person);
        call.setDate(new DateTime().toDate());
        call = callService.save(call);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على الاتصالات")
                .message("تم انشاء اتصال جديد بنجاح")
                .type("success")
                .icon("fa-plus-square")
                .build(), principal.getName());
        return call;
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Call> findAll() {
        return Lists.newArrayList(callService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Call findOne(@PathVariable Long id) {
        return callService.findOne(id);
    }

    @RequestMapping(value = "findByOffer/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @JsonView(Views.Summery.class)
    public List<Call> findByOffer(@PathVariable Long id) {
        return callService.findByOfferId(id);
    }

}
