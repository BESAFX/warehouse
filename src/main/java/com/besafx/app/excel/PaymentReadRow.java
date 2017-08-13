package com.besafx.app.excel;

import com.besafx.app.entity.Account;
import com.besafx.app.entity.Payment;
import com.besafx.app.entity.Person;
import com.besafx.app.rest.AccountRest;
import com.besafx.app.search.AccountSearch;
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

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class PaymentReadRow {

    private final static Logger log = LoggerFactory.getLogger(PaymentReadRow.class);

    private Account account;

    @Autowired
    private AccountSearch accountSearch;

    @Autowired
    private AccountRest accountRest;

    @Autowired
    private PaymentService paymentService;

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
            log.info("////////////////////////////////بداية العملية/////////////////////////////////");
            List<Account> accounts = accountSearch.search(firstName, secondName, thirdName, forthName, null, null, null, null, null, null, courseCode, masterCode, person.getBranch().getCode());
            Thread.sleep(1000);
            if (accounts.isEmpty()) {
                return new AsyncResult<>(null);
            }
            log.info("عدد التسجيلات الموجودة = " + accounts.size());
            accounts.stream().findFirst().ifPresent(value -> account = value);
            log.info("تم إيجاد التسجيل بنجاح...");
            log.info("معرف التسجيل = " + account.getId());
            log.info("فحص هل تبقي مبالغ لم تسدد لهذا الحساب");
            if (payment.getType().equals("ايرادات اساسية")) {
                if (accountRest.findRemainPrice(account.getId()) < payment.getAmountNumber()) {
                    log.info("لا يمكن قبول قيمة اكبر من الباقي من التسجيل");
                    return new AsyncResult<>(null);
                }
            }
            Thread.sleep(1000);
            Payment tempPayment = paymentService.findByCodeAndAccountCourseMasterBranch(payment.getCode(), person.getBranch());
            if (tempPayment != null) {
                log.info("لا يمكن تكرار رقم السند على مستوى الفرع، حيث لكل فرع دفتر سندات قبض خاص به");
                log.info("معرف السند المكرر = " + tempPayment.getId());
                log.info("رقم السند المكرر = " + tempPayment.getCode());
                return new AsyncResult<>(null);
            }
            Thread.sleep(1000);
            payment.setAccount(account);
            Date date = DateConverter.getDateFromHijri(year, month, day);
            log.info("تاريخ السند المدخل : " + date.toString());
            payment.setDate(date);
            payment.setLastPerson(person);
            payment = paymentService.save(payment);
            log.info("تم إنشاء سند جديد بتعريف رقم : " + payment.getId());
            log.info("////////////////////////////////نهاية العملية/////////////////////////////////");
            return new AsyncResult<>(payment);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new AsyncResult<>(null);
        }
    }
}
