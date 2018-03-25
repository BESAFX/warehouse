package com.besafx.app.service;

import com.besafx.app.entity.Branch;
import com.besafx.app.entity.Master;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface MasterService extends PagingAndSortingRepository<Master, Long>, JpaSpecificationExecutor<Master> {

    Master findTopByBranchOrderByCodeDesc(Branch branch);

    Master findByCodeAndBranchAndIdIsNot(Integer code, Branch branch, Long id);

    List<Master> findByBranch(Branch branch);

    List<Master> findByBranchIdIn(List<Long> branchIds);

    Master findByCodeAndBranch(Integer Code, Branch branch);

    Master findByCodeAndBranchCode(Integer Code, Integer branchCode);

    Master findByNameAndBranch(String name, Branch branch);
}
