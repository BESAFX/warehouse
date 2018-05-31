package com.besafx.app.init;

import com.besafx.app.entity.*;
import com.besafx.app.service.*;
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

    private final static Logger LOG = LoggerFactory.getLogger(Initializer.class);

    public static Company company;

    public static TransactionType transactionTypeDeposit;

    public static TransactionType transactionTypeDepositTransfer;

    public static TransactionType transactionTypeWithdraw;

    public static TransactionType transactionTypeWithdrawTransfer;

    public static TransactionType transactionTypeExpense;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private BankService bankService;

    @Autowired
    private TransactionTypeService transactionTypeService;

    @Autowired
    private PersonService personService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TeamService teamService;

    @Override
    public void run(String... args) {
        start();
        init();
    }

    private void start() {

        LOG.info("بداية العمل على إنشاء الحسابات الافتراضية والاساسية لتشغيل البرنامج لأول مرة");

        Company company = null;

        if (Lists.newArrayList(companyService.findAll()).isEmpty()) {

            LOG.info("إنشاء الحساب البنكي المركزي");
            Bank bank = new Bank();
            bank.setName("الصندوق");
            bankService.save(bank);

            LOG.info("إنشاء حساب الشركة");
            company = new Company();
            company.setBank(bank);
            companyService.save(company);

            LOG.info("إنشاء أنواع العمليات المالية");

            LOG.info("عملية الإيداع");
            TransactionType transactionTypeDeposit = new TransactionType();
            transactionTypeDeposit.setCode("Deposit");
            transactionTypeDeposit.setName("إيداع");
            transactionTypeService.save(transactionTypeDeposit);

            LOG.info("عملية الإيداع[تحويل]");
            TransactionType transactionTypeDepositTransfer = new TransactionType();
            transactionTypeDepositTransfer.setCode("Deposit_Transfer");
            transactionTypeDepositTransfer.setName("إيداع[تحويل]");
            transactionTypeDepositTransfer.setTransactionType(transactionTypeDeposit);
            transactionTypeService.save(transactionTypeDepositTransfer);

            LOG.info("عملية السحب");
            TransactionType transactionTypeWithdraw = new TransactionType();
            transactionTypeWithdraw.setCode("Withdraw");
            transactionTypeWithdraw.setName("سحب");
            transactionTypeService.save(transactionTypeWithdraw);

            LOG.info("عملية السحب[تحويل]");
            TransactionType transactionTypeWithdrawTransfer = new TransactionType();
            transactionTypeWithdrawTransfer.setCode("Withdraw_Transfer");
            transactionTypeWithdrawTransfer.setName("سحب[تحويل]");
            transactionTypeWithdrawTransfer.setTransactionType(transactionTypeWithdraw);
            transactionTypeService.save(transactionTypeWithdrawTransfer);

            LOG.info("[مصروفات]");
            TransactionType transactionTypeExpense = new TransactionType();
            transactionTypeExpense.setCode("Expense");
            transactionTypeExpense.setName("[مصروفات]");
            transactionTypeExpense.setTransactionType(transactionTypeWithdraw);
            transactionTypeService.save(transactionTypeExpense);

        }

        if (Lists.newArrayList(personService.findAll()).isEmpty()) {
            Person person = new Person();
            Contact contact = new Contact();
            contact.setNickname("المهندس");
            contact.setName("بسام المهدي");
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
                                            "ROLE_DEPOSIT_CREATE",
                                            "ROLE_WITHDRAW_CREATE",
                                            "ROLE_TRANSFER_CREATE",
                                            "ROLE_EXPENSE_CREATE",
                                            "ROLE_SMS_SEND",
                                            "ROLE_CUSTOMER_CREATE",
                                            "ROLE_CUSTOMER_UPDATE",
                                            "ROLE_CUSTOMER_DELETE",
                                            "ROLE_SUPPLIER_CREATE",
                                            "ROLE_SUPPLIER_UPDATE",
                                            "ROLE_SUPPLIER_DELETE",
                                            "ROLE_PRODUCT_CREATE",
                                            "ROLE_PRODUCT_UPDATE",
                                            "ROLE_PRODUCT_DELETE",
                                            "ROLE_BILL_PURCHASE_CREATE",
                                            "ROLE_BILL_PURCHASE_DELETE",
                                            "ROLE_BILL_PURCHASE_PRODUCT_CREATE",
                                            "ROLE_BILL_PURCHASE_PRODUCT_DELETE",
                                            "ROLE_BILL_PURCHASE_PAYMENT_CREATE",
                                            "ROLE_BILL_PURCHASE_PAYMENT_DELETE",
                                            "ROLE_BILL_SELL_CREATE",
                                            "ROLE_BILL_SELL_DELETE",
                                            "ROLE_BILL_SELL_PRODUCT_CREATE",
                                            "ROLE_BILL_SELL_PRODUCT_DELETE",
                                            "ROLE_BILL_SELL_PAYMENT_CREATE",
                                            "ROLE_BILL_SELL_PAYMENT_DELETE",
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

    private void init() {

        LOG.info("تعريف بعض المتغيرات الهامة");

        company = companyService.findFirstBy();

        transactionTypeDeposit = transactionTypeService.findByCode("Deposit");

        transactionTypeDepositTransfer = transactionTypeService.findByCode("Deposit_Transfer");

        transactionTypeWithdraw = transactionTypeService.findByCode("Withdraw");

        transactionTypeWithdrawTransfer = transactionTypeService.findByCode("Withdraw_Transfer");

        transactionTypeExpense = transactionTypeService.findByCode("Expense");

    }
}
