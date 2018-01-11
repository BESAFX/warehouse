package com.besafx.app.async;

import com.besafx.app.entity.Account;
import com.besafx.app.entity.enums.ContractType;
import com.besafx.app.service.AccountService;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@Service
public class AsyncMultiAccountInOneFile {

    private final Logger log = LoggerFactory.getLogger(AsyncMultiAccountInOneFile.class);

    @Autowired
    private AccountService accountService;

    @Async("threadMultiplePool")
    public Future<JasperPrint> getJasperPrint(Long accountId, ContractType contractType, boolean hijriDate) throws Exception{
        log.info("العمل على التسجيل رقم/ " + accountId);
        Account account = accountService.findOne(accountId);
        Map<String, Object> map = new HashMap<>();
        map.put("ACCOUNT", account);
        map.put("HIJRI_DATE", hijriDate);
        try {
            map.put("LOGO", new URL(account.getCourse().getMaster().getBranch().getLogo()).openStream());
        } catch (IOException e) {
            map.put("LOGO", null);
        }
        map.put("TITLE", "عقد إشتراك ب".concat(account.getCourse().getMaster().getMasterCategory().getName()));
        ClassPathResource jrxmlFile = new ClassPathResource("/report/account/" + contractType + ".jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        log.info("الانتهاء من العمل على التسجيل رقم/ " + accountId);
        return new AsyncResult<>(jasperPrint);
    }
}
