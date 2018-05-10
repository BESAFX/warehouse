package com.besafx.app.rest;

import com.besafx.app.auditing.PersonAwareUserDetails;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.*;
import com.besafx.app.init.Initializer;
import com.besafx.app.search.ContractSearch;
import com.besafx.app.service.*;
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

import java.util.Date;
import java.util.ListIterator;

@RestController
@RequestMapping(value = "/api/contract/")
public class ContractRest {

    private final static Logger LOG = LoggerFactory.getLogger(ContractRest.class);

    private final String FILTER_TABLE = "" +
            "**," +
            "customer[id,contact[id,mobile,shortName]]," +
            "seller[id,contact[id,mobile,shortName]]," +
            "-contractProducts," +
            "-contractPremiums," +
            "person[id,contact[id,shortName]]";

    @Autowired
    private ContractService contractService;

    @Autowired
    private ContractProductService contractProductService;

    @Autowired
    private ContractPremiumService contractPremiumService;

    @Autowired
    private ContractSearch contractSearch;

    @Autowired
    private NotificationService notificationService;

    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_CONTRACT_CREATE')")
    public String create(@RequestBody Contract contract) {
        Contract topContract = contractService.findTopByOrderByCodeDesc();
        if (topContract == null) {
            contract.setCode(Long.valueOf(1));
        } else {
            contract.setCode(topContract.getCode() + 1);
        }
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        contract.setPerson(caller);
        contract.setDate(new DateTime().toDate());
        contract = contractService.save(contract);
        LOG.info("ربط الأصناف المطلوبة مع العقد");
        ListIterator<ContractProduct> contractProductListIterator = contract.getContractProducts().listIterator();
        while (contractProductListIterator.hasNext()){
            ContractProduct contractProduct = contractProductListIterator.next();
            contractProduct.setContract(contract);
            contractProductListIterator.set(contractProductService.save(contractProduct));
        }
        LOG.info("ربط الأقساط مع العقد");
        ListIterator<ContractPremium> contractPremiumListIterator = contract.getContractPremiums().listIterator();
        while (contractPremiumListIterator.hasNext()){
            ContractPremium contractPremium = contractPremiumListIterator.next();
            contractPremium.setContract(contract);
            contractPremiumListIterator.set(contractPremiumService.save(contractPremium));
        }
        StringBuilder builder = new StringBuilder();
        builder.append("تم إنشاء العقد بنجاح بمجموع أسعار = ");
        builder.append(contract.getTotalPrice());
        builder.append("، وأصناف عدد " + contract.getContractProducts().size() + " صنف");
        builder.append("، تسدد على " + contract.getContractPremiums().size() +  " قسط");
        notificationService.notifyAll(Notification
                                              .builder()
                                              .message(builder.toString())
                                              .type("success").build());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), contract);
    }

    @DeleteMapping(value = "delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_CONTRACT_DELETE')")
    public void delete(@PathVariable Long id) {
        Contract contract = contractService.findOne(id);
        if (contract != null) {
            contractService.delete(id);
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .message("تم حذف العقد وكل ما يتعلق به من حسابات بنجاح")
                                                  .type("error").build());
        }
    }

    @GetMapping(value = "findOne/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                                       contractService.findOne(id));
    }

    @GetMapping(value = "filter", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String filter(
            //Contract Filters
            @RequestParam(value = "codeFrom", required = false) final Integer codeFrom,
            @RequestParam(value = "codeTo", required = false) final Integer codeTo,
            @RequestParam(value = "dateFrom", required = false) final Date dateFrom,
            @RequestParam(value = "dateTo", required = false) final Date dateTo,
            //Customer Filters
            @RequestParam(value = "customerCodeFrom", required = false) final Integer customerCodeFrom,
            @RequestParam(value = "customerCodeTo", required = false) final Integer customerCodeTo,
            @RequestParam(value = "customerRegisterDateFrom", required = false) final Date customerRegisterDateFrom,
            @RequestParam(value = "customerRegisterDateTo", required = false) final Date customerRegisterDateTo,
            @RequestParam(value = "customerName", required = false) final String customerName,
            @RequestParam(value = "customerMobile", required = false) final String customerMobile,
            //Seller Filters
            @RequestParam(value = "sellerCodeFrom", required = false) final Integer sellerCodeFrom,
            @RequestParam(value = "sellerCodeTo", required = false) final Integer sellerCodeTo,
            @RequestParam(value = "sellerRegisterDateFrom", required = false) final Date sellerRegisterDateFrom,
            @RequestParam(value = "sellerRegisterDateTo", required = false) final Date sellerRegisterDateTo,
            @RequestParam(value = "sellerName", required = false) final String sellerName,
            @RequestParam(value = "sellerMobile", required = false) final String sellerMobile,
            @RequestParam(value = "filterCompareType", required = false) final String filterCompareType,
            Pageable pageable) {
        return SquigglyUtils.stringify(
                Squiggly.init(
                        new ObjectMapper(),
                        "**,".concat("content[").concat(FILTER_TABLE).concat("]")),
                contractSearch.filter(
                        codeFrom,
                        codeTo,
                        dateFrom,
                        dateTo,
                        customerCodeFrom,
                        customerCodeTo,
                        customerRegisterDateFrom,
                        customerRegisterDateTo,
                        customerName,
                        customerMobile,
                        sellerCodeFrom,
                        sellerCodeTo,
                        sellerRegisterDateFrom,
                        sellerRegisterDateTo,
                        sellerName,
                        sellerMobile,
                        filterCompareType,
                        pageable));
    }
}
