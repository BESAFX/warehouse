package com.besafx.app.service;

import com.besafx.app.entity.BillBuy;
import com.besafx.app.entity.Branch;
import com.google.common.collect.Lists;
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
public interface BillBuyService extends PagingAndSortingRepository<BillBuy, Long>, JpaSpecificationExecutor<BillBuy> {
    BillBuy findByCode(Long code);
    List<BillBuy> findByBranchAndDateBetween(Branch branch, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
}
