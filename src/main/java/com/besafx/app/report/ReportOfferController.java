package com.besafx.app.report;

import com.besafx.app.component.ReportExporter;
import com.besafx.app.entity.Offer;
import com.besafx.app.enums.ExportType;
import com.besafx.app.service.OfferService;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
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
import java.util.*;

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
            Sort sort,
            HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
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
        map.put("offers", getList(predicates, sort));
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
            Sort sort,
            HttpServletResponse response)
            throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
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
        map.put("offers", getList(predicates, sort));
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
            Sort sort,
            HttpServletResponse response)
            throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
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
        map.put("offers", getList(predicates, sort));
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
            Sort sort,
            HttpServletResponse response)
            throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
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
        map.put("offers", getList(predicates, sort));
        //End Search
        ClassPathResource jrxmlFile = new ClassPathResource("/report/offer/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    private List<Offer> getList(List<Specification> predicates, Sort sort) {
        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            return offerService.findAll(result, sort);
        } else {
            return new ArrayList<>();
        }
    }

}
