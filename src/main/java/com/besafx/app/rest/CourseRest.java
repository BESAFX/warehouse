package com.besafx.app.rest;

import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Account;
import com.besafx.app.entity.Course;
import com.besafx.app.entity.Payment;
import com.besafx.app.entity.Person;
import com.besafx.app.service.*;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/course/")
public class CourseRest {

    private final static Logger log = LoggerFactory.getLogger(CourseRest.class);

    private final String FILTER_TABLE = "**,master[id,code,name,branch[id,code,name]],lastPerson[id,contact[id,firstName,forthName]],accounts[id]";

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
    public String create(@RequestBody Course course, Principal principal) {
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
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), course);
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_COURSE_UPDATE')")
    public String update(@RequestBody Course course, Principal principal) {
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
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), course);
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
    public String findAll() {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), Lists.newArrayList(courseService.findAll()));
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), courseService.findOne(id));
    }

    @RequestMapping(value = "count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Long count() {
        return courseService.count();
    }

    @RequestMapping(value = "findByMaster/{masterId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByMaster(@PathVariable(value = "masterId") Long masterId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), courseService.findByMaster(masterService.findOne(masterId)));
    }

    @RequestMapping(value = "fetchTableData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchTableData(Principal principal) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                personService.findByEmail(principal.getName())
                        .getBranch()
                        .getMasters()
                        .stream()
                        .flatMap(master -> master.getCourses().stream()).collect(Collectors.toList()));
    }

}
