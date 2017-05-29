package com.besafx.app.repository;

import com.besafx.app.entity.Account;
import com.besafx.app.entity.Payment;
import com.besafx.app.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PaymentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AccountService accountService;

    public List<Payment> findByAccount(Long accountId) {
        Account account = accountService.findOne(accountId);
        String SQL = "select payment.id, payment.code, payment.date, payment.amount_number, payment.amount_string, payment.note from payment where account = ?";
        RowMapper<Payment> rowMapper = new RowMapper<Payment>() {
            @Override
            public Payment mapRow(ResultSet rs, int index) throws SQLException {
                Payment payment = new Payment();
                payment.setId(rs.getLong(1));
                payment.setCode(rs.getInt(2));
                payment.setDate(rs.getDate(3));
                payment.setAmountNumber(rs.getDouble(4));
                payment.setAmountString(rs.getString(5));
                payment.setNote(rs.getString(6));
                payment.setAccount(account);
                return payment;
            }
        };
        return jdbcTemplate.query(SQL, rowMapper, accountId);
    }
}
