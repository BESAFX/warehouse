package com.besafx.app.search;

import com.besafx.app.entity.BillPurchasePayment;
import com.besafx.app.service.BillPurchasePaymentService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ContractPaymentSearch {

    private static final Logger LOG = LoggerFactory.getLogger(ContractPaymentSearch.class);

    @Autowired
    private BillPurchasePaymentService billPurchasePaymentService;

    public Page<BillPurchasePayment> filter(
            //BillPurchasePayment Filters
            final Long dateFrom,
            final Long dateTo,
            //BillPurchase Filters
            final Integer contractCodeFrom,
            final Integer contractCodeTo,
            final Long contractDateFrom,
            final Long contractDateTo,
            //Customer Filters
            final String customerName,
            final String customerMobile,
            //Supplier Filters
            final String supplierName,
            final String supplierMobile,
            final String filterCompareType,
            Pageable pageRequest) {

        List<Specification<BillPurchasePayment>> predicates = new ArrayList<>();
        //BillPurchasePayment Specification
        Optional.ofNullable(dateFrom)
                .ifPresent(value -> predicates.add(
                        (root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("date"),
                                                                  new DateTime(value).withTimeAtStartOfDay().toDate())));

        Optional.ofNullable(dateTo)
                .ifPresent(value -> predicates.add(
                        (root, cq, cb) -> cb.lessThanOrEqualTo(root.get("date"),
                                                               new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));

        //BillPurchase Specification
        Optional.ofNullable(contractCodeFrom)
                .ifPresent(value -> predicates.add(
                        (root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("billPurchase").get("code"), value)));

        Optional.ofNullable(contractCodeTo)
                .ifPresent(value -> predicates.add(
                        (root, cq, cb) -> cb.lessThanOrEqualTo(root.get("billPurchase").get("code"), value)));

        Optional.ofNullable(contractDateFrom)
                .ifPresent(value -> predicates.add(
                        (root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("billPurchase").get("date"),
                                                                  new DateTime(value).withTimeAtStartOfDay().toDate())));

        Optional.ofNullable(contractDateTo)
                .ifPresent(value -> predicates.add(
                        (root, cq, cb) -> cb.lessThanOrEqualTo(root.get("billPurchase").get("date"),
                                                               new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));

        //Customer Specification
        Optional.ofNullable(customerName)
                .ifPresent(value -> predicates.add(
                        (root, cq, cb) -> cb.like(root.get("billPurchase").get("customer").get("contact").get("name"), "%" + value + "%")));

        Optional.ofNullable(customerMobile)
                .ifPresent(value -> predicates.add(
                        (root, cq, cb) -> cb.like(root.get("billPurchase").get("customer").get("contact").get("mobile"), "%" + value + "%")));

        //Supplier Specification
        Optional.ofNullable(supplierName)
                .ifPresent(value -> predicates.add(
                        (root, cq, cb) -> cb.like(root.get("billPurchase").get("supplier").get("contact").get("name"), "%" + value + "%")));

        Optional.ofNullable(supplierMobile)
                .ifPresent(value -> predicates.add(
                        (root, cq, cb) -> cb.like(root.get("billPurchase").get("supplier").get("contact").get("mobile"), "%" + value + "%")));

        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                if (filterCompareType == null) {
                    result = Specifications.where(result).and(predicates.get(i));
                    continue;
                }
                result = filterCompareType.equalsIgnoreCase("and") ?
                        Specifications.where(result).and(predicates.get(i)) :
                        Specifications.where(result).or(predicates.get(i));
            }
            return billPurchasePaymentService.findAll(result, pageRequest);
        } else {
            return billPurchasePaymentService.findAll(pageRequest);
        }
    }
}
