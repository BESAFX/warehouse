package com.besafx.app.excel;

import com.besafx.app.entity.Account;
import com.besafx.app.entity.Payment;
import com.besafx.app.entity.Person;
import com.besafx.app.rest.AccountRest;
import com.besafx.app.service.AccountService;
import com.besafx.app.service.PaymentService;
import com.besafx.app.util.DateConverter;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
public class PaymentReadRow {

    private final static Logger log = LoggerFactory.getLogger(PaymentReadRow.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRest accountRest;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private NotificationService notificationService;

    @Async("threadPoolPaymentExcel")
    public Future<Payment> readRow(Person person,
                                   Payment payment,
                                   String firstName,
                                   String secondName,
                                   String thirdName,
                                   String forthName,
                                   Integer courseCode,
                                   Integer masterCode,
                                   Integer day,
                                   Integer month,
                                   Integer year) {
        try {
            Thread.sleep(1000);
            Account account = accountService
                    .findByStudentContactFirstNameAndStudentContactSecondNameAndStudentContactThirdNameAndStudentContactForthNameAndCourseCodeAndCourseMasterCodeAndCourseMasterBranchCode
                            (firstName, secondName, thirdName, forthName, courseCode, masterCode, person.getBranch().getCode());
            if (account == null) {
                return new AsyncResult<>(null);
            }
            log.info("تم إيجاد التسجيل بنجاح...");
            log.info("فحص هل تبقي مبالغ لم تسدد لهذا الحساب");
            if (payment.getType().equals("ايرادات اساسية")) {
                if (accountRest.findRemainPrice(account.getId()) < payment.getAmountNumber()) {
                    log.info("لا يمكن قبول قيمة اكبر من الباقي من التسجيل");
                    return new AsyncResult<>(null);
                }
            }
            Payment tempPayment = paymentService.findByCodeAndAccountCourseMasterBranch(payment.getCode(), person.getBranch());
            if (tempPayment != null) {
                log.info("لا يمكن تكرار رقم السند على مستوى الفرع، حيث لكل فرع دفتر سندات قبض خاص به");
                log.info("معرف السند المكرر = " + tempPayment.getId());
                log.info("رقم السند المكرر = " + tempPayment.getCode());
                return new AsyncResult<>(null);
            }
            payment.setAccount(account);
            payment.setDate(DateConverter.getDateFromHijri(year, month, day));
            payment.setLastPerson(person);
            payment = paymentService.save(payment);
            return new AsyncResult<>(payment);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new AsyncResult<>(null);
        }
    }
}
