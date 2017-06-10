package com.besafx.app.controller;
import com.besafx.app.service.BranchService;
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
public class ReportPersonController {

    @Autowired
    private BranchService branchService;

    @Autowired
    private PersonService personService;

    @RequestMapping(value = "/report/PersonByBranch", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportPersonByBranch(
            @RequestParam("branchList") List<Long> branchList,
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
        param1.append("\n");
        param1.append("كشف تفصيلي عن بيانات المستخدمين");
        map.put("param1", param1.toString());
        map.put("param2", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");
        /**
         * Insert Data
         */
        List<WrapperUtil> list = new ArrayList<>();
        ClassPathResource jrxmlFile = new ClassPathResource("/report/person/ReportPersonByBranch.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JRBeanCollectionDataSource(list));
        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }
}
