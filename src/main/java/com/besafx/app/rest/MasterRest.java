package com.besafx.app.rest;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.*;
import com.besafx.app.service.*;
import com.besafx.app.util.DistinctFilter;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.annotation.JsonView;
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

import javax.swing.text.View;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/master/")
public class MasterRest {

    private final static Logger log = LoggerFactory.getLogger(MasterRest.class);

    private final String FILTER_ALL = "**";
    private final String FILTER_TABLE = "id,code,name,period,branch[id,code,name],lastPerson[id,contact[id,firstName,forthName]],courses[id,code,instructor]";
    private final String FILTER_MASTER_COMBO = "id,code,name";
    private final String FILTER_MASTER_BRANCH_COMBO = "id,code,name,branch[id,code,name]";
    private final String FILTER_MASTER_COURSE_COMBO = "id,code,name,courses[id,code,instructor]";

    @Autowired
    private PersonService personService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private MasterService masterService;

    @Autowired
    private OfferService offerService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_MASTER_CREATE')")
    public String create(@RequestBody Master master, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        Integer lastCode = findLastCodeByBranch(master.getBranch().getId());
        if (lastCode == null) {
            master.setCode(1);
        } else {
            master.setCode(lastCode + 1);
        }
        master.setLastUpdate(new Date());
        master.setLastPerson(person);
        master = masterService.save(master);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على التخصصات")
                .message("تم إنشاء تخصص جديد بنجاح")
                .type("success")
                .icon("fa-plus-square")
                .build(), principal.getName());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), master);
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_MASTER_UPDATE')")
    public String update(@RequestBody Master master, Principal principal) {
        if (masterService.findByCodeAndBranchAndIdIsNot(master.getCode(), master.getBranch(), master.getId()) != null) {
            throw new CustomException("هذا الكود مستخدم سابقاً، فضلاً قم بتغير الكود.");
        }
        Person person = personService.findByEmail(principal.getName());
        Master object = masterService.findOne(master.getId());
        if (object != null) {
            master.setLastUpdate(new Date());
            master.setLastPerson(person);
            master = masterService.save(master);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على التخصصات")
                    .message("تم تعديل بيانات التخصص بنجاح")
                    .type("warning")
                    .icon("fa-edit")
                    .build(), principal.getName());
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), master);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_MASTER_DELETE')")
    public void delete(@PathVariable Long id, Principal principal) {
        Master object = masterService.findOne(id);
        if (object != null) {
            log.info("Delete all offers...");
            offerService.delete(object.getOffers());
            log.info("Delete all payments...");
            List<Account> accounts = object.getCourses().stream().flatMap(course -> course.getAccounts().stream()).collect(Collectors.toList());
            paymentService.delete(accounts.stream().flatMap(account -> account.getPayments().stream()).collect(Collectors.toList()));
            log.info("Delete all accounts...");
            accountService.delete(accounts);
            log.info("Delete all courses....");
            courseService.delete(object.getCourses());
            log.info("Finally delete master...");
            masterService.delete(id);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على التخصصات")
                    .message("تم حذف التخصصات وكل ما يتعلق بها من عروض ودورات بنجاح")
                    .type("error")
                    .icon("fa-ban")
                    .build(), principal.getName());
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Master> findAll() {
        return Lists.newArrayList(masterService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Master findOne(@PathVariable Long id) {
        return masterService.findOne(id);
    }

    @RequestMapping(value = "findByBranch", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Master> findByBranch(@RequestParam(value = "branchId") Long branchId) {
        Branch branch = branchService.findOne(branchId);
        if (branch != null) {
            return masterService.findByBranch(branch);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "findByCodeAndBranch", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Master findByCodeAndBranch(@RequestParam(value = "code") Integer code, @RequestParam(value = "branchId") Long branchId) {
        Branch branch = branchService.findOne(branchId);
        if (branch != null) {
            return masterService.findByCodeAndBranch(code, branch);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "findLastCodeByBranch/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Integer findLastCodeByBranch(@PathVariable Long id) {
        Integer maxCode = masterService.findLastCodeByBranch(id);
        if (maxCode == null) {
            return 1;
        } else {
            return maxCode + 1;
        }
    }

    @RequestMapping(value = "count", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long count() {
        return masterService.count();
    }

    @RequestMapping(value = "fetchTableData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Master> fetchTableData(Principal principal) {
        try {
            Person person = personService.findByEmail(principal.getName());
            List<Master> list = new ArrayList<>();
            ///
            companyService.findByManager(person).stream().forEach(company ->
                    branchService.findByCompany(company).stream().forEach(branch ->
                            list.addAll(masterService.findByBranch(branch))));
            ///
            branchService.findByManager(person).stream().forEach(branch ->
                    list.addAll(masterService.findByBranch(branch)));
            ///
            list.addAll(masterService.findByBranch(person.getBranch()));
            ///
            return list.stream().filter(DistinctFilter.distinctByKey(Master::getId)).collect(Collectors.toList());
        } catch (Exception ex) {
            log.info(ex.getMessage(), ex);
            return null;
        }
    }

    @RequestMapping(value = "fetchTableDataSummery", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchTableDataSummery(Principal principal) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), fetchTableData(principal));
    }

    @RequestMapping(value = "fetchMasterCombo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchMasterCombo(Principal principal) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_MASTER_COMBO), fetchTableData(principal));
    }

    @RequestMapping(value = "fetchMasterCourseCombo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchMasterCourseCombo(Principal principal) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_MASTER_COURSE_COMBO), fetchTableData(principal));
    }

    @RequestMapping(value = "fetchMasterBranchCombo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchMasterBranchCombo(Principal principal) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_MASTER_BRANCH_COMBO), fetchTableData(principal));
    }

}
