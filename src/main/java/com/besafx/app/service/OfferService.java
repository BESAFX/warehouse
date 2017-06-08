package com.besafx.app.service;
import com.besafx.app.entity.Branch;
import com.besafx.app.entity.Master;
import com.besafx.app.entity.Offer;
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
public interface OfferService extends PagingAndSortingRepository<Offer, Long>, JpaSpecificationExecutor<Offer> {

    Offer findTopByMasterBranchOrderByCodeDesc(Branch branch);
    Offer findByCodeAndMasterBranchAndIdIsNot(Integer code, Branch branch, Long id);
    List<Offer> findByLastUpdateBetween(@Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<Offer> findByMasterBranch(Branch branch);
    List<Offer> findByMasterBranchIn(List<Branch> branches);
    List<Offer> findByMasterBranchAndLastUpdateBetween(Branch branch, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<Offer> findByMasterBranchInAndLastUpdateBetween(List<Branch> branches, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<Offer> findByMaster(Master master);
    List<Offer> findByMasterIn(List<Master> masters);
    List<Offer> findByMasterAndLastUpdateBetween(Master master, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<Offer> findByMasterInAndLastUpdateBetween(List<Master> masters, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<Offer> findByLastPerson(Person person);
    List<Offer> findByLastPersonAndLastUpdateBetween(Person person, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    Long countByMasterBranch(Branch branch);
    Long countByMasterBranchAndLastUpdateBetween(Branch branch, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    Long countByMasterBranchIn(List<Branch> branches);
    Long countByMasterBranchInAndLastUpdateBetween(List<Branch> branches, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    Long countByMaster(Master master);
    Long countByMasterAndLastUpdateBetween(Master master, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    Long countByMasterIn(List<Master> masters);
    Long countByMasterInAndLastUpdateBetween(List<Master> masters, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
}
