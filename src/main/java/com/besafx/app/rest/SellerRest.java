package com.besafx.app.rest;

import com.besafx.app.auditing.PersonAwareUserDetails;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.BankTransaction;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Seller;
import com.besafx.app.init.Initializer;
import com.besafx.app.search.SellerSearch;
import com.besafx.app.service.BankTransactionService;
import com.besafx.app.service.ContactService;
import com.besafx.app.service.SellerService;
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
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Date;

@RestController
@RequestMapping(value = "/api/seller/")
public class SellerRest {

    private final static Logger LOG = LoggerFactory.getLogger(SellerRest.class);

    private final String FILTER_TABLE = "" +
            "**," +
            "-productPurchases," +
            "-contracts," +
            "seller[id]";

    private final String FILTER_DETAILS = "" +
            "**," +
            "productPurchases[**,product[id,name],-seller,-contractProducts,person[id,contact[id,shortName]]]," +
            "-contracts," +
            "seller[id]";

    @Autowired
    private SellerService sellerService;

    @Autowired
    private SellerSearch sellerSearch;

    @Autowired
    private ContactService contactService;

    @Autowired
    private BankTransactionService bankTransactionService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping(value = "create/{openCash}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TEAM_CREATE')")
    @Transactional
    public String create(@PathVariable(value = "openCash") Double openCash, @RequestBody Seller seller) {
        Seller topSeller = sellerService.findTopByOrderByCodeDesc();
        if (topSeller == null) {
            seller.setCode(1);
        } else {
            seller.setCode(topSeller.getCode() + 1);
        }
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        seller.setSeller(caller.getCompany().getSeller());
        seller.setRegisterDate(new DateTime().toDate());
        seller.setEnabled(true);
        seller.setContact(contactService.save(seller.getContact()));
        seller = sellerService.save(seller);
        notificationService.notifyAll(Notification
                                              .builder()
                                              .message("تم انشاء حساب مستثمر جديد بنجاح")
                                              .type("success").build());
        LOG.info("إنشاء الرصيد الافتتاحي وعمل عملية إيداع بالمبلغ");
        if (openCash > 0) {
            BankTransaction bankTransaction = new BankTransaction();
            bankTransaction.setAmount(openCash);
            bankTransaction.setBank(Initializer.bank);
            bankTransaction.setSeller(seller);
            bankTransaction.setTransactionType(Initializer.transactionTypeDeposit);
            bankTransaction.setDate(new DateTime().toDate());
            bankTransaction.setPerson(caller);
            StringBuilder builder = new StringBuilder();
            builder.append("إيداع مبلغ نقدي بقيمة ");
            builder.append(openCash);
            builder.append("ريال سعودي، ");
            builder.append(" لـ / ");
            builder.append(seller.getShortName());
            builder.append("، رصيد افتتاحي");
            bankTransaction.setNote(builder.toString());
            bankTransactionService.save(bankTransaction);
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message(builder.toString())
                                                  .type("success").build());
        }
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), seller);
    }

    @PutMapping(value = "update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TEAM_UPDATE')")
    @Transactional
    public String update(@RequestBody Seller seller) {
        if (sellerService.findByCodeAndIdIsNot(seller.getCode(), seller.getId()) != null) {
            throw new CustomException("هذا الكود مستخدم سابقاً، فضلاً قم بتغير الكود.");
        }
        Seller object = sellerService.findOne(seller.getId());
        if (object != null) {
            seller.setContact(contactService.save(seller.getContact()));
            seller = sellerService.save(seller);
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message("تم تعديل بيانات المستثمر بنجاح")
                                                  .type("success").build());
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), seller);
        } else {
            return null;
        }
    }

    @DeleteMapping(value = "delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TEAM_DELETE')")
    @Transactional
    public void delete(@PathVariable Long id) {
        Seller seller = sellerService.findOne(id);
        if (seller != null) {
            sellerService.delete(id);
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message("تم حذف المستثمر وكل ما يتعلق به من عقود وحسابات بنجاح")
                                                  .type("error").build());
        }
    }

    @GetMapping(value = "findOne/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_DETAILS),
                                       sellerService.findOne(id));
    }

    @GetMapping(value = "filter", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String filter(
            @RequestParam(value = "codeFrom", required = false) final Integer codeFrom,
            @RequestParam(value = "codeTo", required = false) final Integer codeTo,
            @RequestParam(value = "registerDateFrom", required = false) final Date registerDateFrom,
            @RequestParam(value = "registerDateTo", required = false) final Date registerDateTo,
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
                sellerSearch.filter(
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
