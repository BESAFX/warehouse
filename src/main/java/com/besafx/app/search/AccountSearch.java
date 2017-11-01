package com.besafx.app.search;

import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Account;
import com.besafx.app.service.AccountService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AccountSearch {

    private final static Logger log = LoggerFactory.getLogger(AccountSearch.class);

    @Autowired
    private AccountService accountService;

    public List<Account> search(String firstName,
                                String secondName,
                                String thirdName,
                                String forthName,
                                Long dateFrom,
                                Long dateTo,
                                String studentIdentityNumber,
                                String studentMobile,
                                Long coursePriceFrom,
                                Long coursePriceTo,
                                List<Long> courseIds,
                                List<Long> masterIds,
                                List<Long> branchIds,
                                List<Integer> courseCodes,
                                List<Integer> masterCodes,
                                List<Integer> branchCodes) {

        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(firstName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("firstName"), "%" + value.trim() + "%")));
        Optional.ofNullable(secondName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("secondName"), "%" + value.trim() + "%")));
        Optional.ofNullable(thirdName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("thirdName"), "%" + value.trim() + "%")));
        Optional.ofNullable(forthName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("forthName"), "%" + value.trim() + "%")));
        Optional.ofNullable(dateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("registerDate"), new Date(value))));
        Optional.ofNullable(dateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("registerDate"), new Date(value))));
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
                result = Specifications.where(result).and(predicates.get(i));
            }
            List<Account> list = Lists.newArrayList(accountService.findAll(result));
            list.sort(Comparator.comparing(account -> account.getCourse().getCode()));
            return list;
        } else {
            return new ArrayList<>();
        }
    }
}
