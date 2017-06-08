package com.besafx.app.service;
import com.besafx.app.entity.*;
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
public interface AccountService extends PagingAndSortingRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    Account findTopByCourseMasterBranchOrderByCodeDesc(Branch branch);
    List<Account> findByStudent(Student student);
    List<Account> findByStudentContactIdentityNumber(String identityNumber);
    List<Account> findByCourse(Course course);
    List<Account> findByCourseAndRegisterDateBetween(Course course, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<Account> findByCourseMaster(Master master);
    List<Account> findByCourseMasterAndRegisterDateBetween(Master master, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<Account> findByCourseMasterBranch(Branch branch);
    List<Account> findByCourseMasterBranchAndRegisterDateBetween(Branch branch, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    Long countByCourse(Course course);
    Long countByCourseAndRegisterDateBetween(Course course, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    Long countByCourseMasterAndRegisterDateBetween(Master master, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    Long countByCourseMasterBranchAndRegisterDateBetween(Branch branch, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
}
