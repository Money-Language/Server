package com.moge.moge.domain.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class BoardDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int checkBoardExists(int boardIdx) {
        String checkBoardExistsQuery = "select exists(select * from Board where boardIdx = ? and status = 'ACTIVE')";
        return this.jdbcTemplate.queryForObject(checkBoardExistsQuery, int.class, boardIdx);
    }

    public int checkBoardLikeExists(int boardIdx) {
        String checkBoardLikeExistsQuery = "select exists(select * from BoardLike where boardIdx =? and status = 'ACTIVE')";
        return this.jdbcTemplate.queryForObject(checkBoardLikeExistsQuery, int.class, boardIdx);
    }

    public int createBoardLike(int boardIdx) {
        String createBoardLikeQuery = "insert into BoardLike(boardIdx) values(?)";
        return this.jdbcTemplate.update(createBoardLikeQuery, boardIdx);
    }

    public int deleteBoardLike(int boardIdx) {
        String deleteBoardLikeQuery = "update BoardLike set status = 'DELETE' where boardIdx =?";
        return this.jdbcTemplate.update(deleteBoardLikeQuery, boardIdx);
    }

    public int checkBoardLikeStatus(int boardIdx) {
        String checkBoardLikeStatus = "select exists(select * from BoardLike where boardIdx = ? and status = 'DELETE')";
        return this.jdbcTemplate.queryForObject(checkBoardLikeStatus, int.class, boardIdx);
    }

    public int updateBoardLikeStatus(int boardIdx) {
        String updateBoardLikeStatus = "update BoardLike set status = 'ACTIVE' where boardIdx = ?";
        return this.jdbcTemplate.update(updateBoardLikeStatus, boardIdx);
    }
}
