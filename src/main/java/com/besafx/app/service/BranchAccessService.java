package com.besafx.app.service;

import com.besafx.app.entity.BranchAccess;
import com.besafx.app.entity.Person;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface BranchAccessService extends PagingAndSortingRepository<BranchAccess, Long>, JpaSpecificationExecutor<BranchAccess> {
    List<BranchAccess> findByPerson(Person person);
    List<BranchAccess> findByPersonId(Long personId);
}
