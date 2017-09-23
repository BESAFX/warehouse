package com.besafx.app.component;
import com.besafx.app.config.EmailSender;
import com.besafx.app.controller.ReportOfferController;
import com.besafx.app.service.CompanyService;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
public class ScheduledTasks {

    private final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private ReportOfferController reportOfferController;

    @Scheduled(cron = "0 0 21 * * *")
    public void notifyBossAboutOffersToday() {
        try {
            log.info("");
            Future<byte[]> work = reportOfferController.reportOffersToday();
            byte[] fileBytes = work.get();
            if (fileBytes != null) {
                log.info("جاري إنشاء ملف التقرير: " + "OffersToday");
                File reportFile = File.createTempFile("OffersToday", ".pdf");
                FileUtils.writeByteArrayToFile(reportFile, fileBytes);
                log.info("جاري تحويل الملف");
                Thread.sleep(10000);
                Future<Boolean> mail = emailSender.send("تقرير يومي بالعروض المدخلة من كافة الفروع", "", "w.abukhader@ararhni.com", Lists.newArrayList(reportFile));
                try {
                    mail.get();
                    log.info("تم إرسال الملف فى البريد الإلكتروني بنجاح");
                } catch (InterruptedException e) {
                    log.info("ارسال التقرير الخاص بالعروض اليومية لم يتم بنجاح");
                } catch (ExecutionException e) {
                    log.info("ارسال التقرير الخاص بالعروض اليومية لم يتم بنجاح");
                }
            } else {
                log.info("لا يوجد تقرير لهذا المدير.");
            }
        } catch (Exception ex) {
            log.info(ex.getMessage(), ex);
        }
        log.info("نهاية الفحص بنجاح.");
    }

}
