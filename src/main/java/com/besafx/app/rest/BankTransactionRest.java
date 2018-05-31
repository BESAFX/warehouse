package com.besafx.app.rest;

import com.besafx.app.auditing.PersonAwareUserDetails;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.*;
import com.besafx.app.init.Initializer;
import com.besafx.app.search.BankTransactionSearch;
import com.besafx.app.service.BankService;
import com.besafx.app.service.BankTransactionService;
import com.besafx.app.service.CustomerService;
import com.besafx.app.service.SupplierService;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/bankTransaction/")
public class BankTransactionRest {

    private final static Logger LOG = LoggerFactory.getLogger(BankTransactionRest.class);

    private final String FILTER_TABLE = "" +
            "**," +
            "bank[id,name]," +
            "transactionType[id,name]," +
            "person[id,contact[id,shortName]]";

    @Autowired
    private BankTransactionService bankTransactionService;

    @Autowired
    private BankTransactionSearch bankTransactionSearch;

    @Autowired
    private BankService bankService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping(value = "createDepositCompany/{amount}/{note}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_DEPOSIT_CREATE')")
    @Transactional
    public String createDepositCompany(
            @PathVariable(value = "amount") Double amount,
            @PathVariable(value = "note") String note
                                      ) {
        if(amount <= 0){
            throw new CustomException("عفواً، يجب أن تكون قيمة المعاملة المالية أكبر من الصفر");
        }
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setAmount(amount);
        bankTransaction.setDate(new DateTime().toDate());
        bankTransaction.setBank(Initializer.company.getBank());
        bankTransaction.setTransactionType(Initializer.transactionTypeDeposit);
        bankTransaction.setPerson(caller);
        StringBuilder builder = new StringBuilder();
        builder.append("إيداع مبلغ نقدي بقيمة ");
        builder.append(bankTransaction.getAmount());
        builder.append("ريال سعودي، ");
        builder.append(" لحساب المؤسسة / ");
        builder.append(Initializer.company.getName());
        builder.append("، " + note);
        bankTransaction.setNote(builder.toString());
        bankTransaction = bankTransactionService.save(bankTransaction);
        notificationService.notifyAll(Notification
                                              .builder()
                                              .message(builder.toString())
                                              .type("success").build());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), bankTransaction);
    }

    @PostMapping(value = "createDepositCustomer/{customerId}/{amount}/{note}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_DEPOSIT_CREATE')")
    @Transactional
    public String createDepositCustomer(
            @PathVariable(value = "customerId") Long customerId,
            @PathVariable(value = "amount") Double amount,
            @PathVariable(value = "note") String note
                                       ) {
        Customer customer = customerService.findOne(customerId);
        if (customer == null) {
            throw new CustomException("عفواً، لا يمكن العثور على هذا العميل.");
        }
        if(amount <= 0){
            throw new CustomException("عفواً، يجب أن تكون قيمة المعاملة المالية أكبر من الصفر");
        }
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setAmount(amount);
        bankTransaction.setDate(new DateTime().toDate());
        bankTransaction.setBank(customer.getBank());
        bankTransaction.setTransactionType(Initializer.transactionTypeDeposit);
        bankTransaction.setPerson(caller);
        StringBuilder builder = new StringBuilder();
        builder.append("إيداع مبلغ نقدي بقيمة ");
        builder.append(bankTransaction.getAmount());
        builder.append("ريال سعودي، ");
        builder.append(" لحساب العميل / ");
        builder.append(customer.getContact().getShortName());
        builder.append("، " + note);
        bankTransaction.setNote(builder.toString());
        bankTransaction = bankTransactionService.save(bankTransaction);
        notificationService.notifyAll(Notification
                                              .builder()
                                              .message(builder.toString())
                                              .type("success").build());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), bankTransaction);
    }

    @PostMapping(value = "createDepositSupplier/{supplierId}/{amount}/{note}",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_DEPOSIT_CREATE')")
    @Transactional
    public String createDepositSupplier(
            @PathVariable(value = "supplierId") Long supplierId,
            @PathVariable(value = "amount") Double amount,
            @PathVariable(value = "note") String note
                                       ) {
        Supplier supplier = supplierService.findOne(supplierId);
        if (supplier == null) {
            throw new CustomException("عفواً، لا يمكن العثور على هذا المورد.");
        }
        if(amount <= 0){
            throw new CustomException("عفواً، يجب أن تكون قيمة المعاملة المالية أكبر من الصفر");
        }
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setAmount(amount);
        bankTransaction.setDate(new DateTime().toDate());
        bankTransaction.setBank(supplier.getBank());
        bankTransaction.setTransactionType(Initializer.transactionTypeDeposit);
        bankTransaction.setPerson(caller);
        StringBuilder builder = new StringBuilder();
        builder.append("إيداع مبلغ نقدي بقيمة ");
        builder.append(bankTransaction.getAmount());
        builder.append("ريال سعودي، ");
        builder.append(" لحساب المورد / ");
        builder.append(supplier.getContact().getShortName());
        builder.append("، " + note);
        bankTransaction.setNote(builder.toString());
        bankTransaction = bankTransactionService.save(bankTransaction);
        notificationService.notifyAll(Notification
                                              .builder()
                                              .message(builder.toString())
                                              .type("success").build());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), bankTransaction);
    }

    @PostMapping(value = "createWithdrawCompany/{amount}/{note}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_WITHDRAW_CREATE')")
    @Transactional
    public String createWithdrawCompany(
            @PathVariable(value = "amount") Double amount,
            @PathVariable(value = "note") String note
                                      ) {
        if(amount <= 0){
            throw new CustomException("عفواً، يجب أن تكون قيمة المعاملة المالية أكبر من الصفر");
        }
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setAmount(amount);
        bankTransaction.setDate(new DateTime().toDate());
        bankTransaction.setBank(Initializer.company.getBank());
        bankTransaction.setTransactionType(Initializer.transactionTypeWithdraw);
        bankTransaction.setPerson(caller);
        StringBuilder builder = new StringBuilder();
        builder.append("سحب مبلغ نقدي بقيمة ");
        builder.append(bankTransaction.getAmount());
        builder.append("ريال سعودي، ");
        builder.append(" من حساب المؤسسة / ");
        builder.append(Initializer.company.getName());
        builder.append("، " + note);
        bankTransaction.setNote(builder.toString());
        bankTransaction = bankTransactionService.save(bankTransaction);
        notificationService.notifyAll(Notification
                                              .builder()
                                              .message(builder.toString())
                                              .type("success").build());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), bankTransaction);
    }

    @PostMapping(value = "createWithdrawCustomer/{customerId}/{amount}/{note}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_WITHDRAW_CREATE')")
    @Transactional
    public String createWithdrawCustomer(
            @PathVariable(value = "customerId") Long customerId,
            @PathVariable(value = "amount") Double amount,
            @PathVariable(value = "note") String note
                                       ) {
        Customer customer = customerService.findOne(customerId);
        if (customer == null) {
            throw new CustomException("عفواً، لا يمكن العثور على هذا العميل.");
        }
        if(amount <= 0){
            throw new CustomException("عفواً، يجب أن تكون قيمة المعاملة المالية أكبر من الصفر");
        }
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setAmount(amount);
        bankTransaction.setDate(new DateTime().toDate());
        bankTransaction.setBank(customer.getBank());
        bankTransaction.setTransactionType(Initializer.transactionTypeWithdraw);
        bankTransaction.setPerson(caller);
        StringBuilder builder = new StringBuilder();
        builder.append("سحب مبلغ نقدي بقيمة ");
        builder.append(bankTransaction.getAmount());
        builder.append("ريال سعودي، ");
        builder.append(" من حساب العميل / ");
        builder.append(customer.getContact().getShortName());
        builder.append("، " + note);
        bankTransaction.setNote(builder.toString());
        bankTransaction = bankTransactionService.save(bankTransaction);
        notificationService.notifyAll(Notification
                                              .builder()
                                              .message(builder.toString())
                                              .type("success").build());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), bankTransaction);
    }

    @PostMapping(value = "createWithdrawSupplier/{supplierId}/{amount}/{note}",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_WITHDRAW_CREATE')")
    @Transactional
    public String createWithdrawSupplier(
            @PathVariable(value = "supplierId") Long supplierId,
            @PathVariable(value = "amount") Double amount,
            @PathVariable(value = "note") String note
                                       ) {
        Supplier supplier = supplierService.findOne(supplierId);
        if (supplier == null) {
            throw new CustomException("عفواً، لا يمكن العثور على هذا المورد.");
        }
        if(amount <= 0){
            throw new CustomException("عفواً، يجب أن تكون قيمة المعاملة المالية أكبر من الصفر");
        }
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setAmount(amount);
        bankTransaction.setDate(new DateTime().toDate());
        bankTransaction.setBank(supplier.getBank());
        bankTransaction.setTransactionType(Initializer.transactionTypeWithdraw);
        bankTransaction.setPerson(caller);
        StringBuilder builder = new StringBuilder();
        builder.append("سحب مبلغ نقدي بقيمة ");
        builder.append(bankTransaction.getAmount());
        builder.append("ريال سعودي، ");
        builder.append(" من حساب المورد / ");
        builder.append(supplier.getContact().getShortName());
        builder.append("، " + note);
        bankTransaction.setNote(builder.toString());
        bankTransaction = bankTransactionService.save(bankTransaction);
        notificationService.notifyAll(Notification
                                              .builder()
                                              .message(builder.toString())
                                              .type("success").build());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), bankTransaction);
    }

    @GetMapping(value = "createTransfer/{amount}/{fromBankId}/{toBankId}/{note}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TRANSFER_CREATE')")
    @Transactional
    public void createTransfer(
            @PathVariable(value = "amount") Double amount,
            @PathVariable(value = "fromBankId") Long fromBankId,
            @PathVariable(value = "toBankId") Long toBankId,
            @PathVariable(value = "note") String note) {

        Bank fromBank = bankService.findOne(fromBankId);
        Bank toBank = bankService.findOne(toBankId);
        if (fromBank == null || toBank == null) {
            throw new CustomException("فضلا تأكد من بيانات الحسابات البنكية");
        }
        if(amount <= 0){
            throw new CustomException("عفواً، يجب أن تكون قيمة المعاملة المالية أكبر من الصفر");
        }

        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();

        {
            LOG.info("القيام بعملية السحب أولا من المرسل");
            BankTransaction bankTransactionWithdraw = new BankTransaction();
            bankTransactionWithdraw.setAmount(amount);
            bankTransactionWithdraw.setDate(new DateTime().toDate());
            bankTransactionWithdraw.setBank(fromBank);
            bankTransactionWithdraw.setTransactionType(Initializer.transactionTypeWithdrawTransfer);
            bankTransactionWithdraw.setPerson(caller);
            StringBuilder builder = new StringBuilder();
            builder.append("سحب مبلغ نقدي بقيمة ");
            builder.append(bankTransactionWithdraw.getAmount());
            builder.append("ريال سعودي، ");
            builder.append(" من الحساب البنكي / ");
            builder.append(bankTransactionWithdraw.getBank().getName());
            builder.append("، عملية تحويل إلى الحساب البنكي /  " + toBank.getName());
            builder.append("، " + note);
            bankTransactionWithdraw.setNote(builder.toString());
            bankTransactionService.save(bankTransactionWithdraw);
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message(builder.toString())
                                                  .type("success").build());
        }

        {
            LOG.info("القيام بعملية الإيداع ثانيا إلى المرسل إليه");
            BankTransaction bankTransactionDeposit = new BankTransaction();
            bankTransactionDeposit.setAmount(amount);
            bankTransactionDeposit.setDate(new DateTime().toDate());
            bankTransactionDeposit.setBank(toBank);
            bankTransactionDeposit.setTransactionType(Initializer.transactionTypeDepositTransfer);
            bankTransactionDeposit.setPerson(caller);
            StringBuilder builder = new StringBuilder();
            builder.append("إيداع مبلغ نقدي بقيمة ");
            builder.append(bankTransactionDeposit.getAmount());
            builder.append("ريال سعودي، ");
            builder.append(" للحساب البنكي / ");
            builder.append(bankTransactionDeposit.getBank().getName());
            builder.append("، عملية تحويل من الحساب البنكي / " + fromBank.getName());
            builder.append("، " + note);
            bankTransactionDeposit.setNote(builder.toString());
            bankTransactionService.save(bankTransactionDeposit);
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message(builder.toString())
                                                  .type("success").build());
        }

    }

    @GetMapping(value = "createExpense/{amount}/{note}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_EXPENSE_CREATE')")
    @Transactional
    public String createExpense(
            @PathVariable(value = "amount") Double amount,
            @PathVariable(value = "note") String note) {
        if(amount <= 0){
            throw new CustomException("عفواً، يجب أن تكون قيمة المعاملة المالية أكبر من الصفر");
        }
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setAmount(amount);
        bankTransaction.setDate(new DateTime().toDate());
        bankTransaction.setBank(Initializer.company.getBank());
        bankTransaction.setTransactionType(Initializer.transactionTypeExpense);
        bankTransaction.setPerson(caller);
        StringBuilder builder = new StringBuilder();
        builder.append("صرف مبلغ نقدي بقيمة ");
        builder.append(bankTransaction.getAmount());
        builder.append("ريال سعودي، ");
        builder.append(" من حساب المؤسسة / ");
        builder.append(Initializer.company.getName());
        builder.append("، " + note);
        bankTransaction.setNote(builder.toString());
        bankTransaction = bankTransactionService.save(bankTransaction);
        notificationService.notifyAll(Notification
                                              .builder()
                                              .message(builder.toString())
                                              .type("success").build());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), bankTransaction);
    }

    @GetMapping(value = "findOne/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                                       bankTransactionService.findOne(id));
    }

    @GetMapping(value = "findByDateBetweenOrTransactionTypeCodeIn", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByDateBetweenOrTransactionTypeCodeIn(
            @RequestParam(value = "dateFrom", required = false) final Long dateFrom,
            @RequestParam(value = "dateTo", required = false) final Long dateTo,
            @RequestParam(value = "transactionTypeCodes", required = false) final List<String> transactionTypeCodes) {

        List<Specification<BankTransaction>> predicates = new ArrayList<>();

        Optional.ofNullable(dateFrom)
                .ifPresent(value -> predicates.add(
                        (root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("date"), new DateTime(value).withTimeAtStartOfDay().toDate())));

        Optional.ofNullable(dateTo)
                .ifPresent(value -> predicates.add(
                        (root, cq, cb) -> cb.lessThanOrEqualTo(root.get("date"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));

        Optional.ofNullable(transactionTypeCodes)
                .ifPresent(value -> predicates.add(
                        (root, cq, cb) -> root.get("transactionType").get("code").in(transactionTypeCodes)));

        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), bankTransactionService.findAll(result));
        } else {
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), new ArrayList<>());
        }
    }

    @GetMapping(value = "filter", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String filter(
            @RequestParam(value = "codeFrom", required = false) final Integer codeFrom,
            @RequestParam(value = "codeTo", required = false) final Integer codeTo,
            @RequestParam(value = "dateFrom", required = false) final Long dateFrom,
            @RequestParam(value = "dateTo", required = false) final Long dateTo,
            @RequestParam(value = "transactionTypeCodes", required = false) final List<String> transactionTypeCodes,
            Pageable pageable) {
        return SquigglyUtils.stringify(
                Squiggly.init(
                        new ObjectMapper(),
                        "**,".concat("content[").concat(FILTER_TABLE).concat("]")),
                bankTransactionSearch.filter(
                        codeFrom,
                        codeTo,
                        dateFrom,
                        dateTo,
                        transactionTypeCodes,
                        pageable));
    }
}
