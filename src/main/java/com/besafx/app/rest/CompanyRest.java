package com.besafx.app.rest;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Company;
import com.besafx.app.entity.Person;
import com.besafx.app.service.BranchService;
import com.besafx.app.service.CompanyService;
import com.besafx.app.service.PersonService;
import com.besafx.app.util.DistinctFilter;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/company/")
public class CompanyRest {

    private final static Logger log = LoggerFactory.getLogger(CompanyRest.class);

    @Autowired
    private PersonService personService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_COMPANY_CREATE')")
    public Company create(@RequestBody Company company, Principal principal) {
        Integer maxCode = companyService.findMaxCode();
        if (maxCode == null) {
            company.setCode(1);
        } else {
            company.setCode(maxCode + 1);
        }
        company = companyService.save(company);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على الشركات")
                .message("تم اضافة شركة جديدة بنجاح")
                .type("success")
                .icon("fa-fort-awesome")
                .build(), principal.getName());
        return company;
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_COMPANY_UPDATE')")
    public Company update(@RequestBody Company company, Principal principal) {
        if (companyService.findByCodeAndIdIsNot(company.getCode(), company.getId()) != null) {
            throw new CustomException("هذا الكود مستخدم سابقاً، فضلاً قم بتغير الكود.");
        }
        Company object = companyService.findOne(company.getId());
        if (object != null) {
            company = companyService.save(company);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على الشركات")
                    .message("تم تعديل بيانات الشركة بنجاح")
                    .type("success")
                    .icon("fa-fort-awesome")
                    .build(), principal.getName());
            return company;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_COMPANY_DELETE')")
    public void delete(@PathVariable Long id) {
        Company object = companyService.findOne(id);
        if (object != null) {
            companyService.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Company> findAll() {
        return Lists.newArrayList(companyService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Company findOne(@PathVariable Long id) {
        return companyService.findOne(id);
    }

    @RequestMapping(value = "count", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long count() {
        return companyService.count();
    }

    @RequestMapping(value = "fetchTableData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Company> fetchTableData(Principal principal) {
        try {
            Person person = personService.findByEmail(principal.getName());
            List<Company> list = new ArrayList<>();
            list.addAll(companyService.findByManager(person));
            branchService.findByManager(person).stream().forEach(branch -> list.add(branch.getCompany()));
            list.add(person.getBranch().getCompany());
            return list.stream().filter(DistinctFilter.distinctByKey(Company::getId)).collect(Collectors.toList());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    @RequestMapping(value = "fetchTableDataSummery", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Company> fetchTableDataSummery(Principal principal) {
        return fetchTableData(principal);
    }

}
