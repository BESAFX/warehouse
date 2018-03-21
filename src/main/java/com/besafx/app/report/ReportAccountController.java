package com.besafx.app.report;

import com.besafx.app.async.AsyncMultiAccountInOneFile;
import com.besafx.app.component.ReportExporter;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Account;
import com.besafx.app.entity.Offer;
import com.besafx.app.entity.enums.ContractType;
import com.besafx.app.enums.ExportType;
import com.besafx.app.service.AccountService;
import net.sf.jasperreports.engine.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class ReportAccountController {

    private final static Logger log = LoggerFactory.getLogger(ReportAccountController.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private ReportExporter reportExporter;

    @Autowired
    private AsyncMultiAccountInOneFile asyncMultiAccountInOneFile;

    @RequestMapping(value = "/report/AccountByBranches", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void printAccountByBranches(
            @RequestParam(value = "branchIds") List<Long> branchIds,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            @RequestParam(value = "title") String title,
            Sort sort,
            HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        //Start Search
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(branchIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("course").get("master").get("branch").get("id").in(value)));
        Optional.ofNullable(startDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("registerDate"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(endDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("registerDate"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        //End Search
        map.put("accounts", getList(predicates, sort));
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
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            @RequestParam(value = "title") String title,
            Sort sort,
            HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        //Start Search
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(branchIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("course").get("master").get("branch").get("id").in(value)));
        Optional.ofNullable(masterCategoryIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("course").get("master").get("masterCategory").get("id").in(value)));
        Optional.ofNullable(startDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("registerDate"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(endDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("registerDate"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        map.put("accounts", getList(predicates, sort));
        //End Search
        ClassPathResource jrxmlFile = new ClassPathResource("/report/account/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/AccountByMasters", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void printAccountByMasters(
            @RequestParam(value = "masterIds") List<Long> masterIds,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            @RequestParam(value = "title") String title,
            Sort sort,
            HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        //Start Search
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(masterIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("course").get("master").get("id").in(value)));
        Optional.ofNullable(startDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("registerDate"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(endDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("registerDate"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        //End Search
        map.put("accounts", getList(predicates, sort));
        ClassPathResource jrxmlFile = new ClassPathResource("/report/account/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/AccountByCourses", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void printAccountByCourses(
            @RequestParam(value = "courseIds") List<Long> courseIds,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            @RequestParam(value = "title") String title,
            Sort sort,
            HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        //Start Search
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(courseIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("course").get("id").in(value)));
        Optional.ofNullable(startDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("registerDate"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(endDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("registerDate"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        //End Search
        map.put("accounts", getList(predicates, sort));
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
                File jasperFile = File.createTempFile(account.getName().toString().replaceAll(" ", "_"), ".pdf");
                FileUtils.writeByteArrayToFile(jasperFile, JasperExportManager.exportReportToPdf(jasperPrint));
                zipOutputStream.putNextEntry(new ZipEntry(account.getName().toString().replaceAll(" ", "_").concat(".pdf")));
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

    private List<Offer> getList(List<Specification> predicates, Sort sort) {
        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            return accountService.findAll(result, sort);
        } else {
            return new ArrayList<>();
        }
    }
}
