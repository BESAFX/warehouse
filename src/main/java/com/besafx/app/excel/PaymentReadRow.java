package com.besafx.app.excel;

import com.besafx.app.entity.Payment;
import com.besafx.app.rest.AccountRest;
import com.besafx.app.service.PaymentService;
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
    private AccountRest accountRest;

    @Autowired
    private PaymentService paymentService;

    @Async("threadPoolPaymentExcel")
    public Future<Payment> readRow(Payment payment) throws Exception {
        log.info("////////////////////////////////بداية العملية/////////////////////////////////");
        log.info("فحص هل تبقي مبالغ لم تسدد لهذا الحساب");
        if (payment.getType().equals("ايرادات اساسية")) {
            if (accountRest.findRemainPrice(payment.getAccount().getId()) < payment.getAmountNumber()) {
                log.info("لا يمكن قبول قيمة اكبر من الباقي من التسجيل");
                return new AsyncResult<>(null);
            }
        }
        Payment tempPayment = paymentService.findByCodeAndAccountCourseMasterBranch(payment.getCode(), payment.getLastPerson().getBranch());
        if (tempPayment != null) {
            log.info("لا يمكن تكرار رقم السند على مستوى الفرع، حيث لكل فرع دفتر سندات قبض خاص به");
            log.info("معرف السند المكرر = " + tempPayment.getId());
            log.info("رقم السند المكرر = " + tempPayment.getCode());
            return new AsyncResult<>(null);
        }
        payment = paymentService.save(payment);
        log.info("تم إنشاء سند جديد بتعريف رقم : " + payment.getId());
        log.info("////////////////////////////////نهاية العملية/////////////////////////////////");
        return new AsyncResult<>(payment);
    }
}
