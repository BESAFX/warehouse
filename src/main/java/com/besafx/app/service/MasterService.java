package com.besafx.app.service;
import com.besafx.app.entity.Branch;
import com.besafx.app.entity.Company;
import com.besafx.app.entity.Master;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface MasterService extends PagingAndSortingRepository<Master, Long>, JpaSpecificationExecutor<Master> {

    @Query("select max(code) from Master m where (m.branch.id) = (:id)")
    Integer findLastCodeByBranch(@Param("id") Long id);
    Master findByCodeAndBranchAndIdIsNot(Integer code, Branch branch, Long id);
    List<Master> findByBranch(Branch branch);
    List<Master> findByBranchCompany(Company company);
    Master findByCodeAndBranch(Integer Code, Branch branch);
    Master findByNameAndBranch(String name, Branch branch);
    Long countByBranchCompany(Company company);
    Long countByBranch(Branch branch);
}
