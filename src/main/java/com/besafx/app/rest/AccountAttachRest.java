package com.besafx.app.rest;

import com.besafx.app.service.BranchService;
import com.besafx.app.service.PersonService;
import com.besafx.app.ws.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/accountAttach/")
public class AccountAttachRest {

    private final static Logger log = LoggerFactory.getLogger(AccountAttachRest.class);

    @Autowired
    private BranchService accountAttachService;

    @Autowired
    private PersonService personService;

    @Autowired
    private NotificationService notificationService;
}
