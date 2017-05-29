package com.besafx.app.repository;

import com.besafx.app.entity.Student;
import com.besafx.app.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StudentRepository {

    @Autowired
    ContactService contactService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Student> findUnRegistered(Long branchId, Long masterId, Long courseId) {
        String SQL = "select student.id, student.code, student.contact from student left join account on student.id = account.student and account.branch = ? and account.master = ? and account.course = ? where account.id is null";
        RowMapper<Student> rowMapper = new RowMapper<Student>() {
            @Override
            public Student mapRow(ResultSet rs, int index) throws SQLException {
                Student student = new Student();
                student.setId(rs.getLong(1));
                student.setCode(rs.getInt(2));
                student.setContact(contactService.findOne(rs.getLong(3)));
                return student;
            }
        };
        return jdbcTemplate.query(SQL, rowMapper, branchId, masterId, courseId);
    }
}
