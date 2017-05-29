package com.besafx.app.search;

import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Payment;
import com.besafx.app.service.PaymentService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class PaymentSearch {

    @Autowired
    private PaymentService paymentService;

    public List<Payment> search(
            final String paymentCodeFrom,
            final String paymentCodeTo,
            final Long paymentDateFrom,
            final Long paymentDateTo,
            final Long amountFrom,
            final Long amountTo,
            final String firstName,
            final String secondName,
            final String thirdName,
            final String forthName,
            final Long dateFrom,
            final Long dateTo,
            final String studentIdentityNumber,
            final String studentMobile,
            final Long coursePriceFrom,
            final Long coursePriceTo,
            final Long course,
            final Long master,
            final Long branch,
            final String type
    ) {

        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(paymentCodeFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(paymentCodeTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(paymentDateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("date"), new Date(value))));
        Optional.ofNullable(paymentDateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("date"), new Date(value))));
        Optional.ofNullable(amountFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("amountNumber"), value)));
        Optional.ofNullable(amountTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("amountNumber"), value)));

        Optional.ofNullable(firstName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("account").get("student").get("contact").get("firstName"), "%" + value + "%")));
        Optional.ofNullable(secondName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("account").get("student").get("contact").get("secondName"), "%" + value + "%")));
        Optional.ofNullable(thirdName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("account").get("student").get("contact").get("thirdName"), "%" + value + "%")));
        Optional.ofNullable(forthName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("account").get("student").get("contact").get("forthName"), "%" + value + "%")));
        Optional.ofNullable(dateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("account").get("registerDate"), new Date(value))));
        Optional.ofNullable(dateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("account").get("registerDate"), new Date(value))));
        Optional.ofNullable(studentIdentityNumber).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("account").get("student").get("contact").get("identityNumber"), "%" + value + "%")));
        Optional.ofNullable(studentMobile).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("account").get("student").get("contact").get("mobile"), "%" + value + "%")));
        Optional.ofNullable(coursePriceFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("account").get("coursePrice"), value)));
        Optional.ofNullable(coursePriceTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("account").get("coursePrice"), value)));
        Optional.ofNullable(course).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("account").get("course").get("id"), value)));
        Optional.ofNullable(master).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("account").get("course").get("master").get("id"), value)));
        Optional.ofNullable(branch).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("account").get("course").get("master").get("branch").get("id"), value)));
        Optional.ofNullable(type).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("type"), value)));

        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            return Lists.newArrayList(paymentService.findAll(result));
        } else {
            throw new CustomException("فضلاً ادخل على الاقل عنصر واحد للبحث");
        }

    }
}
