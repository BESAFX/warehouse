package com.besafx.app.controller;
import com.besafx.app.component.ReportExporter;
import com.besafx.app.entity.*;
import com.besafx.app.enums.ExportType;
import com.besafx.app.service.BranchService;
import com.besafx.app.service.MasterService;
import com.besafx.app.service.OfferService;
import com.besafx.app.service.PersonService;
import com.besafx.app.util.DateConverter;
import com.besafx.app.util.WrapperUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
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
            @RequestParam(value = "ids") List<Long> ids,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response) throws Exception {
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
        map.put("param2", title);
        /**
         * Insert Data
         */
        List<Offer> offers;
        if (startDate == null && endDate == null) {
            offers = offerService.findByMasterBranchIdIn(ids);
        } else {
            offers = offerService.findByMasterBranchIdInAndLastUpdateBetween(ids, new DateTime(startDate).withTimeAtStartOfDay().toDate(), new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate());
            map.put("param2", map.get("param2").toString()
                    .concat(" ")
                    .concat("التاريخ من: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(startDate.longValue()))
                    .concat(" ")
                    .concat("التاريخ إلى: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(endDate.longValue())));
        }
        List<WrapperUtil> list = initDateList(offers);
        map.put("ItemDataSource", new JRBeanCollectionDataSource(list));
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
        List<Offer> list = new ArrayList<>();
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(branchIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("master").get("branch").get("id").in(value)));
        Optional.ofNullable(masterCategoryIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("master").get("masterCategory").get("id").in(value)));
        Optional.ofNullable(startDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("lastUpdate"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(endDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("lastUpdate"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            list.addAll(offerService.findAll(result));
        }
        //End Search
        map.put("offers", list);
        ClassPathResource jrxmlFile = new ClassPathResource("/report/offer/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/OfferByMasters", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public void ReportOfferByMasters(
            @RequestParam(value = "ids") List<Long> ids,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response)
            throws Exception {
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
        map.put("param2", title);
        /**
         * Insert Data
         */
        List<Offer> offers;
        if (startDate == null && endDate == null) {
            offers = offerService.findByMasterIdIn(ids);
        } else {
            offers = offerService.findByMasterIdInAndLastUpdateBetween(ids, new DateTime(startDate).withTimeAtStartOfDay().toDate(), new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate());
            map.put("param2", map.get("param2").toString()
                    .concat(" ")
                    .concat("التاريخ من: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(startDate.longValue()))
                    .concat(" ")
                    .concat("التاريخ إلى: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(endDate.longValue())));
        }
        List<WrapperUtil> list = initDateList(offers);
        map.put("ItemDataSource", new JRBeanCollectionDataSource(list));
        ClassPathResource jrxmlFile = new ClassPathResource("/report/offer/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/OfferByPersons", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public void ReportOfferByPersons(
            @RequestParam(value = "ids") List<Long> ids,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response)
            throws Exception {
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
        map.put("param2", title);
        /**
         * Insert Data
         */
        List<Offer> offers;
        if (startDate == null && endDate == null) {
            offers = offerService.findByLastPersonIdIn(ids);
        } else {
            offers = offerService.findByLastPersonIdInAndLastUpdateBetween(ids, new DateTime(startDate).withTimeAtStartOfDay().toDate(), new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate());
            map.put("param2", map.get("param2").toString()
                    .concat(" ")
                    .concat("التاريخ من: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(startDate.longValue()))
                    .concat(" ")
                    .concat("التاريخ إلى: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(endDate.longValue())));
        }
        List<WrapperUtil> list = initDateList(offers);
        map.put("ItemDataSource", new JRBeanCollectionDataSource(list));
        ClassPathResource jrxmlFile = new ClassPathResource("/report/offer/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @Async("threadPoolReportGenerator")
    public Future<byte[]> reportOffersToday() {
        /**
         * Insert Parameters
         */
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

    private List<WrapperUtil> initDateList(List<Offer> offers) {
        List<WrapperUtil> list = new ArrayList<>();
        ListIterator<Offer> listIterator = offers.listIterator();
        while (listIterator.hasNext()) {
            Offer offer = listIterator.next();
            WrapperUtil wrapperUtil = new WrapperUtil();
            wrapperUtil.setObj1(offer);
            list.add(wrapperUtil);
        }
        list.sort(Comparator.comparing(o -> ((Offer) o.getObj1()).getLastUpdate()));
        return list;
    }

}
