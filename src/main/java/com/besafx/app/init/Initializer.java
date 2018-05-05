package com.besafx.app.init;

import com.besafx.app.entity.Company;
import com.besafx.app.entity.Contact;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Team;
import com.besafx.app.service.CompanyService;
import com.besafx.app.service.ContactService;
import com.besafx.app.service.PersonService;
import com.besafx.app.service.TeamService;
import com.besafx.app.util.JSONConverter;
import com.besafx.app.util.Options;
import com.google.common.collect.Lists;
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
    private CompanyService companyService;

    @Autowired
    private PersonService personService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TeamService teamService;

    @Override
    public void run(String... args){

        Company company = null;

        if (Lists.newArrayList(companyService.findAll()).isEmpty()) {
            company = new Company();
            company.setName("مؤسسة مدار للتقسيط");
            companyService.save(company);
        }

        if (Lists.newArrayList(personService.findAll()).isEmpty()) {
            Person person = new Person();
            Contact contact = new Contact();
            contact.setFirstName("بسام");
            contact.setSecondName("أحمد");
            contact.setThirdName("أحمد");
            contact.setForthName("المهدي");
            contact.setPhoto("");
            contact.setQualification("Web Developer");
            person.setContact(contactService.save(contact));
            person.setEmail("islamhaker@gmail.com");
            person.setPassword(passwordEncoder.encode("besa2009"));
            person.setHiddenPassword("besa2009");
            person.setEnabled(true);
            person.setTokenExpired(false);
            person.setActive(false);
            person.setTechnicalSupport(true);
            Team team = new Team();
            team.setCode(1);
            team.setName("الدعم الفني");
            team.setAuthorities(String.join(",", "ROLE_COMPANY_UPDATE",
                                            "ROLE_PERSON_CREATE",
                                            "ROLE_PERSON_UPDATE",
                                            "ROLE_PERSON_DELETE",
                                            "ROLE_TEAM_CREATE",
                                            "ROLE_TEAM_UPDATE",
                                            "ROLE_TEAM_DELETE"
                                           ));
            person.setTeam(teamService.save(team));
            person.setOptions(JSONConverter
                                      .toString(Options.builder()
                                                       .lang("AR")
                                                       .style("mdl-style")
                                                       .dateType("G")
                                                       .iconSet("icon-set-2")
                                                       .iconSetType("png")
                                                       .build())
                             );
            person.setCompany(company);
            personService.save(person);
        }

    }
}
