package com.besafx.app.rest;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.*;
import com.besafx.app.service.*;
import com.besafx.app.util.DistinctFilter;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/course/")
public class CourseRest {

    private final static Logger log = LoggerFactory.getLogger(CourseRest.class);

    @Autowired
    private CompanyService companyService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private PersonService personService;

    @Autowired
    private MasterService masterService;

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
    @PreAuthorize("hasRole('ROLE_COURSE_CREATE')")
    @JsonView(Views.CourseTable.class)
    public Course create(@RequestBody Course course, Principal principal) {
        if (courseService.findByCodeAndMaster(course.getCode(), course.getMaster()) != null) {
            throw new CustomException("هذا الكود مستخدم سابقاً، فضلاً قم بتغير الكود.");
        }
        Person person = personService.findByEmail(principal.getName());
        course.setLastUpdate(new Date());
        course.setLastPerson(person);
        course = courseService.save(course);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على الدورات")
                .message("تم إنشاء دورة جديدة بنجاح")
                .type("success")
                .icon("fa-plus-square")
                .build(), principal.getName());
        return course;
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_COURSE_UPDATE')")
    @JsonView(Views.CourseTable.class)
    public Course update(@RequestBody Course course, Principal principal) {
        if (courseService.findByCodeAndMasterAndIdIsNot(course.getCode(), course.getMaster(), course.getId()) != null) {
            throw new CustomException("هذا الكود مستخدم سابقاً، فضلاً قم بتغير الكود.");
        }
        Person person = personService.findByEmail(principal.getName());
        Course object = courseService.findOne(course.getId());
        if (object != null) {
            course.setLastUpdate(new Date());
            course.setLastPerson(person);
            course = courseService.save(course);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على الدورات")
                    .message("تم تعديل بيانات الدورة بنجاح")
                    .type("success")
                    .icon("fa-edit")
                    .build(), principal.getName());
            return course;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_COURSE_DELETE')")
    public void delete(@PathVariable Long id, Principal principal) {
        Course object = courseService.findOne(id);
        if (object != null) {
            List<Account> accounts = accountService.findByCourse(object);
            List<Payment> payments = paymentService.findByAccountIn(accounts);
            paymentService.delete(payments);
            accountService.delete(accounts);
            courseService.delete(object);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على الدورات")
                    .message("تم حذف الدورة وكل ما يتعلق بها من طلاب وسندات بنجاح")
                    .type("success")
                    .icon("fa-trash")
                    .build(), principal.getName());
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Course> findAll() {
        return Lists.newArrayList(courseService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Course findOne(@PathVariable Long id) {
        return courseService.findOne(id);
    }

    @RequestMapping(value = "count", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long count() {
        return courseService.count();
    }

    @RequestMapping(value = "findByMaster/{masterId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Course> findByMaster(@PathVariable(value = "masterId") Long masterId) {
        return courseService.findByMaster(masterService.findOne(masterId));
    }

    @RequestMapping(value = "fetchTableData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Course> fetchTableData(Principal principal) {
        try {
            Person person = personService.findByEmail(principal.getName());
            List<Course> list = new ArrayList<>();
            ///
            companyService.findByManager(person).stream().forEach(company ->
                    branchService.findByCompany(company).stream().forEach(branch ->
                            masterService.findByBranch(branch).stream().forEach(master ->
                                    list.addAll(courseService.findByMaster(master)))));
            ///
            branchService.findByManager(person).stream().forEach(branch ->
                    masterService.findByBranch(branch).stream().forEach(master ->
                            list.addAll(courseService.findByMaster(master))));
            ///
            masterService.findByBranch(person.getBranch()).stream().forEach(master ->
                    list.addAll(courseService.findByMaster(master)));
            ///
            return list.stream().filter(DistinctFilter.distinctByKey(Course::getId)).collect(Collectors.toList());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    @RequestMapping(value = "fetchTableDataSummery", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @JsonView(Views.Summery.class)
    public List<Course> fetchTableDataSummery(Principal principal) {
        return fetchTableData(principal);
    }

    @RequestMapping(value = "fetchTable", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @JsonView(Views.CourseTable.class)
    public List<Course> fetchTable(Principal principal) {
        return fetchTableData(principal);
    }

}
