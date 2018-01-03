package com.besafx.app.report;

import com.besafx.app.component.ReportExporter;
import com.besafx.app.entity.Offer;
import com.besafx.app.enums.ExportType;
import com.besafx.app.service.OfferService;
import net.sf.jasperreports.engine.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.Future;

@RestController
public class ReportOfferController {

    private final Logger log = LoggerFactory.getLogger(ReportOfferController.class);

    @Autowired
    private OfferService offerService;

    @Autowired
    private ReportExporter reportExporter;

    @RequestMapping(value = "/report/OfferById/{id}", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public void printOfferById(
            @PathVariable(value = "id") Long id,
            @RequestParam(value = "exportType") ExportType exportType,
            HttpServletResponse response) throws Exception {
        Offer offer = offerService.findOne(id);
        if (offer == null) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المملكة العربية السعودية");
        param1.append("\n");
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        map.put("param1", param1.toString());
        map.put("param2", "عرض سعر");
        map.put("offer", offer);
        ClassPathResource jrxmlFile = new ClassPathResource("/report/offer/ReportOfferById.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/OfferByBranches", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public void ReportOfferByBranches(
            @RequestParam(value = "branchIds") List<Long> branchIds,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "registerOption") Integer registerOption,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المملكة العربية السعودية");
        param1.append("\n");
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        map.put("param1", param1.toString());
        map.put("param2", title);
        //Start Search
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(branchIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("master").get("branch").get("id").in(value)));
        Optional.ofNullable(startDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("lastUpdate"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(endDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("lastUpdate"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(registerOption).ifPresent(value -> {
            switch (value) {
                case 2:
                    predicates.add((root, cq, cb) -> cb.isTrue(root.get("registered")));
                    break;
                case 3:
                    predicates.add((root, cq, cb) -> cb.isFalse(root.get("registered")));
                    break;
                default:
                    break;
            }
        });
        map.put("offers", getList(predicates));
        //End Search
        ClassPathResource jrxmlFile = new ClassPathResource("/report/offer/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/OfferByMasterCategories", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public void OfferByMasterCategories(
            @RequestParam(value = "branchIds") List<Long> branchIds,
            @RequestParam(value = "masterCategoryIds") List<Long> masterCategoryIds,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "registerOption") Integer registerOption,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response)
            throws Exception {
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المملكة العربية السعودية");
        param1.append("\n");
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        map.put("param1", param1.toString());
        map.put("param2", title);
        //Start Search
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(branchIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("master").get("branch").get("id").in(value)));
        Optional.ofNullable(masterCategoryIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("master").get("masterCategory").get("id").in(value)));
        Optional.ofNullable(startDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("lastUpdate"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(endDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("lastUpdate"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(registerOption).ifPresent(value -> {
            switch (value) {
                case 2:
                    predicates.add((root, cq, cb) -> cb.isTrue(root.get("registered")));
                    break;
                case 3:
                    predicates.add((root, cq, cb) -> cb.isFalse(root.get("registered")));
                    break;
                default:
                    break;
            }
        });
        map.put("offers", getList(predicates));
        //End Search
        ClassPathResource jrxmlFile = new ClassPathResource("/report/offer/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/OfferByMasters", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public void ReportOfferByMasters(
            @RequestParam(value = "masterIds") List<Long> masterIds,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "registerOption") Integer registerOption,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response)
            throws Exception {
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المملكة العربية السعودية");
        param1.append("\n");
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        map.put("param1", param1.toString());
        map.put("param2", title);
        //Start Search
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(masterIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("master").get("id").in(value)));
        Optional.ofNullable(startDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("lastUpdate"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(endDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("lastUpdate"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(registerOption).ifPresent(value -> {
            switch (value) {
                case 2:
                    predicates.add((root, cq, cb) -> cb.isTrue(root.get("registered")));
                    break;
                case 3:
                    predicates.add((root, cq, cb) -> cb.isFalse(root.get("registered")));
                    break;
                default:
                    break;
            }
        });
        map.put("offers", getList(predicates));
        //End Search
        ClassPathResource jrxmlFile = new ClassPathResource("/report/offer/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/OfferByPersons", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public void ReportOfferByPersons(
            @RequestParam(value = "personIds") List<Long> personIds,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "registerOption") Integer registerOption,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response)
            throws Exception {
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المملكة العربية السعودية");
        param1.append("\n");
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        map.put("param1", param1.toString());
        map.put("param2", title);
        //Start Search
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(personIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("lastPerson").get("id").in(value)));
        Optional.ofNullable(startDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("lastUpdate"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(endDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("lastUpdate"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(registerOption).ifPresent(value -> {
            switch (value) {
                case 2:
                    predicates.add((root, cq, cb) -> cb.isTrue(root.get("registered")));
                    break;
                case 3:
                    predicates.add((root, cq, cb) -> cb.isFalse(root.get("registered")));
                    break;
                default:
                    break;
            }
        });
        map.put("offers", getList(predicates));
        //End Search
        ClassPathResource jrxmlFile = new ClassPathResource("/report/offer/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @Async("threadMultiplePool")
    public Future<byte[]> reportOffersToday() {
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        param1.append("\n");
        param1.append("تقرير عن العروض المدخلة اليوم من كافة الفروع");
        map.put("title", param1.toString());
        DateTime today = new DateTime().withTimeAtStartOfDay();
        DateTime tomorrow = new DateTime().plusDays(1).withTimeAtStartOfDay();
        List<Offer> offers = offerService.findByLastUpdateBetween(today.toDate(), tomorrow.toDate());
        map.put("list", offers);
        if (offers.isEmpty()) {
            return null;
        }
        try {
            ClassPathResource jrxmlFile = new ClassPathResource("/report/offer/OffersToday.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
            return new AsyncResult<>(JasperExportManager.exportReportToPdf(jasperPrint));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    private List<Offer> getList(List<Specification> predicates) {
        List<Offer> list = new ArrayList<>();
        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            list.addAll(offerService.findAll(result));
        }
        return list;
    }

}
