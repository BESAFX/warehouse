package com.besafx.app.controller;

import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Account;
import com.besafx.app.entity.Branch;
import com.besafx.app.entity.Course;
import com.besafx.app.entity.Master;
import com.besafx.app.service.AccountService;
import com.besafx.app.service.BranchService;
import com.besafx.app.service.CourseService;
import com.besafx.app.service.MasterService;
import com.besafx.app.util.DateConverter;
import com.besafx.app.util.WrapperUtil;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@RestController
public class ReportAccountController {

    @Autowired
    private BranchService branchService;

    @Autowired
    private MasterService masterService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/report/AccountByBranch/{branchId}", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void printAccountByBranch(
            @PathVariable(value = "branchId") Long branchId,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response) throws JRException, IOException {

        Branch branch = branchService.findOne(branchId);

        if (branch == null) {
            return;
        }


        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=Report.pdf");
        final OutputStream outStream = response.getOutputStream();

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
            accounts = accountService.findByCourseMasterBranchAndRegisterDateBetween(branch, new Date(startDate), new Date(endDate));
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

        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/report/AccountByMaster/{masterId}", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void printAccountByMaster(
            @PathVariable(value = "masterId") Long masterId,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response) throws JRException, IOException {

        Master master = masterService.findOne(masterId);

        if (master == null) {
            return;
        }


        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=Report.pdf");
        final OutputStream outStream = response.getOutputStream();

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
            accounts = accountService.findByCourseMasterAndRegisterDateBetween(master, new Date(startDate), new Date(endDate));
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

        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/report/AccountByCourse/{courseId}", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void printAccountByCourse(
            @PathVariable(value = "courseId") Long courseId,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response) throws JRException, IOException {

        Course course = courseService.findOne(courseId);

        if (course == null) {
            return;
        }


        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=Report.pdf");
        final OutputStream outStream = response.getOutputStream();

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
            accounts = accountService.findByCourseAndRegisterDateBetween(course, new Date(startDate), new Date(endDate));
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

        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/report/account/contract/{id}", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void printContract(@PathVariable(value = "id") Long id, HttpServletResponse response) throws JRException, IOException {

        Account account = accountService.findOne(id);
        if (account == null) {
            throw new CustomException("عفواً، لا يوجد هذا الحساب");
        }
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=Report.pdf");
        final OutputStream outStream = response.getOutputStream();

        /**
         * Insert Parameters
         */
        Map<String, Object> map = new HashMap<>();
        map.put("BRANCH_NAME", account.getCourse().getMaster().getBranch().getName());
        map.put("STUDENT_NAME", account.getStudent().getContact().getFirstName() + " " + account.getStudent().getContact().getSecondName() + " " + account.getStudent().getContact().getThirdName() + " " + account.getStudent().getContact().getForthName());
        map.put("STUDENT_NUMBER", account.getStudent().getCode() + " " + account.getCourse().getCode() + " " + account.getCourse().getMaster().getCode() + " " + account.getCourse().getMaster().getBranch().getCode() + " " + DateConverter.getYearShortcut(account.getRegisterDate()));
        map.put("STUDENT_IDENTITY_NUMBER", account.getStudent().getContact().getIdentityNumber());
        map.put("STUDENT_MOBILE", account.getStudent().getContact().getMobile());
        map.put("MASTER_NAME", account.getCourse().getMaster().getName());
        map.put("MASTER_PERIOD", account.getCourse().getMaster().getPeriod());
        map.put("COURSE_START_DATE", DateConverter.getHijriStringFromDateRTL(account.getCourse().getStartDate()));
        map.put("COURSE_END_DATE", DateConverter.getHijriStringFromDateRTL(account.getCourse().getEndDate()));
        map.put("COURSE_PRICE", account.getCoursePrice().toString());

        ClassPathResource jrxmlFile = new ClassPathResource("/report/account/Contract.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);

        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
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
}
