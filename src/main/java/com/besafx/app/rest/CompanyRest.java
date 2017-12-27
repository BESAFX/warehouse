package com.besafx.app.rest;

import com.besafx.app.entity.Company;
import com.besafx.app.service.CompanyService;
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

@RestController
@RequestMapping(value = "/api/company/")
public class CompanyRest {

    private final static Logger log = LoggerFactory.getLogger(CompanyRest.class);

    private final String FILTER_TABLE = "**,branches[id]";

    @Autowired
    private CompanyService companyService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String get() {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), companyService.findFirstBy());
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_COMPANY_UPDATE')")
    public String update(@RequestBody Company company, Principal principal) {
        Company object = companyService.findOne(company.getId());
        if (object != null) {
            company = companyService.save(company);
            notificationService.notifyAll(Notification.builder().message("تم تعديل بيانات الشركة بنجاح").type("warning").build());
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), company);
        } else {
            return null;
        }
    }

}
