package com.besafx.app.service;

import com.besafx.app.entity.BillSell;
import com.besafx.app.entity.BillSellAttach;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface BillSellAttachService extends PagingAndSortingRepository<BillSellAttach, Long>, JpaSpecificationExecutor<BillSellAttach> {
    List<BillSellAttach> findByBillSell(BillSell id);
    List<BillSellAttach> findByBillSellId(Long id);
}
