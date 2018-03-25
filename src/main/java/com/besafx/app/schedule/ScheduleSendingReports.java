package com.besafx.app.schedule;

import com.besafx.app.async.*;
import com.besafx.app.component.QuickEmail;
import com.besafx.app.config.DropboxManager;
import com.besafx.app.entity.Branch;
import com.besafx.app.service.CompanyService;
import com.besafx.app.util.DateConverter;
import com.google.common.collect.Lists;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
@Service
public class ScheduleSendingReports {

    private final Logger log = LoggerFactory.getLogger(ScheduleSendingReports.class);

    @Autowired
    private CompanyService companyService;

    @Autowired
    private AsyncScheduleOffers asyncScheduleOffers;

    @Autowired
    private AsyncSchedulePayment asyncSchedulePayment;

    @Autowired
    private AsyncScheduleBillBuy asyncScheduleBillBuy;

    @Autowired
    private AsyncScheduleIncomeAnalysis asyncScheduleIncomeAnalysis;

    @Autowired
    private TransactionalService transactionalService;

    @Autowired
    private QuickEmail quickEmail;

    @Autowired
    private DropboxManager dropboxManager;

    private void run(String timeType) throws Exception {

        Future<byte[]> task = getZip4jFile(timeType);
        byte[] bytes = task.get();

        if (task.isDone()) {

            log.info("Starting uploading zip file");
            StringBuffer fileName = new StringBuffer();
            fileName.append(DateConverter.getNowFileName());
            fileName.append(".zip");

            Future<Boolean> uploadTask = dropboxManager.uploadFile(new ByteArrayInputStream(bytes), fileName.toString(), "/Smart Offer/Reports/" + fileName.toString());

            if (uploadTask.get()) {
                log.info("Ending uploading file");
                log.info("Starting sending message");
                Future<String> uploadFileLinkTask = dropboxManager.shareFile("/Smart Offer/Reports/" + fileName.toString());
                uploadFileLinkTask.get();

                String subject = "", title = "", subTitle = "";

                switch (timeType) {
                    case "Day":
                        subject = "التقارير اليومية - " + DateConverter.getNowFileName();
                        title = "التقارير اليومية - " + DateConverter.getNowFileName();
                        subTitle = "تقارير اليوم الموافق ".concat(DateConverter.getHijriTodayDateString());
                        break;
                    case "Week":
                        subject = "التقارير الاسبوعية - " + DateConverter.getNowFileName();
                        title = "التقارير الاسبوعية - " + DateConverter.getNowFileName();
                        subTitle = "تقارير الاسبوع";
                        break;
                    case "Month":
                        subject = "التقارير الشهرية - " + DateConverter.getNowFileName();
                        title = "التقارير الشهرية - " + DateConverter.getNowFileName();
                        subTitle = "تقارير الشهر";
                        break;
                    case "Year":
                        subject = "التقارير السنوية - " + DateConverter.getNowFileName();
                        title = "التقارير السنوية - " + DateConverter.getNowFileName();
                        subTitle = "تقارير العام";
                        break;
                }

                String body = "اضغط على الزر اداناه لتحميل التقارير";
                String buttonText = "تحميل التقارير";
                List<String> emails = Lists.newArrayList(companyService.findFirstBy().getEmail(), "islamhaker@gmail.com");
//                quickEmail.send(subject, emails, title, subTitle, body, uploadFileLinkTask.get(), buttonText);

                log.info("ENDING SENDING MESSAGE");
            }

        }
    }

    @Async("threadMultiplePool")
    public Future<byte[]> getZipFile(String timeType) throws Exception {

        log.info("Generate zip file");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

        List<Branch> branches = transactionalService.getBranches();

        log.info("Generate offers for each branch report");
        {

            branches.stream().forEach(branch -> {
                try{
                    Future<byte[]> work = asyncScheduleOffers.getFile(timeType, branch);
                    byte[] fileBytes = work.get();

                    ZipEntry entry = new ZipEntry("العروض لفرع " + branch.getName() + ".pdf");
                    zipOutputStream.putNextEntry(entry);
                    zipOutputStream.write(fileBytes);
                    zipOutputStream.closeEntry();
                }catch (Exception ex){}
            });
        }

        log.info("Generate payments for each branch report");
        {
            branches.stream().forEach(branch -> {
                try{
                    Future<byte[]> work = asyncSchedulePayment.getFile(timeType, branch);
                    byte[] fileBytes = work.get();

                    ZipEntry entry = new ZipEntry("الايرادات لفرع " + branch.getName() + ".pdf");
                    zipOutputStream.putNextEntry(entry);
                    zipOutputStream.write(fileBytes);
                    zipOutputStream.closeEntry();
                }catch (Exception ex){}
            });
        }

        log.info("Generate billBuys for each branch report");
        {
            branches.stream().forEach(branch -> {
                try{
                    Future<byte[]> work = asyncScheduleBillBuy.getFile(timeType, branch);
                    byte[] fileBytes = work.get();

                    ZipEntry entry = new ZipEntry("المصروفات لفرع " + branch.getName() + ".pdf");
                    zipOutputStream.putNextEntry(entry);
                    zipOutputStream.write(fileBytes);
                    zipOutputStream.closeEntry();
                }catch (Exception ex){}
            });
        }

        log.info("Generate income analysis for each branch report");
        {
            branches.stream().forEach(branch -> {
                try{
                    Future<byte[]> work = asyncScheduleIncomeAnalysis.getFile(timeType, branch);
                    byte[] fileBytes = work.get();

                    ZipEntry entry = new ZipEntry("تحليل الايرادات لفرع " + branch.getName() + ".pdf");
                    zipOutputStream.putNextEntry(entry);
                    zipOutputStream.write(fileBytes);
                    zipOutputStream.closeEntry();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            });
        }

        zipOutputStream.finish();
        zipOutputStream.flush();
        IOUtils.closeQuietly(zipOutputStream);
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);

        return new AsyncResult<>(byteArrayOutputStream.toByteArray());
    }

    @Async("threadMultiplePool")
    public Future<byte[]> getZip4jFile(String timeType) throws Exception {

        log.info("Generate zip file");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        net.lingala.zip4j.io.ZipOutputStream zos = new net.lingala.zip4j.io.ZipOutputStream(bufferedOutputStream);

        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);

        List<Branch> branches = transactionalService.getBranches();

        log.info("Generate offers for each branch report");
        {
            branches.stream().forEach(branch -> {
                try{
                    Future<byte[]> work = asyncScheduleOffers.getFile(timeType, branch);
                    byte[] fileBytes = work.get();

                    parameters.setFileNameInZip("العروض لفرع " + branch.getName() + ".pdf");
                    parameters.setSourceExternalStream(true);
                    zos.putNextEntry(null, parameters);
                    zos.write(fileBytes);
                    zos.closeEntry();
                }catch (Exception ex){}
            });
        }

        log.info("Generate payments for each branch report");
        {
            branches.stream().forEach(branch -> {
                try{
                    Future<byte[]> work = asyncSchedulePayment.getFile(timeType, branch);
                    byte[] fileBytes = work.get();

                    parameters.setFileNameInZip("الايرادات لفرع " + branch.getName() + ".pdf");
                    parameters.setSourceExternalStream(true);
                    zos.putNextEntry(null, parameters);
                    zos.write(fileBytes);
                    zos.closeEntry();
                }catch (Exception ex){}
            });
        }

        log.info("Generate billBuys for each branch report");
        {
            branches.stream().forEach(branch -> {
                try{
                    Future<byte[]> work = asyncScheduleBillBuy.getFile(timeType, branch);
                    byte[] fileBytes = work.get();

                    parameters.setFileNameInZip("المصروفات لفرع " + branch.getName() + ".pdf");
                    parameters.setSourceExternalStream(true);
                    zos.putNextEntry(null, parameters);
                    zos.write(fileBytes);
                    zos.closeEntry();
                }catch (Exception ex){}
            });
        }

        log.info("Generate income analysis for each branch report");
        {
            branches.stream().forEach(branch -> {
                try{
                    Future<byte[]> work = asyncScheduleIncomeAnalysis.getFile(timeType, branch);
                    byte[] fileBytes = work.get();

                    parameters.setFileNameInZip("تحليل الايرادات لفرع " + branch.getName() + ".pdf");
                    parameters.setSourceExternalStream(true);
                    zos.putNextEntry(null, parameters);
                    zos.write(fileBytes);
                    zos.closeEntry();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            });
        }

        zos.finish();
        zos.flush();
        IOUtils.closeQuietly(zos);
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);

        return new AsyncResult<>(byteArrayOutputStream.toByteArray());
    }

    @Scheduled(cron = "0 0 22 * * *")
    public void runDaily() throws Exception {
        run("Day");
    }

    @Scheduled(cron = "0 0/30 22 * * FRI")
    public void runWeekly() throws Exception {
        run("Week");
    }

    @Scheduled(cron = "0 0/45 22 25 * *")
    public void runMonthly() throws Exception {
        run("Month");
    }


}
