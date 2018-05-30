package com.besafx.app.service;

import com.besafx.app.entity.BillPurchasePayment;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public interface BillPurchasePaymentService extends PagingAndSortingRepository<BillPurchasePayment, Long>, JpaSpecificationExecutor<BillPurchasePayment> {

    BillPurchasePayment findTopByOrderByCodeDesc();

    BillPurchasePayment findByCodeAndIdIsNot(Integer code, Long id);

    List<BillPurchasePayment> findByBillPurchaseId(Long id);

    List<BillPurchasePayment> findByDateBetween(@Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
}
