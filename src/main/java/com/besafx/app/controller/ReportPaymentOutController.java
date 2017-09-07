package com.besafx.app.controller;
import com.besafx.app.component.ReportExporter;
import com.besafx.app.entity.*;
import com.besafx.app.enums.ExportType;
import com.besafx.app.rest.AccountRest;
import com.besafx.app.service.*;
import com.besafx.app.util.DateConverter;
import com.besafx.app.util.WrapperUtil;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ReportPaymentOutController {

    @Autowired
    private BranchService branchService;

    @Autowired
    private PersonService personService;

    @Autowired
    private PaymentOutService paymentOutService;

    @Autowired
    private ReportExporter reportExporter;

    @RequestMapping(value = "/report/PaymentOutByBranch", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public void printPaymentOutByBranch(
            @RequestParam(value = "branchId") Long branchId,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            @RequestParam(value = "exportType") ExportType exportType,
            HttpServletResponse response) throws Exception {
        Branch branch = branchService.findOne(branchId);
        if (branch == null) {
            return;
        }
        //Building Map...
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المملكة العربية السعودية");
        param1.append("\n");
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        StringBuilder param2 = new StringBuilder();
        param2.append("تقرير سندات القبض للفرع");
        param2.append(" / ");
        param2.append(branch.getName());
        map.put("param1", param1.toString());
        map.put("param2", param2.toString());
        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");
        //Preparing Data...
        List<PaymentOut> paymentOutList;
        if (startDate == null && endDate == null) {
            paymentOutList = paymentOutService.findByBranch(branch);
        } else {
            paymentOutList = paymentOutService.findByBranchAndDateBetween(branch, new DateTime(startDate).withTimeAtStartOfDay().toDate(), new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate());
            map.put("param2", map.get("param2").toString()
                    .concat(" ")
                    .concat("التاريخ من: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(startDate.longValue()))
                    .concat(" ")
                    .concat("التاريخ إلى: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(endDate.longValue())));
        }
        map.put("list", paymentOutList);
        //Generating Report...
        ClassPathResource jrxmlFile = new ClassPathResource("/report/paymentOut/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/PaymentOutByPerson", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public void printPaymentOutByPerson(
            @RequestParam(value = "personId") Long personId,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            @RequestParam(value = "exportType") ExportType exportType,
            HttpServletResponse response) throws Exception {
        Person person = personService.findOne(personId);
        if (person == null) {
            return;
        }
        //Building Map...
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المملكة العربية السعودية");
        param1.append("\n");
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        StringBuilder param2 = new StringBuilder();
        param2.append("تقرير سندات القبض للمستخدم");
        param2.append(" / ");
        param2.append(person.getContact().getFirstName() + " " + person.getContact().getForthName());
        map.put("param1", param1.toString());
        map.put("param2", param2.toString());
        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");
        //Preparing Data...
        List<PaymentOut> paymentOutList;
        if (startDate == null && endDate == null) {
            paymentOutList = paymentOutService.findByPerson(person);
        } else {
            paymentOutList = paymentOutService.findByPersonAndDateBetween(person, new DateTime(startDate).withTimeAtStartOfDay().toDate(), new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate());
            map.put("param2", map.get("param2").toString()
                    .concat(" ")
                    .concat("التاريخ من: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(startDate.longValue()))
                    .concat(" ")
                    .concat("التاريخ إلى: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(endDate.longValue())));
        }
        map.put("list", paymentOutList);
        //Generating Report...
        ClassPathResource jrxmlFile = new ClassPathResource("/report/paymentOut/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/PaymentOutByList", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public void printPaymentOutByList(
            @RequestParam("listId") List<Long> listId,
            @RequestParam(value = "exportType") ExportType exportType,
            HttpServletResponse response) throws Exception {
        //Building Map...
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المملكة العربية السعودية");
        param1.append("\n");
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        StringBuilder param2 = new StringBuilder();
        param2.append("قائمة سندات مخصصة");
        map.put("param1", param1.toString());
        map.put("param2", param2.toString());
        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");
        //Preparing Date...
        List<PaymentOut> paymentOutList = new ArrayList<>();
        listId.stream().forEach(id -> paymentOutList.add(paymentOutService.findOne(id)));
        map.put("list", paymentOutList);
        //Generating Report...
        ClassPathResource jrxmlFile = new ClassPathResource("/report/paymentOut/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

}
