package com.besafx.app.service;

import com.besafx.app.entity.Account;
import com.besafx.app.entity.AccountCondition;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface AccountConditionService extends PagingAndSortingRepository<AccountCondition, Long>, JpaSpecificationExecutor<AccountCondition> {
    List<AccountCondition> findByAccountId(Long id);
    List<AccountCondition> findByAccountIn(List<Account> accounts);
}
