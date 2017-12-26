package com.besafx.app.service;
import com.besafx.app.entity.*;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public interface PaymentService extends PagingAndSortingRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

    Payment findTopByAccountCourseMasterBranchOrderByCodeDesc(Branch branch);
    Payment findByCode(Long code);
    Payment findByCodeAndAccountCourseMasterBranch(Long code, Branch branch);
    Payment findByCodeAndAccountCourseMasterBranchAndIdNot(Long code, Branch branch, Long paymentId);
    Payment findByCodeAndLastPersonBranch(Long code, Branch branch);
    List<Payment> findByAccount(Account account);
    List<Payment> findByAccountId(Long account);
    List<Payment> findByAccountAndType(Account account, String type);
    List<Payment> findByAccountCourseMasterBranch(Branch branch);
    List<Payment> findByAccountCourseMasterBranchId(Long branch);
    List<Payment> findByAccountCourseMaster(Master master);
    List<Payment> findByAccountCourse(Course course);
    List<Payment> findByAccountIn(List<Account> accounts);
    List<Payment> findByAccountCourseMasterBranchAndDateBetween(Branch branch, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<Payment> findByAccountCourseMasterAndDateBetween(Master master, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<Payment> findByAccountCourseAndDateBetween(Course course, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<Payment> findByAccountInAndDateBetween(List<Account> accounts, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    @Query("select sum(p.amountNumber) from Payment p where p.account = (:account) and p.type = (:type) ")
    Double sumByAccountAndType(@Param(value = "account") Account account, @Param(value = "type") String type);

}
