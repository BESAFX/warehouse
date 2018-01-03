package com.besafx.app.search;

import com.besafx.app.entity.Bank;
import com.besafx.app.service.BankService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BankSearch {

    @Autowired
    private BankService bankService;

    public List<Bank> search(
            final Long code,
            final String name,
            final String branchName,
            final Long stockFrom,
            final Long stockTo,
            final Long branchId
    ) {
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(code).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("code"), "%" + value + "%")));
        Optional.ofNullable(name).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("name"), "%" + value + "%")));
        Optional.ofNullable(branchName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("branchName"), "%" + value + "%")));
        Optional.ofNullable(stockFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("stock"), value)));
        Optional.ofNullable(stockTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("stock"), value)));
        Optional.ofNullable(branchId).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("branch").get("id"), value)));
        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            return Lists.newArrayList(bankService.findAll(result));
        } else {
            return Lists.newArrayList(bankService.findAll());
        }
    }
}
