package com.besafx.app.service;

import com.besafx.app.entity.BillPurchase;
import com.besafx.app.entity.Supplier;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface BillPurchaseService extends PagingAndSortingRepository<BillPurchase, Long>, JpaSpecificationExecutor<BillPurchase> {

    BillPurchase findTopByOrderByCodeDesc();

    BillPurchase findByCode(Long code);

    BillPurchase findByCodeAndIdIsNot(Long code, Long id);

    List<BillPurchase> findBySupplier(Supplier supplier);
}
