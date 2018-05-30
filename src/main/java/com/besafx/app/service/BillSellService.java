package com.besafx.app.service;

import com.besafx.app.entity.BillSell;
import com.besafx.app.entity.Customer;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface BillSellService extends PagingAndSortingRepository<BillSell, Long>, JpaSpecificationExecutor<BillSell> {

    BillSell findTopByOrderByCodeDesc();

    BillSell findByCode(Long code);

    BillSell findByCodeAndIdIsNot(Long code, Long id);

    List<BillSell> findByCustomer(Customer customer);
}
