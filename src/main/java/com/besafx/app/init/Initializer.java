package com.besafx.app.init;
import com.besafx.app.entity.Company;
import com.besafx.app.entity.Contact;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Team;
import com.besafx.app.service.CompanyService;
import com.besafx.app.service.PersonService;
import com.besafx.app.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Initializer implements CommandLineRunner {

    private final static Logger log = LoggerFactory.getLogger(Initializer.class);

    @Autowired
    private TeamService teamService;

    @Autowired
    private PersonService personService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (personService.count() == 0) {
            runForFirstTimeOnly();
        }
    }

    private void runForFirstTimeOnly() {
        log.info("انشاء مجموعة الصلاحيات");
        Team team = new Team();
        team.setCode(1);
        team.setName("مدير النظام");
        team.setAuthorities(String.join(",",
                "ROLE_COMPANY_UPDATE",
                "ROLE_BRANCH_CREATE",
                "ROLE_BRANCH_UPDATE",
                "ROLE_BRANCH_DELETE",
                "ROLE_MASTER_CREATE",
                "ROLE_MASTER_UPDATE",
                "ROLE_MASTER_DELETE",
                "ROLE_OFFER_CREATE",
                "ROLE_OFFER_UPDATE",
                "ROLE_OFFER_DELETE",
                "ROLE_COURSE_CREATE",
                "ROLE_COURSE_UPDATE",
                "ROLE_COURSE_DELETE",
                "ROLE_STUDENT_CREATE",
                "ROLE_STUDENT_UPDATE",
                "ROLE_STUDENT_DELETE",
                "ROLE_ACCOUNT_CREATE",
                "ROLE_ACCOUNT_UPDATE",
                "ROLE_ACCOUNT_DELETE",
                "ROLE_PAYMENT_CREATE",
                "ROLE_PAYMENT_UPDATE",
                "ROLE_PAYMENT_DELETE",
                "ROLE_BANK_CREATE",
                "ROLE_BANK_UPDATE",
                "ROLE_BANK_DELETE",
                "ROLE_DEPOSIT_CREATE",
                "ROLE_DEPOSIT_UPDATE",
                "ROLE_DEPOSIT_DELETE",
                "ROLE_WITHDRAW_CREATE",
                "ROLE_WITHDRAW_UPDATE",
                "ROLE_WITHDRAW_DELETE",
                "ROLE_BILL_BUY_CREATE",
                "ROLE_BILL_BUY_UPDATE",
                "ROLE_BILL_BUY_DELETE",
                "ROLE_BILL_BUY_TYPE_CREATE",
                "ROLE_BILL_BUY_TYPE_UPDATE",
                "ROLE_BILL_BUY_TYPE_DELETE",
                "ROLE_PERSON_CREATE",
                "ROLE_PERSON_UPDATE",
                "ROLE_PERSON_DELETE",
                "ROLE_PROFILE_UPDATE",
                "ROLE_TEAM_CREATE",
                "ROLE_TEAM_UPDATE",
                "ROLE_TEAM_DELETE"
        ));
        teamService.save(team);
        log.info("أنشاء المستخدم الخاص بمدير النظام");
        Contact contact = new Contact();
        contact.setFirstName("مدير");
        contact.setForthName("النظام");
        contact.setNationality("---");
        contact.setIdentityNumber("---");
        contact.setAddress("---");
        contact.setMobile("---");
        contact.setQualification("---");
        contact.setPhoto(null);
        Person person = new Person();
        person.setEmail("admin@ararhni.com");
        person.setPassword(passwordEncoder.encode("admin"));
        person.setHiddenPassword("admin");
        person.setEnabled(true);
        person.setTokenExpired(false);
        person.setActive(false);
        person.setTeam(team);
        personService.save(person);
        log.info("انشاء الشركة");
        Company company = new Company();
        company.setCode(1);
        company.setName("المعهد الأهلي للتدريب");
        company.setPhone("0138099353");
        company.setFax("0138099352");
        company.setEmail("admin@arahni.com");
        company.setWebsite("www.ararhni.com");
        company.setManager(person);
        companyService.save(company);
    }
}
