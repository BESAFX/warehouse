package com.besafx.app.service;

import com.besafx.app.entity.ContractPayment;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface ContractPaymentService extends PagingAndSortingRepository<ContractPayment, Long>, JpaSpecificationExecutor<ContractPayment> {

    ContractPayment findTopByOrderByCodeDesc();

    ContractPayment findByCodeAndIdIsNot(Integer code, Long id);

    List<ContractPayment> findByContractId(Long id);
}
