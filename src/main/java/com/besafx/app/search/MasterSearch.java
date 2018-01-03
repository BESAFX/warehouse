package com.besafx.app.search;

import com.besafx.app.entity.Master;
import com.besafx.app.service.MasterService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MasterSearch {

    @Autowired
    private MasterService masterService;

    public List<Master> search(
            final String name,
            final Long codeFrom,
            final Long codeTo,
            final Long branchId
    ) {
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(name).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("name"), "%" + value + "%")));
        Optional.ofNullable(codeFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(codeTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(branchId).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("branch").get("id"), value)));
        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            return Lists.newArrayList(masterService.findAll(result));
        } else {
            return Lists.newArrayList(masterService.findAll());
        }
    }
}
