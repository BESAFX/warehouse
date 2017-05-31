package com.besafx.app.rest;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Bank;
import com.besafx.app.entity.Person;
import com.besafx.app.service.BankService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/bank/")
public class BankRest {

    private final static Logger log = LoggerFactory.getLogger(BankRest.class);

    @Autowired
    private CompanyService companyService;

    @Autowired
    private BranchService branchService;

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
                .title("العمليات على قواعد البيانات")
                .message("تم اضافة حساب بنك جديد بنجاح")
                .type("success")
                .icon("fa-plus-square")
                .build(), principal.getName());
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
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على قواعد البيانات")
                    .message("تم تعديل بيانات حساب البنك رقم " + bank.getCode() + " بنجاح.")
                    .type("warn")
                    .icon("fa-plus-square")
                    .build(), principal.getName());
            return bank;
        } else {
            throw new CustomException("عفواً، لا يوجد هذا الحساب");
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BANK_DELETE')")
    public void delete(@PathVariable Long id, Principal principal) {
        Bank object = bankService.findOne(id);
        if (object != null) {
            bankService.delete(id);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على قواعد البيانات")
                    .message("تم حذف حساب بنك بنجاح")
                    .type("error")
                    .icon("fa-plus-square")
                    .build(), principal.getName());
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
            @RequestParam(value = "branchName", required = false) final String branchName,
            @RequestParam(value = "stockFrom", required = false) final Long stockFrom,
            @RequestParam(value = "stockTo", required = false) final Long stockTo,
            @RequestParam(value = "branch", required = false) final Long branch) {
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(code).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("code"), "%" + value + "%")));
        Optional.ofNullable(name).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("name"), "%" + value + "%")));
        Optional.ofNullable(branchName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("branchName"), "%" + value + "%")));
        Optional.ofNullable(stockFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("stock"), value)));
        Optional.ofNullable(stockTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("stock"), value)));
        Optional.ofNullable(branch).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("branch").get("id"), value)));
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

    @RequestMapping(value = "fetchTableData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Bank> fetchTableData(Principal principal) {
        try {
            Person person = personService.findByEmail(principal.getName());
            List<Bank> list = new ArrayList<>();
            companyService.findByManager(person).stream().forEach(company -> branchService.findByCompany(company).stream().forEach(branch -> list.addAll(branch.getBanks())));
            branchService.findByManager(person).stream().forEach(branch -> list.addAll(branch.getBanks()));
            list.addAll(person.getBranch().getBanks());
            return list.stream().filter(DistinctFilter.distinctByKey(Bank::getId)).collect(Collectors.toList());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

}
