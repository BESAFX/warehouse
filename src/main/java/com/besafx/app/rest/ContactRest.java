package com.besafx.app.rest;

import com.besafx.app.entity.Contact;
import com.besafx.app.service.ContactService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/contact/")
public class ContactRest {

    @Autowired
    private ContactService contactRepository;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_CONTACT_CREATE')")
    public Contact create(@RequestBody Contact contact) {
        return contactRepository.save(contact);
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_CONTACT_UPDATE')")
    public Contact update(@RequestBody Contact contact) {
        Contact object = contactRepository.findOne(contact.getId());
        if (object != null) {
            return contactRepository.save(contact);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_CONTACT_DELETE')")
    public void delete(@PathVariable Long id) {
        Contact object = contactRepository.findOne(id);
        if (object != null) {
            contactRepository.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Contact> findAll() {
        return Lists.newArrayList(contactRepository.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Contact findOne(@PathVariable Long id) {
        return contactRepository.findOne(id);
    }

}
