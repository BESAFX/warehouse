package com.besafx.app.search;
import com.besafx.app.entity.Deposit;
import com.besafx.app.service.DepositService;
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
public class DepositSearch {

    @Autowired
    private DepositService depositService;

    public List<Deposit> search(
            final Long code,
            final Long amountFrom,
            final Long amountTo,
            final String fromName,
            final Long dateFrom,
            final Long dateTo,

            final Long bankCode,
            final String bankName,
            final Long bankBranch,
            final String bankBranchName,
            final Long bankStockFrom,
            final Long bankStockTo
    ) {
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(code).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("code"), "%" + value + "%")));
        Optional.ofNullable(amountFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("amount"), value)));
        Optional.ofNullable(amountTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("amount"), value)));
        Optional.ofNullable(fromName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("fromName"), "%" + value + "%")));
        Optional.ofNullable(dateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("date"), new Date(value))));
        Optional.ofNullable(dateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("date"), new Date(value))));
        Optional.ofNullable(bankCode).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("bank").get("code"), "%" + value + "%")));
        Optional.ofNullable(bankName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("bank").get("name"), "%" + value + "%")));
        Optional.ofNullable(bankBranch).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("bank").get("branch").get("id"), value)));
        Optional.ofNullable(bankBranchName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("bank").get("branchName"), "%" + value + "%")));
        Optional.ofNullable(bankStockFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("bank").get("stock"), value)));
        Optional.ofNullable(bankStockTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("bank").get("stock"), value)));
        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            return Lists.newArrayList(depositService.findAll(result));
        } else {
            return Lists.newArrayList(depositService.findAll());
        }
    }

}
