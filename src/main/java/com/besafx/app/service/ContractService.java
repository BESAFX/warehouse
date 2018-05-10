package com.besafx.app.service;

import com.besafx.app.entity.Contract;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface ContractService extends PagingAndSortingRepository<Contract, Long>, JpaSpecificationExecutor<Contract> {

    Contract findTopByOrderByCodeDesc();

    Contract findByCodeAndIdIsNot(Long code, Long id);
}
