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

    Payment findByCode(Integer code);
    Payment findByCodeAndAccountCourseMasterBranch(Integer code, Branch branch);
    @Query("select sum(amountNumber) from Payment p where (p.account.id) = (:accountId) ")
    Double findPaidPriceByAccount(@Param("accountId") Long accountId);
    List<Payment> findByAccount(Account account);
    List<Payment> findByAccountAndType(Account account, String type);
    List<Payment> findByAccountCourseMasterBranch(Branch branch);
    List<Payment> findByAccountCourseMaster(Master master);
    List<Payment> findByAccountCourse(Course course);
    List<Payment> findByAccountIn(List<Account> accounts);
    List<Payment> findByAccountCourseMasterBranchAndDateBetween(Branch branch, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);
    List<Payment> findByAccountCourseMasterAndDateBetween(Master master, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);
    List<Payment> findByAccountCourseAndDateBetween(Course course, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);
    List<Payment> findByAccountInAndDateBetween(List<Account> accounts, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);

}
