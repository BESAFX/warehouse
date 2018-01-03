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
        StringBuilder param1 = new StringBuilder();
        param1.append("المملكة العربية السعودية");
        param1.append("\n");
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        map.put("param1", param1.toString());
        StringBuilder param2 = new StringBuilder();
        param2.append("تقرير عن الاتصالات بالعروض للموظف/ ");
        param2.append(person.getContact().getFirstName().concat(" ").concat(person.getContact().getForthName()));
        map.put("param2", param2.toString());
        map.put("param3", "تاريخ الطباعة (" + DateConverter.getHijriStringFromDateLTR(new Date().getTime()) + ")");
        /**
         * Insert Data
         */
        List<Call> calls;
        if (startDate == null && endDate == null) {
            calls = callService.findByPerson(person);
        } else {
            calls = callService.findByPersonAndDateBetween(person, new DateTime(startDate).withTimeAtStartOfDay().toDate(), new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate());
            map.put("param2", map.get("param2").toString()
                    .concat(" ")
                    .concat("التاريخ من: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(startDate.longValue()))
                    .concat(" ")
                    .concat("التاريخ إلى: ")
                    .concat(DateConverter.getHijriStringFromDateLTR(endDate.longValue())));
        }
        List<WrapperUtil> list = initDateList(calls);
        map.put("ItemDataSource", new JRBeanCollectionDataSource(list));
        ClassPathResource jrxmlFile = new ClassPathResource("/report/call/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    private List<WrapperUtil> initDateList(List<Call> calls) {
        List<WrapperUtil> list = new ArrayList<>();
        ListIterator<Call> listIterator = calls.listIterator();
        while (listIterator.hasNext()) {
            Call call = listIterator.next();
            WrapperUtil wrapperUtil = new WrapperUtil();
            wrapperUtil.setObj1(call);
            list.add(wrapperUtil);
        }
        list.sort(Comparator.comparing(wrapperUtil -> ((Call) wrapperUtil.getObj1()).getOffer().getCustomerName()));
        return list;
    }

}
