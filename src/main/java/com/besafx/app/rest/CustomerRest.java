package com.besafx.app.rest;

import com.besafx.app.auditing.PersonAwareUserDetails;
import com.besafx.app.config.CustomException;
import com.besafx.app.config.SendSMS;
import com.besafx.app.entity.BankTransaction;
import com.besafx.app.entity.BillSellPayment;
import com.besafx.app.entity.Customer;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.projection.BankTransactionAmount;
import com.besafx.app.init.Initializer;
import com.besafx.app.search.CustomerSearch;
import com.besafx.app.service.*;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/customer/")
public class CustomerRest {

    private final static Logger LOG = LoggerFactory.getLogger(CustomerRest.class);

    private final String FILTER_TABLE = "" +
            "**," +
            "-billSells";

    private final String FILTER_DETAILS = "" +
            "**," +
            "billSells[**,-customer,-billSellProducts,-contractPayments,person[id,contact[id,shortName]]]";

    private final String FILTER_COMBO = "" +
            "id," +
            "code," +
            "contact[id,shortName,mobile]";

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerSearch customerSearch;

    @Autowired
    private ContactService contactService;

    @Autowired
    private BankService bankService;

    @Autowired
    private BillSellService billSellService;

    @Autowired
    private BillSellProductService billSellProductService;

    @Autowired
    private BankTransactionService bankTransactionService;

    @Autowired
    private BillSellPaymentService billSellPaymentService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SendSMS sendSMS;

    @PostMapping(value = "create/{openCash}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_CUSTOMER_CREATE')")
    @Transactional
    public String create(@PathVariable(value = "openCash") Double openCash, @RequestBody Customer customer) {
        Customer topCustomer = customerService.findTopByOrderByCodeDesc();
        if (topCustomer == null) {
            customer.setCode(1);
        } else {
            customer.setCode(topCustomer.getCode() + 1);
        }
        customer.setRegisterDate(new DateTime().toDate());
        customer.setEnabled(true);
        customer.setContact(contactService.save(customer.getContact()));
        customer.setBank(bankService.save(customer.getBank()));
        customer = customerService.save(customer);

        notificationService.notifyAll(Notification
                                              .builder()
                                              .message("تم انشاء حساب عميل جديد بنجاح")
                                              .type("success").build());

        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        LOG.info("إنشاء الرصيد الافتتاحي وعمل عملية إيداع بالمبلغ");
        if (openCash > 0) {
            BankTransaction bankTransaction = new BankTransaction();
            bankTransaction.setAmount(openCash);
            bankTransaction.setDate(new DateTime().toDate());
            bankTransaction.setBank(customer.getBank());
            bankTransaction.setTransactionType(Initializer.transactionTypeDeposit);
            bankTransaction.setPerson(caller);
            StringBuilder builder = new StringBuilder();
            builder.append("إيداع مبلغ نقدي بقيمة ");
            builder.append(openCash);
            builder.append("ريال سعودي، ");
            builder.append(" للعميل / ");
            builder.append(customer.getContact().getShortName());
            builder.append("، رصيد افتتاحي");
            bankTransaction.setNote(builder.toString());

            bankTransactionService.save(bankTransaction);

            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message(builder.toString())
                                                  .type("success").build());
        }
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), customer);
    }

    @PutMapping(value = "update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_CUSTOMER_UPDATE')")
    @Transactional
    public String update(@RequestBody Customer customer) {
        if (customerService.findByCodeAndIdIsNot(customer.getCode(), customer.getId()) != null) {
            throw new CustomException("هذا الكود مستخدم سابقاً، فضلاً قم بتغير الكود.");
        }
        Customer object = customerService.findOne(customer.getId());
        if (object != null) {
            customer.setContact(contactService.save(customer.getContact()));
            customer.setBank(bankService.save(customer.getBank()));
            customer = customerService.save(customer);

            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message("تم تعديل بيانات العميل بنجاح")
                                                  .type("success").build());

            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), customer);
        } else {
            return null;
        }
    }

    @DeleteMapping(value = "delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_CUSTOMER_DELETE')")
    @Transactional
    public void delete(@PathVariable Long id) {
        Customer customer = customerService.findOne(id);
        if (customer != null) {
            LOG.info("حذف كل سلع فواتير البيع");
            billSellProductService.delete(customer.getBillSells()
                                                      .stream()
                                                      .flatMap(billSell -> billSell.getBillSellProducts().stream())
                                                      .collect(Collectors.toList()));

            LOG.info("حذف كل دفعات فواتير البيع");
            billSellPaymentService.delete(customer.getBillSells()
                                                      .stream()
                                                      .flatMap(billSell -> billSell.getBillSellPayments().stream())
                                                      .collect(Collectors.toList()));

            LOG.info("حذف كل معاملات البنك لدفعات فواتير البيع");
            bankTransactionService.delete(
                    customer.getBillSells()
                            .stream()
                            .flatMap(billSell -> billSell.getBillSellPayments().stream())
                            .map(BillSellPayment::getBankTransaction)
                            .collect(Collectors.toList()));

            LOG.info("حذف فواتير البيع");
            billSellService.delete(customer.getBillSells());

            LOG.info("حذف بيانات الاتصال");
            contactService.delete(customer.getContact());

            LOG.info("حذف كل المعاملات المالية للحساب البنكي");
            bankTransactionService.delete(customer.getBank().getBankTransactions());

            LOG.info("حذف الحساب البنكي");
            bankService.delete(customer.getBank());

            LOG.info("حذف العميل");
            customerService.delete(customer);

            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message("تم حذف العميل وكل ما يتعلق به من فواتير وحسابات ومعاملات مالية بنجاح")
                                                  .type("error").build());
        }
    }

    @PostMapping(value = "sendMessage/{customerIds}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_SMS_SEND')")
    @Transactional
    public void sendMessage(@RequestBody String content, @PathVariable List<Long> customerIds) throws Exception {
        ListIterator<Long> listIterator = customerIds.listIterator();
        while (listIterator.hasNext()){
            Long id = listIterator.next();
            Customer customer = customerService.findOne(id);
            String message = content.replaceAll("#remain#", customer.getBillsRemain().toString());
            Future<String> task = sendSMS.sendMessage(customer.getContact().getMobile(), message);
            String taskResult = task.get();
            StringBuilder builder = new StringBuilder();
            builder.append("الرقم / ");
            builder.append(customer.getContact().getMobile());
            builder.append("<br/>");
            builder.append(" محتوى الرسالة : ");
            builder.append(message);
            builder.append("<br/>");
            builder.append(" ، نتيجة الإرسال: ");
            builder.append(taskResult);
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message(builder.toString())
                                                  .type("information").build());
        }
    }

    @GetMapping(value = "findAllCombo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAllCombo() {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_COMBO),
                                       customerService.findAll(new Sort(Sort.Direction.ASC, "contact.shortName")));
    }

    @GetMapping(value = "findCustomerBalance/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Transactional
    public String findCustomerBalance(@PathVariable(value = "id") Long id) {
        Customer customer = customerService.findOne(id);

        Double depositAmount = bankTransactionService
                .findByBankAndTransactionTypeIn(customer.getBank(), Lists.newArrayList(
                        Initializer.transactionTypeDeposit,
                        Initializer.transactionTypeDepositTransfer), BankTransactionAmount.class)
                .stream().mapToDouble(BankTransactionAmount::getAmount).sum();

        Double withdrawAmount = bankTransactionService
                .findByBankAndTransactionTypeIn(customer.getBank(), Lists.newArrayList(
                        Initializer.transactionTypeWithdraw,
                        Initializer.transactionTypeWithdrawTransfer,
                        Initializer.transactionTypeExpense), BankTransactionAmount.class)
                .stream().mapToDouble(BankTransactionAmount::getAmount).sum();

        LOG.info("مجموع الإيداعات = " + depositAmount);
        customer.setTotalDeposits(depositAmount);

        LOG.info("مجموع السحبيات = " + withdrawAmount);
        customer.setTotalWithdraws(withdrawAmount);

        customer.setBalance(depositAmount - withdrawAmount);

        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), customer);
    }

    @GetMapping(value = "findAllCustomerBalance", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Transactional
    public String findAllCustomerBalance() {
        List<Customer> customers = Lists.newArrayList(customerService.findAll());
        ListIterator<Customer> bankListIterator = customers.listIterator();
        while (bankListIterator.hasNext()) {

            Customer customer = bankListIterator.next();

            Double depositAmount = bankTransactionService
                    .findByBankAndTransactionTypeIn(customer.getBank(), Lists.newArrayList(
                            Initializer.transactionTypeDeposit,
                            Initializer.transactionTypeDepositTransfer), BankTransactionAmount.class)
                    .stream().mapToDouble(BankTransactionAmount::getAmount).sum();

            Double withdrawAmount = bankTransactionService
                    .findByBankAndTransactionTypeIn(customer.getBank(), Lists.newArrayList(
                            Initializer.transactionTypeWithdraw,
                            Initializer.transactionTypeWithdrawTransfer,
                            Initializer.transactionTypeExpense), BankTransactionAmount.class)
                    .stream().mapToDouble(BankTransactionAmount::getAmount).sum();

            LOG.info("مجموع الإيداعات = " + depositAmount);
            customer.setTotalDeposits(depositAmount);

            LOG.info("مجموع السحبيات = " + withdrawAmount);
            customer.setTotalWithdraws(withdrawAmount);

            customer.setBalance(depositAmount - withdrawAmount);
        }
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), customers);
    }

    @GetMapping(value = "findOne/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_DETAILS),
                                       customerService.findOne(id));
    }

    @GetMapping(value = "filter", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String filter(
            @RequestParam(value = "codeFrom", required = false) final Integer codeFrom,
            @RequestParam(value = "codeTo", required = false) final Integer codeTo,
            @RequestParam(value = "registerDateFrom", required = false) final Long registerDateFrom,
            @RequestParam(value = "registerDateTo", required = false) final Long registerDateTo,
            @RequestParam(value = "name", required = false) final String name,
            @RequestParam(value = "mobile", required = false) final String mobile,
            @RequestParam(value = "phone", required = false) final String phone,
            @RequestParam(value = "nationality", required = false) final String nationality,
            @RequestParam(value = "identityNumber", required = false) final String identityNumber,
            @RequestParam(value = "qualification", required = false) final String qualification,
            @RequestParam(value = "filterCompareType", required = false) final String filterCompareType,
            Pageable pageable) {
        return SquigglyUtils.stringify(
                Squiggly.init(
                        new ObjectMapper(),
                        "**,".concat("content[").concat(FILTER_TABLE).concat("]")),
                customerSearch.filter(
                        codeFrom,
                        codeTo,
                        registerDateFrom,
                        registerDateTo,
                        name,
                        mobile,
                        phone,
                        nationality,
                        identityNumber,
                        qualification,
                        filterCompareType,
                        pageable));
    }
}
