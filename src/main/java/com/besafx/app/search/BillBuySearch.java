package com.besafx.app.search;

import com.besafx.app.entity.BillBuy;
import com.besafx.app.service.BillBuyService;
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
public class BillBuySearch {

    @Autowired
    private BillBuyService billBuyService;

    public List<BillBuy> search(
            final String codeFrom,
            final String codeTo,
            final Long dateFrom,
            final Long dateTo,
            final Long amountFrom,
            final Long amountTo,
            final Long branchId
    ) {
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(codeFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(codeTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(dateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("date"), new Date(value))));
        Optional.ofNullable(dateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("date"), new Date(value))));
        Optional.ofNullable(amountFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("amountNumber"), value)));
        Optional.ofNullable(amountTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("amountNumber"), value)));
        Optional.ofNullable(branchId).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("lastPerson").get("branch").get("id"), value)));


        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            return Lists.newArrayList(billBuyService.findAll(result));
        } else {
            return Lists.newArrayList(billBuyService.findAll());
        }
    }
}
