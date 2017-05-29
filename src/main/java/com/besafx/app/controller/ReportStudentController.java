package com.besafx.app.controller;

import com.besafx.app.entity.Student;
import com.besafx.app.service.StudentService;
import com.besafx.app.util.DateConverter;
import com.besafx.app.util.WrapperUtil;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@RestController
public class ReportStudentController {

    @Autowired
    private StudentService studentRepository;

    @RequestMapping(value = "/report/StudentAll", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void printStudentAll(HttpServletResponse response) throws JRException, IOException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=Report.pdf");
        final OutputStream outStream = response.getOutputStream();

        /**
         * Insert Parameters
         */
        Map<String, Object> map = new HashMap<>();
        map.put("param1", "كشف بيانات الطلبة");
        map.put("param2", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");

        /**
         * Insert Data
         */
        List<Student> studentList = Lists.newArrayList(studentRepository.findAll());
        List<WrapperUtil> list = new ArrayList<>();
        studentList.stream().forEach(row -> {
            WrapperUtil wrapperUtil = new WrapperUtil();
            Optional.ofNullable(row.getContact()).ifPresent(contact -> wrapperUtil.setObj1(contact));
            Optional.ofNullable(row.getContact()).ifPresent(contact -> wrapperUtil
                    .setObj2(DateConverter.getHijriStringFromDateRTL(contact.getBirthDate().getTime())));
            list.add(wrapperUtil);
        });

        ClassPathResource jrxmlFile = new ClassPathResource("/report/ReportStudentAll.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JRBeanCollectionDataSource(list));

        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }
}
