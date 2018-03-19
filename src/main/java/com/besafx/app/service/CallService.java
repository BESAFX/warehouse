package com.besafx.app.service;

import com.besafx.app.entity.Call;
import com.besafx.app.entity.Offer;
import com.besafx.app.entity.Person;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
public interface CallService extends PagingAndSortingRepository<Call, Long>, JpaSpecificationExecutor<Call> {
    List<Call> findByOffer(Offer offer);

    List<Call> findByOfferId(Long offerId);

    List<Call> findByPersonId(Long personId);

    List<Call> findByPersonIdIn(List<Long> personsId);

    List<Call> findByPersonIdAndDateBetween(Long personId, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);

    List<Call> findByPersonIdInAndDateBetween(List<Long> personsId, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);

    List<Call> findByPerson(Person person);

    List<Call> findByPerson(Person person, Sort sort);

    List<Call> findByPersonIn(List<Person> persons);

    List<Call> findByPersonAndDateBetween(Person person, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);

    List<Call> findByPersonAndDateBetween(Person person, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate, Sort sort);

    List<Call> findByPersonInAndDateBetween(List<Person> persons, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
}
