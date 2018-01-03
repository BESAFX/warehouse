package com.besafx.app.search;

import com.besafx.app.entity.Course;
import com.besafx.app.service.CourseService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CourseSearch {

    @Autowired
    private CourseService courseService;

    public List<Course> search(
            final String instructor,
            final Long codeFrom,
            final Long codeTo,
            final Long branchId,
            final Long masterId
    ) {
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(instructor).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("instructor"), "%" + value + "%")));
        Optional.ofNullable(codeFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(codeTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(branchId).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("master").get("branch").get("id"), value)));
        Optional.ofNullable(masterId).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("master").get("id"), value)));
        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            return Lists.newArrayList(courseService.findAll(result));
        } else {
            return Lists.newArrayList(courseService.findAll());
        }
    }
}
