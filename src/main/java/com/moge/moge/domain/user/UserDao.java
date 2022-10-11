package com.moge.moge.domain.user;

import com.moge.moge.domain.user.model.User;
import com.moge.moge.domain.user.model.req.PostLoginReq;
import com.moge.moge.domain.user.model.req.PostUserReq;
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

        String createUserTermsQuery = "insert into Terms(contract1, contract2, contract3, contract4, userIdx) values(?,?,?,?,?)";
        Object[] createUserTermsParams = new Object[] {
                postUserReq.getContract1(),
                postUserReq.getContract2(),
                postUserReq.getContract3(),
                postUserReq.getContract4(),
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

    public User getPwd(PostLoginReq postLoginReq){
        String getPwdQuery = "select userIdx, email, nickname, password, profileImage from User where email = ?";
        String getPwdParams = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new User(
                        rs.getInt("userIdx"),
                        rs.getString("email"),
                        rs.getString("nickname"),
                        rs.getString("password"),
                        rs.getString("profileImage")
                ),
                getPwdParams
        );
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
}
