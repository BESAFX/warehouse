package com.besafx.app;

import com.besafx.app.service.AccountService;
import com.besafx.app.service.BranchService;
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

    @Test
    public void contextLoads() throws ExecutionException, InterruptedException {
        accountService.findByCourseMasterBranch(branchService.findByCode(2)).stream().forEach(account -> {
            account.setCoursePaymentType("قسط شهري");
            account.setCourseCreditAmount(1000.0);
            accountService.save(account);
        });
    }
}
