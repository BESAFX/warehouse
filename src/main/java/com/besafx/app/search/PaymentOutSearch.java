package com.besafx.app.search;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.PaymentOut;
import com.besafx.app.service.PaymentOutService;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class PaymentOutSearch {

    @Autowired
    private PaymentOutService paymentOutService;

    public List<PaymentOut> search(
            final String paymentCodeFrom,
            final String paymentCodeTo,
            final Long paymentDateFrom,
            final Long paymentDateTo,
            final Long amountFrom,
            final Long amountTo,
            final Long dateFrom,
            final Long dateTo,
            final Long branchId
    ) {
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(paymentCodeFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(paymentCodeTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(paymentDateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("date"), new Date(value))));
        Optional.ofNullable(paymentDateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("date"), new Date(value))));
        Optional.ofNullable(amountFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("amountNumber"), value)));
        Optional.ofNullable(amountTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("amountNumber"), value)));
        Optional.ofNullable(dateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("date"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(dateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("date"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(branchId).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("branch").get("id"), value)));
        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            return Lists.newArrayList(paymentOutService.findAll(result));
        } else {
            throw new CustomException("فضلاً ادخل على الاقل عنصر واحد للبحث");
        }

    }
}
