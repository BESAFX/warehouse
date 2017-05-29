package com.besafx.app.rest;

import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Bank;
import com.besafx.app.entity.Person;
import com.besafx.app.service.BankService;
import com.besafx.app.service.PersonService;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/bank/")
public class BankRest {

    @Autowired
    private PersonService personService;

    @Autowired
    private BankService bankService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BANK_CREATE')")
    public Bank create(@RequestBody Bank bank, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        bank.setLastUpdate(new Date());
        bank.setLastPerson(person);
        bank.setStock(bank.getStartAmount());
        bank = bankService.save(bank);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على البنوك")
                .message("تم اضافة بنك جديد بنجاح")
                .type("success")
                .icon("fa-database")
                .build(), principal.getName());
        notificationService.notifyAllExceptMe(Notification
                .builder()
                .title("العمليات على البنوك")
                .message("تم اضافة بنك جديد بواسطة " + principal.getName())
                .type("warning")
                .icon("fa-database")
                .build());
        return bank;
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BANK_UPDATE')")
    public Bank update(@RequestBody Bank bank, Principal principal) {
        Bank object = bankService.findOne(bank.getId());
        if (object != null) {
            Person person = personService.findByEmail(principal.getName());
            bank.setLastUpdate(new Date());
            bank.setLastPerson(person);
            bank = bankService.save(bank);

            return bank;
        } else {
            throw new CustomException("عفواً، لا يوجد هذا الحساب");
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BANK_DELETE')")
    public void delete(@PathVariable Long id) {
        Bank object = bankService.findOne(id);
        if (object != null) {
            bankService.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Bank> findAll() {
        return Lists.newArrayList(bankService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Bank findOne(@PathVariable Long id) {
        return bankService.findOne(id);
    }

    @RequestMapping(value = "filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Bank> filter(
            @RequestParam(value = "code", required = false) final Long code,
            @RequestParam(value = "name", required = false) final String name,
            @RequestParam(value = "branch", required = false) final String branch,
            @RequestParam(value = "stockFrom", required = false) final Long stockFrom,
            @RequestParam(value = "stockTo", required = false) final Long stockTo) {

        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(code).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("code"), "%" + value + "%")));
        Optional.ofNullable(name).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("name"), "%" + value + "%")));
        Optional.ofNullable(branch).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("branch"), "%" + value + "%")));
        Optional.ofNullable(stockFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("stock"), value)));
        Optional.ofNullable(stockTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("stock"), value)));

        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            return Lists.newArrayList(bankService.findAll(result));
        } else {
            return findAll();
        }
    }

}
