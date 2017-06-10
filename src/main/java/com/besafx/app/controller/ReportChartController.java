package com.besafx.app.controller;
import com.besafx.app.entity.Branch;
import com.besafx.app.entity.Master;
import com.besafx.app.service.BranchService;
import com.besafx.app.service.MasterService;
import com.besafx.app.service.OfferService;
import com.besafx.app.util.DateConverter;
import com.besafx.app.util.WrapperUtil;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ReportChartController {

    @Autowired
    private BranchService branchService;

    @Autowired
    private MasterService masterService;

    @Autowired
    private OfferService offerService;

    @RequestMapping(value = "/report/ChartOffersCountAverageByBranch", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseBody
    public void ReportChartOffersCountAverageByBranch(
            @RequestParam("branchesList") List<Long> branchList,
            @RequestParam("chartType") String chartType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response)
            throws JRException, IOException {
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
        map.put("ChartType", chartType);
        map.put("param2", "متوسط عدد العروض بين الفروع");
        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");
        /**
         * Insert Data
         */
        List<Branch> listOfBranch = branchList.stream().map(i -> branchService.findOne(i)).collect(Collectors.toList());
        long offersCount;
        if (startDate == null && endDate == null) {
            offersCount = offerService.countByMasterBranchIn(listOfBranch);
        } else {
            offersCount = offerService.countByMasterBranchInAndLastUpdateBetween(listOfBranch, new DateTime(startDate).withTimeAtStartOfDay().toDate(), new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate());
            map.put("param2", map.get("param2").toString()
                    .concat(" ")
                    .concat("التاريخ من: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(startDate.longValue()))
                    .concat(" ")
                    .concat("التاريخ إلى: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(endDate.longValue())));
        }
        List<WrapperUtil> list = new ArrayList<>();
        listOfBranch.stream().forEach(branch -> {
            long offersByBranchCount;
            if (startDate == null && endDate == null) {
                offersByBranchCount = offerService.countByMasterBranch(branch);
            } else {
                offersByBranchCount = offerService.countByMasterBranchAndLastUpdateBetween(branch, new DateTime(startDate).withTimeAtStartOfDay().toDate(), new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate());
            }
            WrapperUtil wrapperUtil = new WrapperUtil();
            wrapperUtil.setObj1(branch);
            wrapperUtil.setObj2(Double.valueOf(((double) offersByBranchCount / (double) offersCount) * 100));
            wrapperUtil.setObj3(offersByBranchCount);
            list.add(wrapperUtil);
        });
        map.put("ChartDateSource", new JRBeanCollectionDataSource(list));
        map.put("TableDateSource", new JRBeanCollectionDataSource(list));
        ClassPathResource jrxmlFile = new ClassPathResource("/report/chart/OffersCountAverageByBranch.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> getFile() throws IOException {
        ClassPathResource pdfFile = new ClassPathResource("file.pdf");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Content-Disposition", "inline; filename=Report.pdf");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(pdfFile.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(pdfFile.getInputStream()));
    }

    @RequestMapping(value = "/report/ChartOffersCountAverageByMaster", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportChartOffersCountAverageByMaster(
            @RequestParam("mastersList") List<Long> masterList,
            @RequestParam("chartType") String chartType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response)
            throws JRException, IOException {
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
        map.put("ChartType", chartType);
        map.put("param2", "متوسط عدد العروض بين التخصصات");
        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");
        /**
         * Insert Data
         */
        List<Master> listOfMaster = masterList.stream().map(i -> masterService.findOne(i)).collect(Collectors.toList());
        long offersCount;
        if (startDate == null && endDate == null) {
            offersCount = offerService.countByMasterIn(listOfMaster);
        } else {
            offersCount = offerService.countByMasterInAndLastUpdateBetween(listOfMaster, new DateTime(startDate).withTimeAtStartOfDay().toDate(), new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate());
            map.put("param2", map.get("param2").toString()
                    .concat(" ")
                    .concat("التاريخ من: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(startDate.longValue()))
                    .concat(" ")
                    .concat("التاريخ إلى: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(endDate.longValue())));
        }
        List<WrapperUtil> list = new ArrayList<>();
        listOfMaster.stream().forEach(master -> {
            long offersByMasterCount;
            if (startDate == null && endDate == null) {
                offersByMasterCount = offerService.countByMaster(master);
            } else {
                offersByMasterCount = offerService.countByMasterAndLastUpdateBetween(master, new DateTime(startDate).withTimeAtStartOfDay().toDate(), new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate());
            }
            WrapperUtil wrapperUtil = new WrapperUtil();
            wrapperUtil.setObj1(master);
            wrapperUtil.setObj2(Double.valueOf(((double) offersByMasterCount / (double) offersCount) * 100));
            wrapperUtil.setObj3(offersByMasterCount);
            list.add(wrapperUtil);
        });
        map.put("ChartDateSource", new JRBeanCollectionDataSource(list));
        map.put("TableDateSource", new JRBeanCollectionDataSource(list));
        ClassPathResource jrxmlFile = new ClassPathResource("/report/chart/OffersCountAverageByMaster.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/report/ChartOffersPriceAverageByBranch", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportChartOffersPriceAverageByBranch(
            @RequestParam("branchesList") List<Long> branchList,
            @RequestParam("chartType") String chartType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response)
            throws JRException, IOException {
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
        map.put("ChartType", chartType);
        map.put("param2", "متوسط دخل العروض بين الفروع");
        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");
        /**
         * Insert Data
         */
        List<Branch> listOfBranch = branchList.stream().map(i -> branchService.findOne(i)).collect(Collectors.toList());
        long offersPriceSumForAllBranch;
        if (startDate == null && endDate == null) {
            offersPriceSumForAllBranch = (long) offerService.findByMasterBranchIn(listOfBranch).stream().mapToDouble(offer -> offer.getMasterPrice()).sum();
        } else {
            offersPriceSumForAllBranch = (long) offerService.findByMasterBranchInAndLastUpdateBetween(listOfBranch, new DateTime(startDate).withTimeAtStartOfDay().toDate(), new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate()).stream().mapToDouble(offer -> offer.getMasterPrice()).sum();
            map.put("param2", map.get("param2").toString()
                    .concat(" ")
                    .concat("التاريخ من: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(startDate.longValue()))
                    .concat(" ")
                    .concat("التاريخ إلى: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(endDate.longValue())));
        }
        List<WrapperUtil> list = new ArrayList<>();
        listOfBranch.stream().forEach(branch -> {
            long offersPriceSumForBranch;
            if (startDate == null && endDate == null) {
                offersPriceSumForBranch = (long) offerService.findByMasterBranch(branch).stream().mapToDouble(offer -> offer.getMasterPrice()).sum();
            } else {
                offersPriceSumForBranch = (long) offerService.findByMasterBranchAndLastUpdateBetween(branch, new DateTime(startDate).withTimeAtStartOfDay().toDate(), new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate()).stream().mapToDouble(offer -> offer.getMasterPrice()).sum();
            }
            WrapperUtil wrapperUtil = new WrapperUtil();
            wrapperUtil.setObj1(branch);
            wrapperUtil.setObj2(Double.valueOf(((double) offersPriceSumForBranch / (double) offersPriceSumForAllBranch) * 100));
            wrapperUtil.setObj3(offersPriceSumForBranch);
            list.add(wrapperUtil);
        });
        map.put("ChartDateSource", new JRBeanCollectionDataSource(list));
        map.put("TableDateSource", new JRBeanCollectionDataSource(list));
        ClassPathResource jrxmlFile = new ClassPathResource("/report/chart/OffersPriceAverageByBranch.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/report/ChartOffersPriceAverageByMaster", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportChartOffersPriceAverageByMaster(
            @RequestParam("mastersList") List<Long> mastersList,
            @RequestParam("chartType") String chartType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response)
            throws JRException, IOException {
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
        map.put("ChartType", chartType);
        map.put("param2", "متوسط دخل العروض بين التخصصات");
        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");
        /**
         * Insert Data
         */
        List<Master> listOfMaster = mastersList.stream().map(i -> masterService.findOne(i)).collect(Collectors.toList());
        long offersPriceSumForAllMaster;
        if (startDate == null && endDate == null) {
            offersPriceSumForAllMaster = (long) offerService.findByMasterIn(listOfMaster).stream().mapToDouble(offer -> offer.getMasterPrice()).sum();
        } else {
            offersPriceSumForAllMaster = (long) offerService.findByMasterInAndLastUpdateBetween(listOfMaster, new DateTime(startDate).withTimeAtStartOfDay().toDate(), new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate()).stream().mapToDouble(offer -> offer.getMasterPrice()).sum();
            map.put("param2", map.get("param2").toString()
                    .concat(" ")
                    .concat("التاريخ من: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(startDate.longValue()))
                    .concat(" ")
                    .concat("التاريخ إلى: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(endDate.longValue())));
        }
        List<WrapperUtil> list = new ArrayList<>();
        listOfMaster.stream().forEach(master -> {
            long offersPriceSumForMaster;
            if (startDate == null && endDate == null) {
                offersPriceSumForMaster = (long) offerService.findByMaster(master).stream().mapToDouble(offer -> offer.getMasterPrice()).sum();
            } else {
                offersPriceSumForMaster = (long) offerService.findByMasterAndLastUpdateBetween(master, new DateTime(startDate).withTimeAtStartOfDay().toDate(), new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate()).stream().mapToDouble(offer -> offer.getMasterPrice()).sum();
            }
            WrapperUtil wrapperUtil = new WrapperUtil();
            wrapperUtil.setObj1(master);
            wrapperUtil.setObj2(Double.valueOf(((double) offersPriceSumForMaster / (double) offersPriceSumForAllMaster) * 100));
            wrapperUtil.setObj3(offersPriceSumForMaster);
            list.add(wrapperUtil);
        });
        map.put("ChartDateSource", new JRBeanCollectionDataSource(list));
        map.put("TableDateSource", new JRBeanCollectionDataSource(list));
        ClassPathResource jrxmlFile = new ClassPathResource("/report/chart/OffersPriceAverageByMaster.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }
}
