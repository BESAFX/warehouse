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
public interface OfferService extends PagingAndSortingRepository<Offer, Long>, JpaSpecificationExecutor<Offer> {

    @Query("select max(code) from Offer o where (o.master.branch.id) = (:id)")
    Integer findLastCodeByMasterBranch(@Param("id") Long id);

    @Query("select max(id) from Offer")
    Integer findMaxId();

    List<Offer> findByMasterBranchCompany(Company companyFX);

    List<Offer> findByMasterBranch(Branch branch);

    List<Offer> findByMasterBranchIn(List<Branch> branches);

    List<Offer> findByMasterBranchAndLastUpdateBetween(Branch branch, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);

    List<Offer> findByMasterBranchInAndLastUpdateBetween(List<Branch> branches, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);

    List<Offer> findByMaster(Master master);

    List<Offer> findByMasterIn(List<Master> masters);

    List<Offer> findByMasterAndLastUpdateBetween(Master master, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);

    List<Offer> findByMasterInAndLastUpdateBetween(List<Master> masters, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);

    List<Offer> findByLastPerson(Person person);

    List<Offer> findByLastPersonAndLastUpdateBetween(Person person, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);

    Long countByMasterBranchCompany(Company company);

    Long countByMasterBranch(Branch branch);

    Long countByMasterBranchAndLastUpdateBetween(Branch branch, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);

    Long countByMasterBranchIn(List<Branch> branches);

    Long countByMasterBranchInAndLastUpdateBetween(List<Branch> branches, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);

    Long countByMaster(Master master);

    Long countByMasterAndLastUpdateBetween(Master master, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);

    Long countByMasterIn(List<Master> masters);

    Long countByMasterInAndLastUpdateBetween(List<Master> masters, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);
}
