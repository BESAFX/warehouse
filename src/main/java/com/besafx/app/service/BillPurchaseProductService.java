package com.besafx.app.service;

import com.besafx.app.entity.BillPurchaseProduct;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface BillPurchaseProductService extends PagingAndSortingRepository<BillPurchaseProduct, Long>, JpaSpecificationExecutor<BillPurchaseProduct> {
    List<BillPurchaseProduct> findByBillPurchaseId(Long id);
}
