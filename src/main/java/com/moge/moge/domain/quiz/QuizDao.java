package com.moge.moge.domain.quiz;

import com.moge.moge.domain.quiz.dto.req.PostBoardReq;
import com.moge.moge.domain.quiz.dto.res.PostBoardRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class QuizDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public PostBoardRes createBoard(int userIdx, PostBoardReq postBoardReq) {
        String createBoardQuery = "insert into Board(title, userIdx, categoryIdx) values(?, ?, ?)";
        Object[] createBoardParams = new Object[] {
                postBoardReq.getTitle(),
                userIdx,
                postBoardReq.getCategoryIdx()
        };
        this.jdbcTemplate.update(createBoardQuery, createBoardParams);
        String lastInsertIdQuery = "select last_insert_id()";
        int boardIdx = this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
        return new PostBoardRes(boardIdx, postBoardReq.getCategoryIdx(), userIdx, postBoardReq.getTitle());
    }



}
