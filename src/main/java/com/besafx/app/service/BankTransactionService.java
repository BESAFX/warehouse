package com.besafx.app.service;

import com.besafx.app.entity.BankTransaction;
import com.besafx.app.entity.Seller;
import com.besafx.app.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface BankTransactionService extends PagingAndSortingRepository<BankTransaction, Long>, JpaSpecificationExecutor<BankTransaction> {
    List<BankTransaction> findBySeller(Seller seller);

    <T> List<T> findByTransactionTypeIn(List<TransactionType> transactionTypes, Class<T> type);

    <T> List<T> findBySellerAndTransactionTypeIn(Seller seller, List<TransactionType> transactionTypes, Class<T> type);
}
