package com.besafx.app.controller;

import com.besafx.app.entity.Branch;
import com.besafx.app.entity.Master;
import com.besafx.app.entity.Offer;
import com.besafx.app.entity.Person;
import com.besafx.app.service.BranchService;
import com.besafx.app.service.MasterService;
import com.besafx.app.service.OfferService;
import com.besafx.app.service.PersonService;
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

@RestController
public class ReportOfferController {

    @Autowired
    private OfferService offerService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private MasterService masterService;

    @Autowired
    private PersonService personService;

    @RequestMapping(value = "/report/OfferById/{id}", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void printOfferById(@PathVariable(value = "id") Long id, HttpServletResponse response) throws JRException, IOException {

        Offer offer = offerService.findOne(id);

        if (offer == null) {
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
        param2.append("عرض سعر");
        map.put("param2", param2.toString());

        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");

        /**
         * Insert Data
         */
        List<WrapperUtil> list = new ArrayList<>();
        WrapperUtil wrapperUtil = new WrapperUtil();
        wrapperUtil.setObj1(offer);
        if (offer.getMasterPaymentType().equals("نقدي")) {
            wrapperUtil.setObj2(
                    offer.getMasterPrice() - (offer.getMasterPrice() * offer.getMasterDiscountAmount() / 100));
        } else {
            wrapperUtil
                    .setObj2(offer.getMasterPrice() + (offer.getMasterPrice() * offer.getMasterProfitAmount() / 100));
        }
        list.add(wrapperUtil);

        ClassPathResource jrxmlFile = new ClassPathResource("/report/offer/ReportOfferById.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JRBeanCollectionDataSource(list));

        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/report/OfferByBranch/{id}", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportOfferByBranch(
            @PathVariable(value = "id") Long id,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response) throws JRException, IOException {

        Branch branch = branchService.findOne(id);

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
        param2.append("تقرير عن العروض للفرع/ ");
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


        map.put("ItemDataSource", new JRBeanCollectionDataSource(list));

        ClassPathResource jrxmlFile = new ClassPathResource("/report/offer/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);

        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/report/OfferByMaster/{id}", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportOfferByMaster(
            @PathVariable(value = "id") Long id,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response)
            throws JRException, IOException {

        Master master = masterService.findOne(id);

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
        param2.append("تقرير عن العروض للتخصص/ ");
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

        map.put("ItemDataSource", new JRBeanCollectionDataSource(list));

        ClassPathResource jrxmlFile = new ClassPathResource("/report/offer/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);

        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/report/OfferByPerson/{id}", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportOfferByPerson(
            @PathVariable(value = "id") Long id,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response)
            throws JRException, IOException {

        Person person = personService.findOne(id);

        if (person == null) {
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
        param2.append("تقرير عن العروض للموظف/ ");
        param2.append(person.getContact().getFirstName().concat(" ").concat(person.getContact().getForthName()));
        map.put("param2", param2.toString());

        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");

        /**
         * Insert Data
         */
        List<Offer> offers;

        if (startDate == null && endDate == null) {
            offers = offerService.findByLastPerson(person);
        } else {
            offers = offerService.findByLastPersonAndLastUpdateBetween(person, new Date(startDate), new Date(endDate));
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

        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
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

        list.sort((o1, o2) -> {
            Offer offer1 = (Offer) o1.getObj1();
            Offer offer2 = (Offer) o2.getObj1();
            int compare = Integer.compare(offer1.getCode(), offer2.getCode());
            if (compare == 0) {
                compare = offer1.getLastUpdate().compareTo(offer2.getLastUpdate());
            }
            return compare;
        });

        return list;
    }


}
