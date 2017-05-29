package com.besafx.app.rest;

import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Offer;
import com.besafx.app.entity.Person;
import com.besafx.app.service.BranchService;
import com.besafx.app.service.MasterService;
import com.besafx.app.service.OfferService;
import com.besafx.app.service.PersonService;
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
@RequestMapping(value = "/api/offer/")
public class OfferRest {

    private final static Logger log = LoggerFactory.getLogger(OfferRest.class);

    @Autowired
    private PersonService personService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private OfferService offerService;

    @Autowired
    private MasterService masterService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_OFFER_CREATE')")
    public Offer create(@RequestBody Offer offer, Principal principal) {
        try {
            Integer lastCode = offerService.findLastCodeByMasterBranch(offer.getMaster().getBranch().getId());
            if (lastCode == null) {
                offer.setCode(1);
            } else {
                offer.setCode(lastCode + 1);
            }
            offer.setLastUpdate(new Date());
            offer.setLastPerson(personService.findByEmail(principal.getName()));
            offer = offerService.save(offer);
            return offer;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_OFFER_UPDATE')")
    public Offer update(@RequestBody Offer offer, Principal principal) {
        Offer object = offerService.findOne(offer.getId());
        if (object != null) {
            offer.setLastUpdate(new Date());
            offer.setLastPerson(personService.findByEmail(principal.getName()));
            offer = offerService.save(offer);
            return offer;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_OFFER_DELETE')")
    public void delete(@PathVariable Long id) {
        Offer object = offerService.findOne(id);
        if (object != null) {
            offerService.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Offer> findAll() {
        return Lists.newArrayList(offerService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Offer findOne(@PathVariable Long id) {
        return offerService.findOne(id);
    }

    @RequestMapping(value = "findByBranch/{branchId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Offer> findByBranch(@PathVariable(value = "branchId") Long branchId) {
        return offerService.findByMasterBranch(branchService.findOne(branchId));
    }

    @RequestMapping(value = "findByMaster/{masterId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Offer> findByMaster(@PathVariable(value = "masterId") Long masterId) {
        return offerService.findByMaster(masterService.findOne(masterId));
    }

    @RequestMapping(value = "findCustomersByBranch/{branchId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> findCustomersByBranch(@PathVariable(value = "branchId") Long branchId) {
        List<String> list = offerService
                .findByMasterBranch(branchService.findOne(branchId))
                .stream()
                .map(value -> value.getCustomerName())
                .distinct()
                .collect(Collectors.toList());
        return list;
    }

    @RequestMapping(value = "count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Long count() {
        return offerService.count();
    }

    @RequestMapping(value = "fetchTableData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Offer> fetchTableData(Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        List<Offer> list = new ArrayList<>();
        return findAll();
    }

    @RequestMapping(value = "fetchCount", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Long fetchCount(Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        return offerService.count();
    }

    @RequestMapping(value = "filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Offer> filter(
            @RequestParam(value = "codeFrom", required = false) final Long codeFrom,
            @RequestParam(value = "codeTo", required = false) final Long codeTo,
            @RequestParam(value = "dateFrom", required = false) final Long dateFrom,
            @RequestParam(value = "dateTo", required = false) final Long dateTo,
            @RequestParam(value = "customerName", required = false) final String customerName,
            @RequestParam(value = "customerIdentityNumber", required = false) final String customerIdentityNumber,
            @RequestParam(value = "customerMobile", required = false) final String customerMobile,
            @RequestParam(value = "masterPriceFrom", required = false) final Long masterPriceFrom,
            @RequestParam(value = "masterPriceTo", required = false) final Long masterPriceTo,
            @RequestParam(value = "branch", required = false) final Long branch,
            @RequestParam(value = "master", required = false) final Long master,
            @RequestParam(value = "registered", required = false) final Boolean registered) {

        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(codeFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(codeTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(dateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("lastUpdate"), new Date(value))));
        Optional.ofNullable(dateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("lastUpdate"), new Date(value))));
        Optional.ofNullable(customerName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("customerName"), "%" + value + "%")));
        Optional.ofNullable(customerIdentityNumber).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("customerIdentityNumber"), "%" + value + "%")));
        Optional.ofNullable(customerMobile).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("customerMobile"), "%" + value + "%")));
        Optional.ofNullable(masterPriceFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("masterPrice"), value)));
        Optional.ofNullable(masterPriceTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("masterPrice"), value)));
        Optional.ofNullable(branch).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("master").get("branch").get("id"), value)));
        Optional.ofNullable(master).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("master").get("id"), value)));
        Optional.ofNullable(registered).ifPresent(value -> predicates.add((root, cq, cb) -> value ? cb.isTrue(root.get("registered")) : cb.isFalse(root.get("registered"))));

        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            return Lists.newArrayList(offerService.findAll(result));
        } else {
            throw new CustomException("فضلاً ادخل على الاقل عنصر واحد للبحث");
        }
    }
}
