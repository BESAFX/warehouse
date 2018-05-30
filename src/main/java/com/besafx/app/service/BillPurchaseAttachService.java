package com.besafx.app.service;

import com.besafx.app.entity.BillPurchase;
import com.besafx.app.entity.BillPurchaseAttach;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface BillPurchaseAttachService extends PagingAndSortingRepository<BillPurchaseAttach, Long>, JpaSpecificationExecutor<BillPurchaseAttach> {
    List<BillPurchaseAttach> findByBillPurchase(BillPurchase id);
    List<BillPurchaseAttach> findByBillPurchaseId(Long id);
}
