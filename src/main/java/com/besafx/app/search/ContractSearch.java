package com.besafx.app.search;

import com.besafx.app.entity.Contract;
import com.besafx.app.service.ContractService;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class ContractSearch {

    private static final Logger LOG = LoggerFactory.getLogger(ContractSearch.class);

    @Autowired
    private ContractService contractService;

    public Page<Contract> filter(
            //Contract Filters
            final Integer codeFrom,
            final Integer codeTo,
            final Date dateFrom,
            final Date dateTo,
            //Customer Filters
            final Integer customerCodeFrom,
            final Integer customerCodeTo,
            final Date customerRegisterDateFrom,
            final Date customerRegisterDateTo,
            final String customerName,
            final String customerMobile,
            //Seller Filters
            final Integer sellerCodeFrom,
            final Integer sellerCodeTo,
            final Date sellerRegisterDateFrom,
            final Date sellerRegisterDateTo,
            final String sellerName,
            final String sellerMobile,
            final String filterCompareType,
            Pageable pageRequest
                                ) {
        List<Specification<Contract>> predicates = new ArrayList<>();
        //Contract Specification
        Optional.ofNullable(codeFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(codeTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(dateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("date"), new DateTime
                (value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(dateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("date"), new DateTime(value)
                .plusDays(1).withTimeAtStartOfDay().toDate())));
        //Customer Specification
        Optional.ofNullable(customerCodeFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("customer").get
                ("code"), value)));
        Optional.ofNullable(customerCodeTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("customer").get
                ("code"), value)));
        Optional.ofNullable(customerRegisterDateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get
                ("customer").get("registerDate"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(customerRegisterDateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("customer")
                                                                                                                                 .get("registerDate"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(customerName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("customer").get("contact").get
                ("name"), "%" + value + "%")));
        Optional.ofNullable(customerMobile).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("customer").get("contact").get
                ("mobile"), "%" + value + "%")));
        //Seller Specification
        Optional.ofNullable(sellerCodeFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("seller").get
                ("code"), value)));
        Optional.ofNullable(sellerCodeTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("seller").get("code"),
                                                                                                                   value)));
        Optional.ofNullable(sellerRegisterDateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("seller")
                                                                                                                                    .get("registerDate"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(sellerRegisterDateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("seller").get
                ("registerDate"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(sellerName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("seller").get("contact").get("name"),
                                                                                                    "%" + value + "%")));
        Optional.ofNullable(sellerMobile).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("seller").get("contact").get
                ("mobile"), "%" + value + "%")));

        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                if (filterCompareType == null) {
                    result = Specifications.where(result).and(predicates.get(i));
                    continue;
                }
                result = filterCompareType.equalsIgnoreCase("and") ? Specifications.where(result).and(predicates.get(i)) : Specifications.where
                        (result).or(predicates.get(i));
            }
            return contractService.findAll(result, pageRequest);
        } else {
            return contractService.findAll(pageRequest);
        }
    }
}
