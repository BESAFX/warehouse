package com.besafx.app.controller;
import com.besafx.app.entity.Branch;
import com.besafx.app.entity.Course;
import com.besafx.app.entity.Master;
import com.besafx.app.entity.Payment;
import com.besafx.app.rest.AccountRest;
import com.besafx.app.service.*;
import com.besafx.app.util.DateConverter;
import com.besafx.app.util.WrapperUtil;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ReportPaymentController {

    @Autowired
    private BranchService branchService;

    @Autowired
    private MasterService masterService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRest accountRest;

    @Autowired
    private PaymentService paymentService;

    @RequestMapping(value = "/report/PaymentByBranch/{branchId}", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void printPaymentByBranch(
            @PathVariable("branchId") Long branchId,
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
        StringBuilder param2 = new StringBuilder();
        param2.append("الطلبة المسجلين حسب الفرع ودفعاتهم ");
        param2.append(" --- ");
        param2.append("الفرع: " + "(" + branch.getName() + ")");
        map.put("param1", param1.toString());
        map.put("param2", param2.toString());
        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");
        /**
         * Insert Data
         */
        List<Payment> paymentList;
        if (startDate == null && endDate == null) {
            paymentList = paymentService.findByAccountCourseMasterBranch(branch);
        } else {
            paymentList = paymentService.findByAccountCourseMasterBranchAndDateBetween(branch, new DateTime(startDate).withTimeAtStartOfDay().toDate(), new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate());
            map.put("param2", map.get("param2").toString()
                    .concat(" ")
                    .concat("التاريخ من: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(startDate.longValue()))
                    .concat(" ")
                    .concat("التاريخ إلى: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(endDate.longValue())));
        }
        List<WrapperUtil> list = initDataList(paymentList);
        ClassPathResource jrxmlFile = new ClassPathResource("/report/payment/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JRBeanCollectionDataSource(list));
        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/report/PaymentByMaster/{masterId}", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void printPaymentByMaster(
            @PathVariable("masterId") Long masterId,
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
        StringBuilder param2 = new StringBuilder();
        param2.append("الطلبة المسجلين حسب التخصص ودفعاتهم ");
        param2.append(" --- ");
        param2.append("الفرع: " + "(" + master.getBranch().getName() + ")");
        param2.append("التخصص: " + "(" + master.getName() + ")");
        map.put("param1", param1.toString());
        map.put("param2", param2.toString());
        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");
        /**
         * Insert Data
         */
        List<Payment> paymentList;
        if (startDate == null && endDate == null) {
            paymentList = paymentService.findByAccountCourseMaster(master);
        } else {
            paymentList = paymentService.findByAccountCourseMasterAndDateBetween(master, new DateTime(startDate).withTimeAtStartOfDay().toDate(), new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate());
            map.put("param2", map.get("param2").toString()
                    .concat(" ")
                    .concat("التاريخ من: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(startDate.longValue()))
                    .concat(" ")
                    .concat("التاريخ إلى: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(endDate.longValue())));
        }
        List<WrapperUtil> list = initDataList(paymentList);
        ClassPathResource jrxmlFile = new ClassPathResource("/report/payment/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JRBeanCollectionDataSource(list));
        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/report/PaymentByCourse/{courseId}", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void printPaymentByCourse(
            @PathVariable("courseId") Long courseId,
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
        StringBuilder param2 = new StringBuilder();
        param2.append("الطلبة المسجلين حسب الدورة ودفعاتهم ");
        param2.append(" --- ");
        param2.append("الفرع: " + "(" + course.getMaster().getBranch().getName() + ")");
        param2.append("التخصص: " + "(" + course.getMaster().getName() + ")");
        param2.append("رقم الدورة: " + "(" + course.getCode() + ")");
        map.put("param1", param1.toString());
        map.put("param2", param2.toString());
        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");
        /**
         * Insert Data
         */
        List<Payment> paymentList;
        if (startDate == null && endDate == null) {
            paymentList = paymentService.findByAccountCourse(course);
        } else {
            paymentList = paymentService.findByAccountCourseAndDateBetween(course, new DateTime(startDate).withTimeAtStartOfDay().toDate(), new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate());
            map.put("param2", map.get("param2").toString()
                    .concat(" ")
                    .concat("التاريخ من: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(startDate.longValue()))
                    .concat(" ")
                    .concat("التاريخ إلى: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(endDate.longValue())));
        }
        List<WrapperUtil> list = initDataList(paymentList);
        ClassPathResource jrxmlFile = new ClassPathResource("/report/payment/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JRBeanCollectionDataSource(list));
        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/report/PaymentByAccountIn", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void printPaymentByAccountIn(
            @RequestParam("accountList") List<Long> accountList,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response) throws JRException, IOException {
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
        StringBuilder param2 = new StringBuilder();
        param2.append("الطلبة المسجلين ودفعاتهم");
        map.put("param1", param1.toString());
        map.put("param2", param2.toString());
        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");
        /**
         * Insert Data
         */
        List<Payment> paymentList;
        if (startDate == null && endDate == null) {
            paymentList = paymentService.findByAccountIn(accountList.stream().map(id -> accountService.findOne(id)).collect(Collectors.toList()));
        } else {
            paymentList = paymentService.findByAccountInAndDateBetween(accountList.stream().map(id -> accountService.findOne(id)).collect(Collectors.toList()), new DateTime(startDate).withTimeAtStartOfDay().toDate(), new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate());
            map.put("param2", map.get("param2").toString()
                    .concat(" ")
                    .concat("التاريخ من: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(startDate.longValue()))
                    .concat(" ")
                    .concat("التاريخ إلى: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(endDate.longValue())));
        }
        List<WrapperUtil> list = initDataList(paymentList);
        ClassPathResource jrxmlFile = new ClassPathResource("/report/payment/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JRBeanCollectionDataSource(list));
        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    private List<WrapperUtil> initDataList(List<Payment> paymentList) {
        List<WrapperUtil> list = new ArrayList<>();
        paymentList.stream().forEach(row -> {
            WrapperUtil wrapperUtil = new WrapperUtil();
            Optional.ofNullable(row).ifPresent(payment -> wrapperUtil.setObj1(payment));
            wrapperUtil.setObj2(DateConverter.getHijriStringFromDateRTL(row.getDate().getTime()));
            wrapperUtil.setObj3(
                    DateConverter.getYearShortcut(row.getAccount().getRegisterDate())
                            + "-" + row.getAccount().getCourse().getMaster().getBranch().getCode()
                            + "-" + row.getAccount().getCourse().getMaster().getCode()
                            + "-" + row.getAccount().getCourse().getCode()
                            + "-" + row.getAccount().getCode()
            );
            wrapperUtil.setObj4(accountRest.findRequiredPrice(row.getAccount().getId()));
            wrapperUtil.setObj5(accountRest.findPaidPrice(row.getAccount().getId()));
            wrapperUtil.setObj6(accountRest.findRemainPrice(row.getAccount().getId()));
            list.add(wrapperUtil);
        });
        Collections.sort(list, (WrapperUtil o1, WrapperUtil o2) -> {
            return o1.getObj3().toString().compareTo(o2.getObj3().toString());
        });
        return list;
    }

}
