package com.besafx.app.service;
import com.besafx.app.entity.BillBuyType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface BillBuyTypeService extends PagingAndSortingRepository<BillBuyType, Long>, JpaSpecificationExecutor<BillBuyType> {

    BillBuyType findTopByOrderByCodeDesc();
    BillBuyType findByCodeAndIdIsNot(Integer code, Long id);
}
