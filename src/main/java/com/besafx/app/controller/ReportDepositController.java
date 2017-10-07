package com.besafx.app.controller;

import com.besafx.app.component.ReportExporter;
import com.besafx.app.entity.Offer;
import com.besafx.app.entity.Person;
import com.besafx.app.enums.ExportType;
import com.besafx.app.service.DepositService;
import com.besafx.app.service.PersonService;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.security.Principal;
import java.util.*;

@RestController
public class ReportDepositController {

    @Autowired
    private DepositService depositService;

    @Autowired
    private PersonService personService;

    @Autowired
    private ReportExporter reportExporter;

    @RequestMapping(value = "/report/DepositByBranches", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public void printDepositByBranch(
            @RequestParam(value = "branchIds") List<Long> branchIds,
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
        Optional.ofNullable(branchIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("bank").get("branch").get("id").in(value)));
        Optional.ofNullable(startDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("date"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(endDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("date"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        map.put("DEPOSITS", getList(predicates));
        //End Search
        ClassPathResource jrxmlFile = new ClassPathResource("/report/deposit/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    private List<Offer> getList(List<Specification> predicates) {
        List<Offer> list = new ArrayList<>();
        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            list.addAll(depositService.findAll(result));
        }
        return list;
    }
}
