package com.besafx.app.rest;

import com.besafx.app.config.CustomException;
import com.besafx.app.config.SendSMS;
import com.besafx.app.entity.BillPurchasePayment;
import com.besafx.app.entity.Customer;
import com.besafx.app.search.CustomerSearch;
import com.besafx.app.service.*;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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
            "-billPurchases";

    private final String FILTER_DETAILS = "" +
            "**," +
            "billPurchases[**,-customer,supplier[id,contact[id,mobile,shortName]],sponsor1[id,contact[id,mobile,shortName]],sponsor2[id,contact[id,mobile,shortName]],-billPurchaseProducts,contractPremiums[**,-billPurchase,-contractPayments],-contractPayments,person[id,contact[id,shortName]]]";

    private final String FILTER_COMBO = "" +
            "id," +
            "code," +
            "contact[id,shortName,mobile]";

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerSearch customerSearch;

    @Autowired
    private BillPurchaseService billPurchaseService;

    @Autowired
    private BillPurchaseProductService billPurchaseProductService;

    @Autowired
    private BankTransactionService bankTransactionService;

    @Autowired
    private BillPurchasePaymentService billPurchasePaymentService;

    @Autowired
    private ContractPremiumService contractPremiumService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SendSMS sendSMS;

    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_CUSTOMER_CREATE')")
    @Transactional
    public String create(@RequestBody Customer customer) {
        Customer topCustomer = customerService.findTopByOrderByCodeDesc();
        if (topCustomer == null) {
            customer.setCode(1);
        } else {
            customer.setCode(topCustomer.getCode() + 1);
        }
        customer.setContact(billPurchaseService.save(customer.getContact()));
        customer.setEnabled(true);
        customer = customerService.save(customer);
        notificationService.notifyAll(Notification
                                              .builder()
                                              .message("تم انشاء حساب عميل جديد بنجاح")
                                              .type("success").build());
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
            customer.setContact(billPurchaseService.save(customer.getContact()));
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
            LOG.info("حذف كل سلع العقود");
            billPurchaseProductService.delete(customer.getBillPurchases()
                                                      .stream()
                                                      .flatMap(contract -> contract.getContractProducts().stream())
                                                      .collect(Collectors.toList()));

            LOG.info("حذف كل معاملات البنك لدفعات العقود");
            bankTransactionService.delete(
                    customer.getBillPurchases()
                            .stream()
                            .flatMap(contract -> contract.getContractPayments().stream())
                            .map(BillPurchasePayment::getBankTransaction)
                            .collect(Collectors.toList()));

            LOG.info("حذف كل دفعات العقود");
            billPurchasePaymentService.delete(customer.getBillPurchases()
                                                      .stream()
                                                      .flatMap(contract -> contract.getContractPayments().stream())
                                                      .collect(Collectors.toList()));

            LOG.info("حذف كل أقساط العقود");
            contractPremiumService.delete(customer.getBillPurchases()
                                                  .stream()
                                                  .flatMap(contract -> contract.getContractPremiums().stream())
                                                  .collect(Collectors.toList()));

            LOG.info("حذف العقود");
            contractService.delete(customer.getBillPurchases());

            LOG.info("حذف بيانات الاتصال");
            billPurchaseService.delete(customer.getContact());

            LOG.info("حذف العميل");
            customerService.delete(customer);

            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message("تم حذف العميل وكل ما يتعلق به من عقود وحسابات بنجاح")
                                                  .type("error").build());
        }
    }

    @PostMapping(value = "sendMessage/{customerIds}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_SMS_SEND')")
    public void sendMessage(@RequestBody String content, @PathVariable List<Long> customerIds) throws Exception {
        ListIterator<Long> listIterator = customerIds.listIterator();
        while (listIterator.hasNext()){
            Long id = listIterator.next();
            Customer customer = customerService.findOne(id);
            String message = content.replaceAll("#remain#", customer.getContractsRemain().toString());
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
