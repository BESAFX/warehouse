package com.besafx.app.rest;

import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Offer;
import com.besafx.app.entity.Person;
import com.besafx.app.search.OfferSearch;
import com.besafx.app.service.BranchService;
import com.besafx.app.service.MasterService;
import com.besafx.app.service.OfferService;
import com.besafx.app.service.PersonService;
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
@RequestMapping(value = "/api/offer/")
public class OfferRest {

    private final static Logger log = LoggerFactory.getLogger(OfferRest.class);

    public static final String FILTER_TABLE = "**,accountsByMobile[id,registerDate,course[id,code,name,master[id,code,name,branch[id,code,name]]]],master[id,code,name,branch[id,code,name]],lastPerson[id,contact[id,firstName,forthName]],calls[**,person[id,contact[id,firstName,forthName]],-offer]";

    @Autowired
    private PersonService personService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private OfferService offerService;

    @Autowired
    private OfferSearch offerSearch;

    @Autowired
    private MasterService masterService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_OFFER_CREATE')")
    public String create(@RequestBody Offer offer, Principal principal) {
        try {
            Person person = personService.findByEmail(principal.getName());
            Offer topOffer = offerService.findTopByMasterBranchOrderByCodeDesc(person.getBranch());
            if (topOffer == null) {
                offer.setCode(1);
            } else {
                offer.setCode(topOffer.getCode() + 1);
            }
            offer.setLastUpdate(new Date());
            offer.setLastPerson(person);
            offer = offerService.save(offer);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على العروض")
                    .message("تم انشاء عرض جديد بنجاح")
                    .type("success")
                    .icon("fa-plus-square")
                    .build(), principal.getName());
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), offer);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_OFFER_UPDATE')")
    public String update(@RequestBody Offer offer, Principal principal) {
        if (offerService.findByCodeAndMasterBranchAndIdIsNot(offer.getCode(), offer.getMaster().getBranch(), offer.getId()) != null) {
            throw new CustomException("هذا الكود مستخدم سابقاً، فضلاً قم بتغير الكود.");
        }
        Offer object = offerService.findOne(offer.getId());
        if (object != null) {
            offer = offerService.save(offer);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على الشركات")
                    .message("تم تعديل بيانات العرض بنجاح")
                    .type("success")
                    .icon("fa-edit")
                    .build(), principal.getName());
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), offer);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_OFFER_DELETE')")
    public void delete(@PathVariable Long id, Principal principal) {
        Offer object = offerService.findOne(id);
        if (object != null) {
            if(!principal.getName().equals(object.getLastPerson().getEmail())){
                throw new CustomException("لا يمكنك حذف عرض لم تقم بإضافته");
            }
            offerService.delete(id);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على الشركات")
                    .message("تم حذف العرض بنجاح")
                    .type("success")
                    .icon("fa-trash")
                    .build(), principal.getName());
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAll() {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), Lists.newArrayList(offerService.findAll()));
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), offerService.findOne(id));
    }

    @RequestMapping(value = "findByCustomerMobile/{customerMobile}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByCustomerMobile(@PathVariable String customerMobile) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), offerService.findByCustomerMobile(customerMobile));
    }

    @RequestMapping(value = "findByBranch/{branchId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByBranch(@PathVariable(value = "branchId") Long branchId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), offerService.findByMasterBranch(branchService.findOne(branchId)));
    }

    @RequestMapping(value = "findByMaster/{masterId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByMaster(@PathVariable(value = "masterId") Long masterId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), offerService.findByMaster(masterService.findOne(masterId)));
    }

    @RequestMapping(value = "findCustomersByBranch/{branchId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findCustomersByBranch(@PathVariable(value = "branchId") Long branchId) {
        List<String> list = offerService
                .findByMasterBranch(branchService.findOne(branchId))
                .stream()
                .map(value -> value.getCustomerName())
                .distinct()
                .collect(Collectors.toList());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), list);
    }

    @RequestMapping(value = "count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Long count() {
        return offerService.count();
    }

    @RequestMapping(value = "filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String filter(
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
            @RequestParam(value = "personId", required = false) final Long personId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                offerSearch.search(codeFrom, codeTo, dateFrom, dateTo, customerName, customerIdentityNumber, customerMobile, masterPriceFrom, masterPriceTo, branch, master, personId));
    }
}
