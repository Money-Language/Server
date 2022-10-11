package com.moge.moge.domain.user;

import com.moge.moge.domain.user.model.PostUserReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createUser(PostUserReq postUserReq) {
        String createUserQuery = "insert into User(email, nickname, password) values(?,?,?)";
        Object[] createUserParams = new Object[] {
                postUserReq.getEmail(),
                postUserReq.getNickname(),
                postUserReq.getPassword()
        };
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInsertIdQuery = "select last_insert_id()";

        String createUserTermsQuery = "insert into Terms(contract1, contract2, contract3, userIdx) values(?,?,?,?)";
        Object[] createUserTermsParams = new Object[] {
                postUserReq.getContract1(),
                postUserReq.getContract2(),
                postUserReq.getContract3(),
                this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class)
        };
        return this.jdbcTemplate.update(createUserTermsQuery, createUserTermsParams);
    }
    

    public int checkEmail(String email) {
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery, int.class, checkEmailParams);
    }

    public int checkNickname(String nickname) {
        String checkNicknameQuery = "select exists(select nickname from User where nickname = ?)";
        String checkNicknameParams = nickname;
        return this.jdbcTemplate.queryForObject(checkNicknameQuery,
                int.class,
                checkNicknameParams);
    }


}
