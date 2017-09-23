package com.besafx.app.rest;

import com.besafx.app.entity.Contact;
import com.besafx.app.entity.Student;
import com.besafx.app.service.ContactService;
import com.besafx.app.service.StudentService;
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

@RestController
@RequestMapping(value = "/api/student/")
public class StudentRest {

    private final static Logger log = LoggerFactory.getLogger(StudentRest.class);

    public static final String FILTER_TABLE = "**";

    @Autowired
    private StudentService studentService;

    @Autowired
    private ContactService contactService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_STUDENT_CREATE')")
    public String create(@RequestBody Student student) {
        if (student.getContact().getId() == null) {
            Contact contact = contactService.save(student.getContact());
            student.setContact(contact);
        }
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), studentService.save(student));
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_STUDENT_UPDATE')")
    public String update(@RequestBody Student student) {
        Student object = studentService.findOne(student.getId());
        if (object != null) {
            student.setContact(contactService.save(student.getContact()));
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), studentService.save(student));
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_STUDENT_DELETE')")
    public void delete(@PathVariable Long id) {
        Student object = studentService.findOne(id);
        if (object != null) {
            studentService.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAll() {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), Lists.newArrayList(studentService.findAll()));
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), studentService.findOne(id));
    }

}
