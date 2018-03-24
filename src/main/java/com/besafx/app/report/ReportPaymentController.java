package com.besafx.app.report;

import com.besafx.app.component.ReportExporter;
import com.besafx.app.entity.Payment;
import com.besafx.app.enums.ExportType;
import com.besafx.app.service.CourseService;
import com.besafx.app.service.PaymentService;
import net.sf.jasperreports.engine.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ReportPaymentController {

    private final Logger log = LoggerFactory.getLogger(ReportPaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ReportExporter reportExporter;

    @RequestMapping(value = "/report/CashReceipt/{paymentId}", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public void printCashReceipt(@PathVariable(value = "paymentId") Long paymentId, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<>();
        Payment payment = paymentService.findOne(paymentId);
        try {
            map.put("logo", new URL(payment.getAccount().getCourse().getMaster().getBranch().getLogo()).openStream());
        } catch (IOException e) {
            map.put("logo", null);
        }
        map.put("payment", payment);
        ClassPathResource jrxmlFile = new ClassPathResource("/report/payment/CashReceipt.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(ExportType.PDF, response, jasperPrint);
    }

    @RequestMapping(value = "/report/PaymentByBranches", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public void printPaymentByBranches(
            @RequestParam(value = "branchIds") List<Long> branchIds,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "isSummery") Boolean isSummery,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            @RequestParam(value = "codeFrom", required = false) Long codeFrom,
            @RequestParam(value = "codeTo", required = false) Long codeTo,
            @RequestParam(value = "title") String title,
            Sort sort,
            HttpServletResponse response
    ) throws JRException, IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        //Start Search
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(branchIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("account").get("course").get("master").get("branch").get("id").in(value)));
        Optional.ofNullable(startDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("date"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(endDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("date"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(codeFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(codeTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("code"), value)));
        map.put("payments", getList(predicates, sort));
        //End Search
        ClassPathResource jrxmlFile = new ClassPathResource("/report/payment/" + (isSummery ? "ReportSummery" : "Report") + ".jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/IncomeAnalysisByBranches", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public void printIncomeAnalysisByBranches(
            @RequestParam(value = "branchIds") List<Long> branchIds,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            @RequestParam(value = "title") String title,
            HttpServletResponse response
    ) throws JRException, IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("courses", courseService.findByMasterBranchIdIn(branchIds));
        ClassPathResource jrxmlFile = new ClassPathResource("/report/payment/IncomeAnalysis.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/PaymentByMasterCategories", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public void printPaymentByMasterCategories(
            @RequestParam(value = "branchIds") List<Long> branchIds,
            @RequestParam(value = "masterCategoryIds") List<Long> masterCategoryIds,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "isSummery") Boolean isSummery,
            @RequestParam(value = "codeFrom", required = false) Long codeFrom,
            @RequestParam(value = "codeTo", required = false) Long codeTo,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            @RequestParam(value = "title") String title,
            Sort sort,
            HttpServletResponse response
    ) throws JRException, IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        //Start Search
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(branchIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("account").get("course").get("master").get("branch").get("id").in(value)));
        Optional.ofNullable(masterCategoryIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("account").get("course").get("master").get("masterCategory").get("id").in(value)));
        Optional.ofNullable(codeFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(codeTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(startDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("date"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(endDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("date"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        map.put("payments", getList(predicates, sort));
        //End Search
        ClassPathResource jrxmlFile = new ClassPathResource("/report/payment/" + (isSummery ? "ReportSummery" : "Report") + ".jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/PaymentByMasters", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public void printPaymentByMasters(
            @RequestParam(value = "masterIds") List<Long> masterIds,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "isSummery") Boolean isSummery,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            @RequestParam(value = "codeFrom", required = false) Long codeFrom,
            @RequestParam(value = "codeTo", required = false) Long codeTo,
            @RequestParam(value = "title") String title,
            Sort sort,
            HttpServletResponse response
    ) throws JRException, IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        //Start Search
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(masterIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("account").get("course").get("master").get("id").in(value)));
        Optional.ofNullable(startDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("date"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(endDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("date"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(codeFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(codeTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("code"), value)));
        map.put("payments", getList(predicates, sort));
        //End Search
        ClassPathResource jrxmlFile = new ClassPathResource("/report/payment/" + (isSummery ? "ReportSummery" : "Report") + ".jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/PaymentByCourses", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public void printPaymentByCourses(
            @RequestParam(value = "courseIds") List<Long> courseIds,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "isSummery") Boolean isSummery,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            @RequestParam(value = "codeFrom", required = false) Long codeFrom,
            @RequestParam(value = "codeTo", required = false) Long codeTo,
            @RequestParam(value = "title") String title,
            Sort sort,
            HttpServletResponse response
    ) throws JRException, IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        //Start Search
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(courseIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("account").get("course").get("id").in(value)));
        Optional.ofNullable(startDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("date"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(endDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("date"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(codeFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(codeTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("code"), value)));
        map.put("payments", getList(predicates, sort));
        //End Search
        ClassPathResource jrxmlFile = new ClassPathResource("/report/payment/"+(isSummery ? "ReportSummery" : "Report")+".jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/PaymentByAccountIn", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public void printPaymentByAccountIn(
            @RequestParam("accountIds") List<Long> accountIds,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "isSummery") Boolean isSummery,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            @RequestParam(value = "title") String title,
            Sort sort,
            HttpServletResponse response
    ) throws JRException, IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        //Start Search
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(accountIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("account").get("id").in(value)));
        Optional.ofNullable(startDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("date"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(endDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("date"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        map.put("payments", getList(predicates, sort));
        //End Search
        ClassPathResource jrxmlFile = new ClassPathResource("/report/payment/" + (isSummery ? "ReportSummery" : "Report") + ".jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/PaymentsByList", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public void printPaymentsByList(
            @RequestParam("paymentIds") List<Long> paymentIds,
            @RequestParam(value = "isSummery") Boolean isSummery,
            HttpServletResponse response
    ) throws JRException, IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("title", "قائمة سندات مخصصة");
        map.put("payments", paymentIds.stream().map(id -> paymentService.findOne(id)).collect(Collectors.toList()));
        ClassPathResource jrxmlFile = new ClassPathResource("/report/payment/" + (isSummery ? "ReportSummery" : "Report") + ".jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(ExportType.PDF, response, jasperPrint);
    }

    private List<Payment> getList(List<Specification> predicates, Sort sort) {
        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            return paymentService.findAll(result, sort);
        }else{
            return new ArrayList<>();
        }
    }

}
