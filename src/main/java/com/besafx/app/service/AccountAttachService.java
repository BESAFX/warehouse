package com.besafx.app.service;
import com.besafx.app.entity.Account;
import com.besafx.app.entity.AccountAttach;
import com.besafx.app.entity.Payment;
import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface AccountAttachService extends PagingAndSortingRepository<AccountAttach, Long>, JpaSpecificationExecutor<AccountAttach> {
    List<AccountAttach> findByAccountId(Long id);
    List<AccountAttach> findByAccountIn(List<Account> accounts);
}
