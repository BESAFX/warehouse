package com.besafx.app.service;
import com.besafx.app.entity.Branch;
import com.besafx.app.entity.Course;
import com.besafx.app.entity.Master;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface CourseService extends PagingAndSortingRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    Course findTopByMasterOrderByCodeDesc(Master master);
    Course findByCodeAndMasterAndIdIsNot(Integer code, Master master, Long id);
    Course findByCodeAndMasterCodeAndMasterBranch(Integer courseCode, Integer masterCode, Branch branch);
    Long countByMaster(Master master);
    Long countByMasterBranch(Branch branch);
    List<Course> findByMaster(Master master);

}
