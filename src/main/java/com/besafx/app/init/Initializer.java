package com.besafx.app.init;

import com.besafx.app.entity.*;
import com.besafx.app.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class Initializer implements CommandLineRunner {

    @Autowired
    private ScreenService screenService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private PersonService personService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (contactService.count() == 0) {
            runForFirstTimeOnly();
        }
    }

    private void runForFirstTimeOnly() {

        Team team = new Team();
        team.setName("مدراء الشركات");
        teamService.save(team);

        Contact contact = new Contact();
        contact.setFirstName("المدير");
        contact.setForthName("العام");
        contact.setPhoto("/no-image.jpg");
        contact.setQualification("مدير شركة");
        contactService.save(contact);

        Person person = new Person();
        person.setContact(contact);
        person.setTeam(team);
        person.setEmail("admin@email.com");
        person.setPassword(passwordEncoder.encode("admin"));
        person.setEnabled(true);
        person.setTokenExpired(false);
        person.setActive(false);
        personService.save(person);

        Company company = new Company();
        company.setManager(person);
        company.setName("شركة تجريبية");
        companyService.save(company);

        Screen screen = new Screen();
        screen.setCode(Screen.ScreenCode.COMPANY);
        screen.setName("الشركات");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode(Screen.ScreenCode.BRANCH);
        screen.setName("الفروع");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode(Screen.ScreenCode.MASTER);
        screen.setName("التخصصات");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode(Screen.ScreenCode.OFFER);
        screen.setName("العروض");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode(Screen.ScreenCode.COURSE);
        screen.setName("الدورات");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode(Screen.ScreenCode.STUDENT);
        screen.setName("الطلاب");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode(Screen.ScreenCode.ACCOUNT);
        screen.setName("تسجيل الطلاب");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode(Screen.ScreenCode.PAYMENT);
        screen.setName("السندات");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode(Screen.ScreenCode.BANK);
        screen.setName("الحسابات البنكية");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode(Screen.ScreenCode.DEPOSIT);
        screen.setName("الإيداعات البنكية");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode(Screen.ScreenCode.WITHDRAW);
        screen.setName("السحبيات البنكية");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode(Screen.ScreenCode.BILL_BUY);
        screen.setName("فواتير الشراء");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode(Screen.ScreenCode.BILL_BUY_TYPE);
        screen.setName("حسابات فواتير الشراء");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode(Screen.ScreenCode.TEAM);
        screen.setName("المجموعات");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode(Screen.ScreenCode.PERSON);
        screen.setName("حسابات المستخدمين");
        screenService.save(screen);

        Iterator<Screen> iterator = screenService.findAll().iterator();

        while (iterator.hasNext()) {

            screen = iterator.next();

            Permission permissionFounded = permissionService
                    .findByCreateEntityAndUpdateEntityAndDeleteEntityAndReportEntityAndScreen(
                            true,
                            true,
                            true,
                            true,
                            screen);

            if (permissionFounded == null) {
                Permission permission = new Permission();
                permission.setScreen(screen);
                permission.setCreateEntity(true);
                permission.setUpdateEntity(true);
                permission.setDeleteEntity(true);
                permission.setReportEntity(true);
                permissionService.save(permission);

                Role role = new Role();
                role.setTeam(team);
                role.setPermission(permission);
                roleService.save(role);
            } else {
                Role role = new Role();
                role.setTeam(team);
                role.setPermission(permissionFounded);
                roleService.save(role);
            }
        }
    }

}
