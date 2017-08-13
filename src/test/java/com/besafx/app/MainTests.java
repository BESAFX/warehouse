package com.besafx.app;
import com.besafx.app.component.ScheduledTasks;
import com.besafx.app.config.EmailSender;
import com.besafx.app.entity.Account;
import com.besafx.app.search.AccountSearch;
import com.besafx.app.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MainTests {

    private final static Logger log = LoggerFactory.getLogger(MainTests.class);

    private static final String ACCESS_TOKEN = "_kj_6GrBNbYAAAAAAACR03IkJ8JnT5i1AntPiHU9GiLCsT7zO_LxIJMUEMk93T9O";

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private PersonService personService;

    @Autowired
    private OfferService offerService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ContactService contactService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private ScheduledTasks scheduledTasks;

    @Autowired
    private StudentService studentService;

    @Autowired
    private AccountSearch accountSearch;

    @Test
    public void contextLoads() {
        List<Account> accounts = accountSearch.search2("محمد", "عقيل", "دواي", "المطرفي", null, null, null, null, null, null, null, null, null);
        log.info(accounts.size() + " Row");
    }
}
