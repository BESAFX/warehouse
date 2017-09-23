package com.besafx.app.service;

import com.besafx.app.entity.Branch;
import com.besafx.app.entity.Payment;
import com.besafx.app.entity.PaymentOut;
import com.besafx.app.entity.Person;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public interface PaymentOutService extends PagingAndSortingRepository<PaymentOut, Long>, JpaSpecificationExecutor<PaymentOut> {
    PaymentOut findByCode(Long code);
    Payment findByCodeAndBranch(Long code, Branch branch);

    List<PaymentOut> findByBranch(Branch branch);
    List<PaymentOut> findByBranchId(Long branchId);
    List<PaymentOut> findByBranchIn(List<Branch> branches);
    List<PaymentOut> findByBranchIdIn(List<Long> branchIds);

    List<PaymentOut> findByBranchAndDateBetween(Branch branch, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<PaymentOut> findByBranchIdAndDateBetween(Long branchId, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<PaymentOut> findByBranchInAndDateBetween(List<Branch> branches, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<PaymentOut> findByBranchIdInAndDateBetween(List<Long> branchIds, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);

    List<PaymentOut> findByPerson(Person person);
    List<PaymentOut> findByPersonId(Long personId);
    List<PaymentOut> findByPersonIn(List<Person> persons);
    List<PaymentOut> findByPersonIdIn(List<Long> personIds);

    List<PaymentOut> findByPersonAndDateBetween(Person person, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<PaymentOut> findByPersonIdAndDateBetween(Long personId, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<PaymentOut> findByPersonInAndDateBetween(List<Person> persons, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<PaymentOut> findByPersonIdInAndDateBetween(List<Long> personIds, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
}
