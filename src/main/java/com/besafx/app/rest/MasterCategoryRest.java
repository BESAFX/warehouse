package com.besafx.app.rest;

import com.besafx.app.config.CustomException;
import com.besafx.app.entity.MasterCategory;
import com.besafx.app.entity.Person;
import com.besafx.app.service.MasterCategoryService;
import com.besafx.app.service.MasterService;
import com.besafx.app.service.PersonService;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping(value = "/api/masterCategory/")
public class MasterCategoryRest {
    
    public static final String FILTER_TABLE = "**,masters[id],person[id,contact[id,firstName,forthName]]";
    public static final String FILTER_MASTER_CATEGORY_COMBO = "id,code,name";

    @Autowired
    private MasterCategoryService masterCategoryService;

    @Autowired
    private MasterService masterService;

    @Autowired
    private PersonService personService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_MASTER_CATEGORY_CREATE')")
    @Transactional
    public String create(@RequestBody MasterCategory masterCategory, Principal principal) {
        MasterCategory topMasterCategory = masterCategoryService.findTopByOrderByCodeDesc();
        if (topMasterCategory == null) {
            masterCategory.setCode(1);
        } else {
            masterCategory.setCode(topMasterCategory.getCode() + 1);
        }
        Person caller = personService.findByEmail(principal.getName());
        masterCategory.setPerson(caller);
        masterCategory.setDate(new DateTime().toDate());
        masterCategory = masterCategoryService.save(masterCategory);
        notificationService.notifyOne(Notification
                .builder()
                .title("التخصصات" )
                .message("تم انشاء تصنيف جديد بنجاح")
                .type("success")
                .icon("fa-plus-square")
                .build(), principal.getName());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), masterCategory);
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_MASTER_CATEGORY_UPDATE')")
    @Transactional
    public String update(@RequestBody MasterCategory masterCategory, Principal principal) {
        if (masterCategoryService.findByCodeAndIdIsNot(masterCategory.getCode(), masterCategory.getId()) != null) {
            throw new CustomException("هذا الكود مستخدم سابقاً، فضلاً قم بتغير الكود.");
        }
        MasterCategory object = masterCategoryService.findOne(masterCategory.getId());
        if (object != null) {
            Person caller = personService.findByEmail(principal.getName());
            masterCategory.setPerson(caller);
            masterCategory.setDate(new DateTime().toDate());
            masterCategory = masterCategoryService.save(masterCategory);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("التخصصات")
                    .message("تم تعديل بيانات التصنيف بنجاح")
                    .type("warning")
                    .icon("fa-edit")
                    .build(), principal.getName());
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), masterCategory);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_MASTER_CATEGORY_DELETE')")
    @Transactional
    public void delete(@PathVariable Long id, Principal principal) {
        MasterCategory masterCategory = masterCategoryService.findOne(id);
        if (masterCategory != null) {
            masterCategory.getMasters().stream().forEach(master -> {
                master.setMasterCategory(null);
                masterService.save(master);
            });
            masterCategoryService.delete(id);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("التخصصات")
                    .message("تم حذف التصنيف بنجاح")
                    .type("error")
                    .icon("fa-trash")
                    .build(), principal.getName());
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAll() {
        List<MasterCategory> list = Lists.newArrayList(masterCategoryService.findAll());
        list.sort(Comparator.comparing(MasterCategory::getCode));
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), list);
    }

    @RequestMapping(value = "findAllCombo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAllCombo() {
        List<MasterCategory> list = Lists.newArrayList(masterCategoryService.findAll());
        list.sort(Comparator.comparing(MasterCategory::getCode));
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_MASTER_CATEGORY_COMBO), list);
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), masterCategoryService.findOne(id));
    }
}
