package com.besafx.app.service;
import com.besafx.app.entity.Student;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface StudentService extends PagingAndSortingRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    @Query("select max(code) from Student")
    Integer findMaxCode();
    Student findByCode(Integer code);
    Student findByContactIdentityNumber(String identityNumber);
}
