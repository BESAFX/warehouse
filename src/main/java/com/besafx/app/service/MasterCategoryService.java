package com.besafx.app.service;

import com.besafx.app.entity.MasterCategory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface MasterCategoryService extends PagingAndSortingRepository<MasterCategory, Long>, JpaSpecificationExecutor<MasterCategory> {
    MasterCategory findTopByOrderByCodeDesc();

    MasterCategory findByCode(Integer code);

    MasterCategory findByCodeAndIdIsNot(Integer code, Long id);
}
