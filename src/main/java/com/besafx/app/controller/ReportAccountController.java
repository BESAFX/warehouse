package com.besafx.app.controller;
import com.besafx.app.component.ReportExporter;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.*;
import com.besafx.app.enums.ExportType;
import com.besafx.app.service.*;
import com.besafx.app.util.DateConverter;
import com.besafx.app.util.WrapperUtil;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.security.Principal;
import java.util.*;

@RestController
public class ReportAccountController {

    @Autowired
    private PersonService personService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private MasterService masterService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ReportExporter reportExporter;

    @RequestMapping(value = "/report/AccountByBranch/{branchId}", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void printAccountByBranch(
            @PathVariable(value = "branchId") Long branchId,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response) throws Exception {
        Branch branch = branchService.findOne(branchId);
        if (branch == null) {
            return;
        }
        /**
         * Insert Parameters
         */
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المملكة العربية السعودية");
        param1.append("\n");
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        map.put("param1", param1.toString());
        StringBuilder param2 = new StringBuilder();
        param2.append("كشف بيانات الطلبة المسجلين للفرع/");
        param2.append(branch.getName());
        map.put("param2", param2.toString());
        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");
        /**
         * Insert Data
         */
        List<Account> accounts;
        if (startDate == null && endDate == null) {
            accounts = accountService.findByCourseMasterBranch(branch);
        } else {
            accounts = accountService.findByCourseMasterBranchAndRegisterDateBetween(branch, new DateTime(startDate).withTimeAtStartOfDay().toDate(), new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate());
            map.put("param2", map.get("param2").toString()
                    .concat(" ")
                    .concat("التاريخ من: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(startDate.longValue()))
                    .concat(" ")
                    .concat("التاريخ إلى: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(endDate.longValue())));
        }
        List<WrapperUtil> list = initDateList(accounts);
        map.put("ItemDataSource", new JRBeanCollectionDataSource(list));
        ClassPathResource jrxmlFile = new ClassPathResource("/report/account/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/AccountByMasterCategories", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void AccountByMasterCategories(
            @RequestParam(value = "branchIds") List<Long> branchIds,
            @RequestParam(value = "masterCategoryIds") List<Long> masterCategoryIds,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            Principal principal,
            HttpServletResponse response) throws Exception {
        Person caller = personService.findByEmail(principal.getName());
        Map<String, Object> map = new HashMap<>();
        map.put("LOGO", new URL(caller.getBranch().getLogo()).openStream());
        map.put("TITLE", title);
        map.put("CALLER", caller);
        //Start Search
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(branchIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("course").get("master").get("branch").get("id").in(value)));
        Optional.ofNullable(masterCategoryIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("course").get("master").get("masterCategory").get("id").in(value)));
        Optional.ofNullable(startDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("registerDate"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(endDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("registerDate"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        map.put("ACCOUNTS", getList(predicates));
        //End Search
        ClassPathResource jrxmlFile = new ClassPathResource("/report/account/ReportByMasterCategory.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/AccountByMaster/{masterId}", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void printAccountByMaster(
            @PathVariable(value = "masterId") Long masterId,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response) throws Exception {
        Master master = masterService.findOne(masterId);
        if (master == null) {
            return;
        }
        /**
         * Insert Parameters
         */
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المملكة العربية السعودية");
        param1.append("\n");
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        map.put("param1", param1.toString());
        StringBuilder param2 = new StringBuilder();
        param2.append("كشف بيانات الطلبة المسجلين للتخصص/ ");
        param2.append(master.getName());
        param2.append(" ");
        param2.append("التابع للفرع/ ");
        param2.append(master.getBranch().getName());
        map.put("param2", param2.toString());
        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");
        /**
         * Insert Data
         */
        List<Account> accounts;
        if (startDate == null && endDate == null) {
            accounts = accountService.findByCourseMaster(master);
        } else {
            accounts = accountService.findByCourseMasterAndRegisterDateBetween(master, new DateTime(startDate).withTimeAtStartOfDay().toDate(), new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate());
            map.put("param2", map.get("param2").toString()
                    .concat(" ")
                    .concat("التاريخ من: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(startDate.longValue()))
                    .concat(" ")
                    .concat("التاريخ إلى: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(endDate.longValue())));
        }
        List<WrapperUtil> list = initDateList(accounts);
        map.put("ItemDataSource", new JRBeanCollectionDataSource(list));
        ClassPathResource jrxmlFile = new ClassPathResource("/report/account/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/AccountByCourse/{courseId}", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void printAccountByCourse(
            @PathVariable(value = "courseId") Long courseId,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response) throws Exception {
        Course course = courseService.findOne(courseId);
        if (course == null) {
            return;
        }
        /**
         * Insert Parameters
         */
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المملكة العربية السعودية");
        param1.append("\n");
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        map.put("param1", param1.toString());
        StringBuilder param2 = new StringBuilder();
        param2.append("كشف بيانات الطلبة المسجلين للدورة رقم/ ");
        param2.append(course.getCode());
        param2.append(" ");
        param2.append("التابع للتخصص/ ");
        param2.append(course.getMaster().getName());
        param2.append(" ");
        param2.append("التابع للفرع/ ");
        param2.append(course.getMaster().getBranch().getName());
        map.put("param2", param2.toString());
        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");
        /**
         * Insert Data
         */
        List<Account> accounts;
        if (startDate == null && endDate == null) {
            accounts = accountService.findByCourse(course);
        } else {
            accounts = accountService.findByCourseAndRegisterDateBetween(course, new DateTime(startDate).withTimeAtStartOfDay().toDate(), new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate());
            map.put("param2", map.get("param2").toString()
                    .concat(" ")
                    .concat("التاريخ من: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(startDate.longValue()))
                    .concat(" ")
                    .concat("التاريخ إلى: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(endDate.longValue())));
        }
        List<WrapperUtil> list = initDateList(accounts);
        map.put("ItemDataSource", new JRBeanCollectionDataSource(list));
        ClassPathResource jrxmlFile = new ClassPathResource("/report/account/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/account/contract/{id}", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void printContract(
            @PathVariable(value = "id") Long id,
            HttpServletResponse response) throws Exception {
        Account account = accountService.findOne(id);
        if (account == null) {
            throw new CustomException("عفواً، لا يوجد هذا الحساب");
        }
        /**
         * Insert Parameters
         */
        Map<String, Object> map = new HashMap<>();
        map.put("ACCOUNT", account);
        map.put("LOGO", new URL(account.getCourse().getMaster().getBranch().getLogo()).openStream());
        map.put("TITLE", "عقد إشتراك ب".concat(account.getCourse().getMaster().getMasterCategory().getName()));
        ClassPathResource jrxmlFile = new ClassPathResource("/report/account/"+account.getCourse().getMaster().getMasterCategory().getReportFileName()+".jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(ExportType.PDF, response, jasperPrint);
    }

    private List<WrapperUtil> initDateList(List<Account> accountList) {
        List<WrapperUtil> list = new ArrayList<>();
        accountList.stream().forEach(row -> {
            WrapperUtil wrapperUtil = new WrapperUtil();
            Optional.ofNullable(row).ifPresent(account -> wrapperUtil.setObj1(account));
            list.add(wrapperUtil);
        });
        return list;
    }

    private List<Offer> getList(List<Specification> predicates) {
        List<Offer> list = new ArrayList<>();
        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            list.addAll(accountService.findAll(result));
        }
        return list;
    }
}
