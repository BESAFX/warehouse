package com.besafx.app.search;

import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Account;
import com.besafx.app.service.AccountService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AccountSearch {

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
                                Long courseId,
                                Long masterId,
                                Long branchId) {
        return search(firstName, secondName, thirdName, forthName, dateFrom, dateTo, studentIdentityNumber, studentMobile, coursePriceFrom, coursePriceTo, courseId, masterId, branchId, null, null, null);
    }

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
                                Integer courseCode,
                                Integer masterCode,
                                Integer branchCode) {
        return search(firstName, secondName, thirdName, forthName, dateFrom, dateTo, studentIdentityNumber, studentMobile, coursePriceFrom, coursePriceTo, null, null, null, courseCode, masterCode, branchCode);
    }


    private List<Account> search(String firstName,
                                String secondName,
                                String thirdName,
                                String forthName,
                                Long dateFrom,
                                Long dateTo,
                                String studentIdentityNumber,
                                String studentMobile,
                                Long coursePriceFrom,
                                Long coursePriceTo,
                                Long courseId,
                                Long masterId,
                                Long branchId,
                                Integer courseCode,
                                Integer masterCode,
                                Integer branchCode) {

        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(firstName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("firstName"), "%" + value + "%")));
        Optional.ofNullable(secondName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("secondName"), "%" + value + "%")));
        Optional.ofNullable(thirdName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("thirdName"), "%" + value + "%")));
        Optional.ofNullable(forthName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("forthName"), "%" + value + "%")));
        Optional.ofNullable(dateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("registerDate"), new Date(value))));
        Optional.ofNullable(dateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("registerDate"), new Date(value))));
        Optional.ofNullable(studentIdentityNumber).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("identityNumber"), "%" + value + "%")));
        Optional.ofNullable(studentMobile).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("mobile"), "%" + value + "%")));
        Optional.ofNullable(coursePriceFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("coursePrice"), value)));
        Optional.ofNullable(coursePriceTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("coursePrice"), value)));
        Optional.ofNullable(courseId).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("course").get("id"), value)));
        Optional.ofNullable(masterId).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("course").get("master").get("id"), value)));
        Optional.ofNullable(branchId).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("course").get("master").get("branch").get("id"), value)));
        Optional.ofNullable(courseCode).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("course").get("code"), value)));
        Optional.ofNullable(masterCode).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("course").get("master").get("code"), value)));
        Optional.ofNullable(branchCode).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("course").get("master").get("branch").get("code"), value)));
        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            List<Account> list = Lists.newArrayList(accountService.findAll(result));
            Comparator<Account> comparator = Comparator.comparing(account -> account.getCourse().getId());
            list.sort(comparator);
            return list;
        } else {
            throw new CustomException("فضلاً ادخل على الاقل عنصر واحد للبحث");
        }
    }
}
