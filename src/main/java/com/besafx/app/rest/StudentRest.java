package com.besafx.app.rest;

import com.besafx.app.entity.Contact;
import com.besafx.app.entity.Student;
import com.besafx.app.repository.StudentRepository;
import com.besafx.app.service.ContactService;
import com.besafx.app.service.StudentService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/student/")
public class StudentRest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private StudentRepository studentRepository;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_STUDENT_CREATE')")
    public Student create(@RequestBody Student student) {
        if (student.getContact().getId() == null) {
            Contact contact = contactService.save(student.getContact());
            student.setContact(contact);
        }
        Integer maxCode = studentService.findMaxCode();
        if (maxCode == null) {
            student.setCode(1);
        } else {
            student.setCode(maxCode + 1);
        }
        return studentService.save(student);
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_STUDENT_UPDATE')")
    public Student update(@RequestBody Student student) {
        Student object = studentService.findOne(student.getId());
        if (object != null) {
            student.setContact(contactService.save(student.getContact()));
            return studentService.save(student);
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
    public List<Student> findAll() {
        return Lists.newArrayList(studentService.findAll());
    }

    @RequestMapping(value = "findUnRegistered/{branchId}/{masterId}/{courseId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Student> findUnRegistered(@PathVariable Long branchId, @PathVariable Long masterId, @PathVariable Long courseId) {
        return studentRepository.findUnRegistered(branchId, masterId, courseId);
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Student findOne(@PathVariable Long id) {
        return studentService.findOne(id);
    }

    @RequestMapping(value = "findByCode", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Student findByCode(@RequestParam(value = "code") Integer code) {
        return studentService.findByCode(code);
    }

}
