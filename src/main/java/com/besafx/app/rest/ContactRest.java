package com.besafx.app.rest;

import com.besafx.app.entity.Contact;
import com.besafx.app.service.ContactService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/contact/")
public class ContactRest {

    private final static Logger LOG = LoggerFactory.getLogger(ContactRest.class);

    @Autowired
    private ContactService contactService;

    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_CONTACT_CREATE')")
    public Contact create(@RequestBody Contact contact) {
        return contactService.save(contact);
    }

    @PutMapping(value = "update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_CONTACT_UPDATE')")
    public Contact update(@RequestBody Contact contact) {
        Contact object = contactService.findOne(contact.getId());
        if (object != null) {
            return contactService.save(contact);
        } else {
            return null;
        }
    }

    @DeleteMapping(value = "delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_CONTACT_DELETE')")
    public void delete(@PathVariable Long id) {
        Contact object = contactService.findOne(id);
        if (object != null) {
            contactService.delete(id);
        }
    }

    @GetMapping(value = "findAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Contact> findAll() {
        return Lists.newArrayList(contactService.findAll());
    }

    @GetMapping(value = "findOne/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Contact findOne(@PathVariable Long id) {
        return contactService.findOne(id);
    }

}
