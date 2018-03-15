package com.besafx.app.rest;

import com.besafx.app.entity.Call;
import com.besafx.app.entity.Person;
import com.besafx.app.service.CallService;
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
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(value = "/api/call/")
public class CallRest {

    public static final String FILTER_TABLE = "**,person[id,contact[id,firstName,forthName]],offer[id]";
    private final static Logger log = LoggerFactory.getLogger(CallRest.class);
    @Autowired
    private CallService callService;

    @Autowired
    private PersonService personService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String create(@RequestBody Call call, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        call.setPerson(person);
        call.setDate(new DateTime().toDate());
        call = callService.save(call);
        notificationService.notifyAll(Notification
                .builder()
                .title("العمليات على الاتصالات")
                .message("تم اتصال جديد بنجاح")
                .type("success")
                .icon("fa-plus-square")
                .build());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), call);
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAll() {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), Lists.newArrayList(callService.findAll()));
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), callService.findOne(id));
    }

    @RequestMapping(value = "findByOffer/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByOffer(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), callService.findByOfferId(id));
    }

}
