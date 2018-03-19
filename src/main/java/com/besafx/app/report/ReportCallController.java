package com.besafx.app.report;

import com.besafx.app.component.ReportExporter;
import com.besafx.app.entity.Call;
import com.besafx.app.entity.Person;
import com.besafx.app.enums.ExportType;
import com.besafx.app.service.CallService;
import com.besafx.app.service.PersonService;
import com.besafx.app.util.DateConverter;
import com.besafx.app.util.WrapperUtil;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
public class ReportCallController {

    private final Logger log = LoggerFactory.getLogger(ReportCallController.class);

    @Autowired
    private CallService callService;

    @Autowired
    private PersonService personService;

    @Autowired
    private ReportExporter reportExporter;

    @RequestMapping(value = "/report/CallByPerson/{id}", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public void ReportCallByPerson(
            @PathVariable(value = "id") Long id,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            Sort sort,
            HttpServletResponse response)
            throws Exception {
        Person person = personService.findOne(id);
        if (person == null) {
            return;
        }
        /**
         * Insert Parameters
         */
        Map<String, Object> map = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        builder.append("تقرير عن الاتصالات بالعروض للموظف/ ");
        builder.append(person.getContact().getShortName());
        map.put("title", builder.toString());

        //Start Search
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(id).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("person").get("id"), value)));
        Optional.ofNullable(startDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("date"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(endDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("date"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            map.put("calls", callService.findAll(result, sort));
        }else{
            map.put("calls", new ArrayList<>());
        }
        //End Search

        ClassPathResource jrxmlFile = new ClassPathResource("/report/call/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

}
