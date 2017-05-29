package com.besafx.app.controller;

import com.besafx.app.entity.Branch;
import com.besafx.app.entity.Course;
import com.besafx.app.entity.Master;
import com.besafx.app.entity.Offer;
import com.besafx.app.service.*;
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
import java.util.stream.Collectors;

@RestController
public class ReportInfoController {

    @Autowired
    private BranchService branchService;

    @Autowired
    private MasterService masterService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private OfferService offerService;

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/report/BranchDetails", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportBranchDetails(@RequestParam("branchesList") List<Long> branchList, HttpServletResponse response) throws JRException, IOException {


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

        map.put("param2", "تقرير عن تفاصيل الفروع");

        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");

        /**
         * Insert Data
         */
        List<Branch> listOfBranch = branchList.stream().map(i -> branchService.findOne(i)).collect(Collectors.toList());
        List<WrapperUtil> list = new ArrayList<>();
        listOfBranch.stream().forEach(branch -> {
            WrapperUtil wrapperUtil = new WrapperUtil();
            wrapperUtil.setObj1(branch);
            wrapperUtil.setObj2(offerService.countByMasterBranch(branch));
            wrapperUtil.setObj3(masterService.countByBranch(branch));
            wrapperUtil.setObj4(courseService.countByMasterBranch(branch));
            list.add(wrapperUtil);
        });

        map.put("BranchDetailsDataSource", new JRBeanCollectionDataSource(list));

        ClassPathResource jrxmlFile = new ClassPathResource("/report/info/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);

        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/report/MasterDetails", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportMasterDetails(@RequestParam("mastersList") List<Long> mastersList, HttpServletResponse response) throws JRException, IOException {


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

        map.put("param2", "تقرير عن تفاصيل التخصصات");

        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");

        /**
         * Insert Data
         */
        List<Master> listOfMaster = mastersList.stream().map(i -> masterService.findOne(i)).collect(Collectors.toList());
        List<WrapperUtil> list = new ArrayList<>();
        listOfMaster.stream().forEach(master -> {
            WrapperUtil wrapperUtil = new WrapperUtil();
            wrapperUtil.setObj1(master);
            wrapperUtil.setObj2(offerService.countByMaster(master));
            wrapperUtil.setObj3(courseService.countByMaster(master));
            list.add(wrapperUtil);
        });

        map.put("MasterDetailsDataSource", new JRBeanCollectionDataSource(list));

        ClassPathResource jrxmlFile = new ClassPathResource("/report/info/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);

        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/report/CourseDetails", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportCourseDetails(@RequestParam("coursesList") List<Long> coursesList, HttpServletResponse response) throws JRException, IOException {


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

        map.put("param2", "تقرير عن تفاصيل الدورات");

        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");

        /**
         * Insert Data
         */
        List<Course> listOfCourse = coursesList.stream().map(i -> courseService.findOne(i)).collect(Collectors.toList());
        List<WrapperUtil> list = new ArrayList<>();
        listOfCourse.stream().forEach(course -> {
            WrapperUtil wrapperUtil = new WrapperUtil();
            wrapperUtil.setObj1(course);
            wrapperUtil.setObj2(accountService.countByCourse(course));
            wrapperUtil.setObj3(DateConverter.getHijriStringFromDateRTL(course.getStartDate().getTime()));
            wrapperUtil.setObj4(DateConverter.getHijriStringFromDateRTL(course.getEndDate().getTime()));
            list.add(wrapperUtil);
        });

        map.put("CourseDetailsDataSource", new JRBeanCollectionDataSource(list));

        ClassPathResource jrxmlFile = new ClassPathResource("/report/info/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);

        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/report/UnRegisteredStudentDetailsByBranch/{branchId}", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportUnRegisteredStudentDetailsByBranch(
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
        map.put("param1", param1.toString());

        StringBuilder param2 = new StringBuilder();
        param2.append("بيانات الطلاب غير المسجلين للفرع/ ");
        param2.append(branch.getName());
        map.put("param2", param2.toString());

        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");

        /**
         * Insert Data
         */
        List<Offer> offers;

        if (startDate == null && endDate == null) {
            offers = offerService.findByMasterBranch(branch);
        } else {
            offers = offerService.findByMasterBranchAndLastUpdateBetween(branch, new Date(startDate), new Date(endDate));
            map.put("param2", map.get("param2").toString()
                    .concat(" ")
                    .concat("التاريخ من: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(startDate.longValue()))
                    .concat(" ")
                    .concat("التاريخ إلى: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(endDate.longValue())));
        }

        List<WrapperUtil> list = initDateList(offers);

        map.put("UnRegisteredStudentDetailsByDataSource", new JRBeanCollectionDataSource(list));

        ClassPathResource jrxmlFile = new ClassPathResource("/report/info/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);

        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);

    }

    @RequestMapping(value = "/report/UnRegisteredStudentDetailsByMaster/{masterId}", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportUnRegisteredStudentDetailsByMaster(
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
        map.put("param1", param1.toString());

        StringBuilder param2 = new StringBuilder();
        param2.append("بيانات الطلاب غير المسجلين للتخصص/ ");
        param2.append(master.getName());
        param2.append(" ");
        param2.append("التابع للفرع/ ");
        param2.append(master.getBranch().getName());
        map.put("param2", param2.toString());

        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");

        /**
         * Insert Data
         */
        List<Offer> offers;

        if (startDate == null && endDate == null) {
            offers = offerService.findByMaster(master);
        } else {
            offers = offerService.findByMasterAndLastUpdateBetween(master, new Date(startDate), new Date(endDate));
            map.put("param2", map.get("param2").toString()
                    .concat(" ")
                    .concat("التاريخ من: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(startDate.longValue()))
                    .concat(" ")
                    .concat("التاريخ إلى: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(endDate.longValue())));
        }

        List<WrapperUtil> list = initDateList(offers);

        map.put("UnRegisteredStudentDetailsByDataSource", new JRBeanCollectionDataSource(list));

        ClassPathResource jrxmlFile = new ClassPathResource("/report/info/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);

        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);

    }

    private List<WrapperUtil> initDateList(List<Offer> offers) {
        List<WrapperUtil> list = new ArrayList<>();
        ListIterator<Offer> listIterator = offers.listIterator();
        while (listIterator.hasNext()) {
            Offer offer = listIterator.next();
            WrapperUtil wrapperUtil = new WrapperUtil();
            wrapperUtil.setObj1(offer);
            wrapperUtil.setObj2(DateConverter.getHijriStringFromDateRTL(offer.getLastUpdate().getTime()));
            list.add(wrapperUtil);
        }

        Collections.sort(list, (WrapperUtil o1, WrapperUtil o2) -> {
            return ((Offer) o1.getObj1()).getCode().compareTo(((Offer) o2.getObj1()).getCode());
        });
        return list;
    }

}
