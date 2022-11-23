package com.moge.moge.domain.mail.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class MailDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int insertCertifiedCode(String email, String code) {
        String checkQuery = "select exists(select * from Certification where email =?)";
        String insertQuery = "insert into Certification(email, code) values (?,?)";
        String updateQuery = "update Certification set code =? where email =?";

        if (this.jdbcTemplate.queryForObject(checkQuery, int.class, email) == 0) {
            return this.jdbcTemplate.update(insertQuery, email, code);
        }
        return this.jdbcTemplate.update(updateQuery, code, email);
    }

    public int checkCertifiedEmail(String email) {
        String checkQuery = "select exists(select * from Certification where email = ?)";
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, email);
    }

    public int checkCertifiedTime(String email) {
        String checkQuery = "select TIMESTAMPDIFF(second, updatedAt, CURRENT_TIMESTAMP()) from Certification where email =?";
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, email);
    }

    public boolean checkCertifiedCode(String email, String code) {
        String checkQuery = "select exists(select * from Certification where email =? and code =?)";
        if (this.jdbcTemplate.queryForObject(checkQuery, int.class, email, code) == 0) {
            return false;
        } else {
            return true;
        }
    }


}
