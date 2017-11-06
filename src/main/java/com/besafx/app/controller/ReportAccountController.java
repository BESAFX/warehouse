package com.besafx.app.controller;

import com.besafx.app.async.AsyncMultiAccountInOneFile;
import com.besafx.app.component.ReportExporter;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.*;
import com.besafx.app.entity.enums.ContractType;
import com.besafx.app.enums.ExportType;
import com.besafx.app.service.*;
import com.besafx.app.util.DateConverter;
import com.besafx.app.util.WrapperUtil;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class ReportAccountController {

    private final static Logger log = LoggerFactory.getLogger(ReportAccountController.class);

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

    @Autowired
    private AsyncMultiAccountInOneFile asyncMultiAccountInOneFile;

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

    @RequestMapping(value = "/report/account/contract/zip", method = RequestMethod.GET, produces = "application/zip")
    @ResponseBody
    public byte[] printContractAsZip(
            @RequestParam(value = "accountIds") List<Long> accountIds,
            @RequestParam(value = "contractType") ContractType contractType,
            @RequestParam(value = "reportFileName") String reportFileName,
            @RequestParam(value = "hijriDate") boolean hijriDate,
            HttpServletResponse response) {

        if (accountIds.isEmpty()) {
            throw new CustomException("عفواً، اختر عنصر واحد على الأقل");
        }

        try {

            //
            response.setHeader("Content-Type", "application/zip");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(reportFileName.replaceAll(" ", "_"), "utf-8") + ".zip\"");
            //

            //
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
            ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);
            //

            List<JasperPrint> jasperPrints = new ArrayList<>();
            ListIterator<Long> listIterator = accountIds.listIterator();
            while (listIterator.hasNext()) {
                Long id = listIterator.next();
                Account account = accountService.findOne(id);
                JasperPrint jasperPrint = asyncMultiAccountInOneFile.getJasperPrint(id, contractType, hijriDate).get();
                jasperPrints.add(jasperPrint);
                //
                StringBuilder fileName = new StringBuilder("");
                fileName.append(account.getStudent().getContact().getFirstName());
                fileName.append(" ");
                fileName.append(account.getStudent().getContact().getSecondName());
                fileName.append(" ");
                fileName.append(account.getStudent().getContact().getThirdName());
                fileName.append(" ");
                fileName.append(account.getStudent().getContact().getForthName());
                fileName.append(" ");
                fileName.append(account.getCourse().getMaster().getName());
                //
                File jasperFile = File.createTempFile(fileName.toString().replaceAll(" ", "_"), ".pdf");
                FileUtils.writeByteArrayToFile(jasperFile, JasperExportManager.exportReportToPdf(jasperPrint));
                zipOutputStream.putNextEntry(new ZipEntry(fileName.toString().replaceAll(" ", "_").concat(".pdf")));
                FileInputStream fileInputStream = new FileInputStream(jasperFile);
                IOUtils.copy(fileInputStream, zipOutputStream);
                fileInputStream.close();
                zipOutputStream.closeEntry();
            }

            if (zipOutputStream != null) {
                zipOutputStream.finish();
                zipOutputStream.flush();
                IOUtils.closeQuietly(zipOutputStream);
            }
            IOUtils.closeQuietly(bufferedOutputStream);
            IOUtils.closeQuietly(byteArrayOutputStream);

            return byteArrayOutputStream.toByteArray();

        } catch (Exception ex) {
            return null;
        }
    }

    @RequestMapping(value = "/report/account/contract/pdf", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void printContractAsPDF(
            @RequestParam(value = "accountIds") List<Long> accountIds,
            @RequestParam(value = "contractType") ContractType contractType,
            @RequestParam(value = "reportFileName") String reportFileName,
            @RequestParam(value = "hijriDate") Boolean hijriDate,
            HttpServletResponse response) throws Exception {

        if (accountIds.isEmpty()) {
            throw new CustomException("عفواً، اختر عنصر واحد على الأقل");
        }


        log.info("HIJRI: " + hijriDate);

        List<JasperPrint> jasperPrints = new ArrayList<>();

        ListIterator<Long> listIterator = accountIds.listIterator();
        while (listIterator.hasNext()) {
            jasperPrints.add(asyncMultiAccountInOneFile.getJasperPrint(listIterator.next(), contractType, hijriDate).get());
        }

        StringBuilder builder = new StringBuilder("");
        if (accountIds.size() > 1) {
            builder.append(reportFileName);
        } else if (accountIds.size() == 1) {
            Account account = accountService.findOne(accountIds.get(0));
            builder.append(account.getStudent().getContact().getFirstName());
            builder.append(" ");
            builder.append(account.getStudent().getContact().getSecondName());
            builder.append(" ");
            builder.append(account.getStudent().getContact().getThirdName());
            builder.append(" ");
            builder.append(account.getStudent().getContact().getForthName());
            builder.append(" ");
            builder.append(account.getCourse().getMaster().getName());
        }
        reportExporter.exportMultiple(builder.toString().replaceAll(" ", "_"), response, jasperPrints);
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
