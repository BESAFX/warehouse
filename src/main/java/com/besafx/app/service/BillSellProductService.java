package com.besafx.app.service;

import com.besafx.app.entity.BillSellProduct;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface BillSellProductService extends PagingAndSortingRepository<BillSellProduct, Long>, JpaSpecificationExecutor<BillSellProduct> {
    List<BillSellProduct> findByBillSellId(Long id);
}
