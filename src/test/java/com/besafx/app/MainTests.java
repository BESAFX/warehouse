package com.besafx.app;

import com.besafx.app.entity.Payment;
import com.besafx.app.entity.enums.PaymentMethod;
import com.besafx.app.service.AccountService;
import com.besafx.app.service.BranchService;
import com.besafx.app.service.PaymentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MainTests {

    private final static Logger log = LoggerFactory.getLogger(MainTests.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private PaymentService paymentService;

    @Test
    public void contextLoads() throws ExecutionException, InterruptedException {
        paymentService.findAll().forEach(a -> {
            a.setPaymentMethod(PaymentMethod.Cash);
            paymentService.save(a);
        });
    }
}
