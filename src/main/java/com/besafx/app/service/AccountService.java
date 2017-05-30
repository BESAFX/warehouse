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

    Account findTopByCourseMasterBranchOrderByStudentCodeDesc(Branch branch);
    List<Account> findByStudent(Student student);
    List<Account> findByCourse(Course course);
    List<Account> findByCourseAndRegisterDateBetween(Course course, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);
    List<Account> findByCourseMaster(Master master);
    List<Account> findByCourseMasterAndRegisterDateBetween(Master master, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);
    List<Account> findByCourseMasterBranch(Branch branch);
    List<Account> findByCourseMasterBranchAndRegisterDateBetween(Branch branch, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);
    List<Account> findByCourseMasterBranchCompany(Company company);
    Long countByCourse(Course course);
    Long countByCourseAndRegisterDateBetween(Course course, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);
    Long countByCourseMasterAndRegisterDateBetween(Master master, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);
    Long countByCourseMasterBranch(Branch branch);
    Long countByCourseMasterBranchAndRegisterDateBetween(Branch branch, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);
    Long countByCourseMasterBranchCompany(Company company);
}
