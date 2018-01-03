package com.besafx.app.service;

import com.besafx.app.entity.Account;
import com.besafx.app.entity.AccountNote;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface AccountNoteService extends PagingAndSortingRepository<AccountNote, Long>, JpaSpecificationExecutor<AccountNote> {
    List<AccountNote> findByAccount(Account account);

    List<AccountNote> findByAccountId(Long accountId);
}
