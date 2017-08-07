package com.besafx.app.service;

import com.besafx.app.entity.Call;
import com.besafx.app.entity.Offer;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface CallService extends PagingAndSortingRepository<Call, Long>, JpaSpecificationExecutor<Call> {
    List<Call> findByOffer(Offer offer);

    List<Call> findByOfferId(Long offerId);
}
