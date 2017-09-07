package com.besafx.app.rest;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Branch;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Views;
import com.besafx.app.service.BranchService;
import com.besafx.app.service.CompanyService;
import com.besafx.app.service.PersonService;
import com.besafx.app.util.DistinctFilter;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/branch/")
public class BranchRest {

    private final static Logger log = LoggerFactory.getLogger(BranchRest.class);

    private final String FILTER_TABLE = "id,code,name,address,phone,mobile,fax,email,website,commericalRegisteration,logo,company[id,code,name],manager[id,contact[id,firstName,forthName]]";
    private final String FILTER_BRANCH_COMBO = "id,code,name";
    private final String FILTER_BRANCH_MASTER_COMBO = "id,code,name,masters[id,code,name]";
    private final String FILTER_BRANCH_MASTER_COURSE_COMBO = "id,code,name,masters[id,code,name,courses[id,code,instructor]]";

    @Autowired
    private CompanyService companyService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private PersonService personService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BRANCH_CREATE')")
    public String create(@RequestBody Branch branch, Principal principal) {
        Integer maxCode = branchService.findMaxCode();
        if (maxCode == null) {
            branch.setCode(1);
        } else {
            branch.setCode(maxCode + 1);
        }
        branch = branchService.save(branch);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على الفروع")
                .message("تم اضافة فرع جديد بنجاح")
                .type("success")
                .icon("fa-cubes")
                .build(), principal.getName());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), branch);
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BRANCH_UPDATE')")
    public String update(@RequestBody Branch branch, Principal principal) {
        if (branchService.findByCodeAndIdIsNot(branch.getCode(), branch.getId()) != null) {
            throw new CustomException("هذا الكود مستخدم سابقاً، فضلاً قم بتغير الكود.");
        }
        Branch object = branchService.findOne(branch.getId());
        if (object != null) {
            branch = branchService.save(branch);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على الفروع")
                    .message("تم تعديل بيانات الفرع بنجاح")
                    .type("success")
                    .icon("fa-cubes")
                    .build(), principal.getName());
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), branch);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BRANCH_DELETE')")
    public void delete(@PathVariable Long id) {
        Branch object = branchService.findOne(id);
        if (object != null) {
            branchService.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Branch> findAll() {
        return Lists.newArrayList(branchService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Branch findOne(@PathVariable Long id) {
        return branchService.findOne(id);
    }

    @RequestMapping(value = "count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Long count() {
        return branchService.count();
    }

    @RequestMapping(value = "findByName/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Branch findByName(@PathVariable(value = "name") String name) {
        return branchService.findByName(name);
    }

    @RequestMapping(value = "findByCode/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Branch findByCode(@PathVariable(value = "code") Integer code) {
        return branchService.findByCode(code);
    }

    @RequestMapping(value = "fetchTableData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Branch> fetchTableData(Principal principal) {
        try {
            Person person = personService.findByEmail(principal.getName());
            List<Branch> list = new ArrayList<>();
            companyService.findByManager(person).stream().forEach(company -> list.addAll(branchService.findByCompany(company)));
            list.addAll(branchService.findByManager(person));
            list.add(person.getBranch());
            return list.stream().filter(DistinctFilter.distinctByKey(Branch::getId)).collect(Collectors.toList());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    @RequestMapping(value = "fetchTableDataSummery", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchTableDataSummery(Principal principal) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), fetchTableData(principal));
    }

    @RequestMapping(value = "fetchBranchMaster", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchBranchMaster(Principal principal) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_BRANCH_MASTER_COMBO), fetchTableData(principal));
    }

    @RequestMapping(value = "fetchBranchMasterCourse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchBranchMasterCourse(Principal principal) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_BRANCH_MASTER_COURSE_COMBO), fetchTableData(principal));
    }

    @RequestMapping(value = "fetchBranchCombo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchBranchCombo(Principal principal) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_BRANCH_COMBO), fetchTableData(principal));
    }
}
