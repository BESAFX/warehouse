package com.besafx.app.service;

import com.besafx.app.entity.BillSellPayment;
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
public interface BillSellPaymentService extends PagingAndSortingRepository<BillSellPayment, Long>, JpaSpecificationExecutor<BillSellPayment> {

    BillSellPayment findTopByOrderByCodeDesc();

    BillSellPayment findByCodeAndIdIsNot(Integer code, Long id);

    List<BillSellPayment> findByBillSellId(Long id);

    List<BillSellPayment> findByDateBetween(@Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
}
