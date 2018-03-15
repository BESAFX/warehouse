package com.besafx.app.search;

import com.besafx.app.entity.Account;
import com.besafx.app.service.AccountService;
import com.google.common.collect.Lists;
import org.hibernate.criterion.SimpleProjection;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class AccountSearch {

    private final static Logger log = LoggerFactory.getLogger(AccountSearch.class);

    @Autowired
    private AccountService accountService;

    public Page<Account> search(
            final String firstName,
            final String secondName,
            final String thirdName,
            final String forthName,
            final String fullName,
            final Long dateFrom,
            final Long dateTo,
            final String studentIdentityNumber,
            final String studentMobile,
            final Long coursePriceFrom,
            final Long coursePriceTo,
            final List<Long> courseIds,
            final List<Long> masterIds,
            final List<Long> branchIds,
            final List<Integer> courseCodes,
            final List<Integer> masterCodes,
            final List<Integer> branchCodes,
            final String searchType,
            final Pageable pageRequest) {

        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(firstName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("firstName"), "%" + value.trim() + "%")));
        Optional.ofNullable(secondName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("secondName"), "%" + value.trim() + "%")));
        Optional.ofNullable(thirdName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("thirdName"), "%" + value.trim() + "%")));
        Optional.ofNullable(forthName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("forthName"), "%" + value.trim() + "%")));
        Optional.ofNullable(fullName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.<String>get("student").get("contact").get("fullName"), "%" + value.trim() + "%")));
        Optional.ofNullable(dateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("registerDate"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(dateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("registerDate"), new DateTime(value).plusDays(1).withTimeAtStartOfDay())));
        Optional.ofNullable(studentIdentityNumber).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("identityNumber"), "%" + value.trim() + "%")));
        Optional.ofNullable(studentMobile).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("mobile"), "%" + value.trim() + "%")));
        Optional.ofNullable(coursePriceFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("coursePrice"), value)));
        Optional.ofNullable(coursePriceTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("coursePrice"), value)));
        Optional.ofNullable(courseIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("course").get("id").in(value)));
        Optional.ofNullable(masterIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("course").get("master").get("id").in(value)));
        Optional.ofNullable(branchIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("course").get("master").get("branch").get("id").in(value)));
        Optional.ofNullable(courseCodes).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("course").get("code").in(value)));
        Optional.ofNullable(masterCodes).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("course").get("master").get("code").in(value)));
        Optional.ofNullable(branchCodes).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("course").get("master").get("branch").get("code").in(value)));
        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = searchType.equals("and") ? Specifications.where(result).and(predicates.get(i)) : Specifications.where(result).or(predicates.get(i));
            }
            return accountService.findAll(result, pageRequest);
        } else {
            return new PageImpl<>(new ArrayList<>());
        }
    }
}
