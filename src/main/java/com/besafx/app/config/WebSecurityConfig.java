package com.besafx.app.config;
import com.besafx.app.entity.Person;
import com.besafx.app.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionEvent;
import java.util.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final Logger log = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Autowired
    private PersonService personService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/ui/**").permitAll()
                .antMatchers("/api/**").permitAll()
                .antMatchers("/company").access("hasRole('ROLE_COMPANY_UPDATE')")
                .antMatchers("/branch").access("hasRole('ROLE_BRANCH_CREATE') or hasRole('ROLE_BRANCH_UPDATE') or hasRole('ROLE_BRANCH_DELETE')")
                .antMatchers("/master").access("hasRole('ROLE_MASTER_CREATE') or hasRole('ROLE_MASTER_UPDATE') or hasRole('ROLE_MASTER_DELETE')")
                .antMatchers("/offer").access("hasRole('ROLE_OFFER_CREATE') or hasRole('ROLE_OFFER_UPDATE') or hasRole('ROLE_OFFER_DELETE')")
                .antMatchers("/course").access("hasRole('ROLE_COURSE_CREATE') or hasRole('ROLE_COURSE_UPDATE') or hasRole('ROLE_COURSE_DELETE')")
                .antMatchers("/student").access("hasRole('ROLE_STUDENT_CREATE') or hasRole('ROLE_STUDENT_UPDATE') or hasRole('ROLE_STUDENT_DELETE')")
                .antMatchers("/account").access("hasRole('ROLE_ACCOUNT_CREATE') or hasRole('ROLE_ACCOUNT_UPDATE') or hasRole('ROLE_ACCOUNT_DELETE')")
                .antMatchers("/payment").access("hasRole('ROLE_PAYMENT_CREATE') or hasRole('ROLE_PAYMENT_UPDATE') or hasRole('ROLE_PAYMENT_DELETE')")
                .antMatchers("/team").access("hasRole('ROLE_TEAM_CREATE') or hasRole('ROLE_TEAM_UPDATE') or hasRole('ROLE_TEAM_DELETE')")
                .antMatchers("/person").access("hasRole('ROLE_PERSON_CREATE') or hasRole('ROLE_PERSON_UPDATE') or hasRole('ROLE_PERSON_DELETE')")
                .antMatchers("/bank").access("hasRole('ROLE_BANK_CREATE') or hasRole('ROLE_BANK_UPDATE') or hasRole('ROLE_BANK_DELETE')")
                .antMatchers("/deposit").access("hasRole('ROLE_DEPOSIT_CREATE') or hasRole('ROLE_DEPOSIT_UPDATE') or hasRole('ROLE_DEPOSIT_DELETE')")
                .antMatchers("/withdraw").access("hasRole('ROLE_WITHDRAW_CREATE') or hasRole('ROLE_WITHDRAW_UPDATE') or hasRole('ROLE_WITHDRAW_DELETE')")
                .antMatchers("/billBuy").access("hasRole('ROLE_BILL_BUY_CREATE') or hasRole('ROLE_BILL_BUY_UPDATE') or hasRole('ROLE_BILL_BUY_DELETE')")
                .antMatchers("/billBuyType").access("hasRole('ROLE_BILL_BUY_TYPE_CREATE') or hasRole('ROLE_BILL_BUY_TYPE_UPDATE') or hasRole('ROLE_BILL_BUY_TYPE_DELETE')")
                .anyRequest().authenticated();
        http.formLogin()
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/menu")
                .permitAll();
        http.logout()
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
        http.rememberMe();
        http.csrf().disable();
        http.sessionManagement()
                .maximumSessions(2)
                .sessionRegistry(sessionRegistry());
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher() {
            @Override
            public void sessionDestroyed(HttpSessionEvent event) {
                SecurityContextImpl securityContext = (SecurityContextImpl) event.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
                if (securityContext != null) {
                    UserDetails userDetails = (UserDetails) securityContext.getAuthentication().getPrincipal();
                    Person person = personService.findByEmail(userDetails.getUsername());
                    person.setActive(false);
                    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
                    person.setIpAddress(request.getRemoteAddr());
                    personService.save(person);
                }
                super.sessionDestroyed(event);
            }
        });
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService((String email) -> {
                    Person person = personService.findByEmail(email);
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_PROFILE_UPDATE"));
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        log.info("فحص وجود المستخدم");
                        if (person == null) {
                            log.info("هذا المستخدم غير موجود");
                            throw new UsernameNotFoundException("هذا المستخدم غير موجود");
                        }
                        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
                        person.setLastLoginDate(new Date());
                        person.setActive(true);
                        person.setIpAddress(request.getRemoteAddr());
                        personService.save(person);
                        Optional.ofNullable(person.getTeam().getAuthorities()).ifPresent(value -> Arrays.asList(value.split(",")).stream().forEach(s -> authorities.add(new SimpleGrantedAuthority(s))));
                    }
                    return new User(person.getEmail(), person.getPassword(), person.getEnabled(), true, true, true, authorities);
                }
        ).passwordEncoder(passwordEncoder);
    }

}
