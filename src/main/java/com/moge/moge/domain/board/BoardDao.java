package com.moge.moge.domain.board;

import com.moge.moge.domain.board.model.GetBoardTopLike;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

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

    public int checkBoardLikeExists(int boardIdx, int userIdx) {
        String checkBoardLikeExistsQuery = "select exists(select * from BoardLike where boardIdx =? and userIdx = ? and status = 'ACTIVE')";
        Object[] params = new Object[]{boardIdx, userIdx};
        return this.jdbcTemplate.queryForObject(checkBoardLikeExistsQuery, int.class, params);
    }

    public int createBoardLike(int boardIdx, int userIdx) {
        String createBoardLikeQuery = "insert into BoardLike(boardIdx, userIdx) values(?, ?)";
        Object[] params = new Object[]{boardIdx, userIdx};
        return this.jdbcTemplate.update(createBoardLikeQuery, params);
    }

    public int deleteBoardLike(int boardIdx, int userIdx) {
        String deleteBoardLikeQuery = "update BoardLike set status = 'DELETE' where boardIdx =? and userIdx =?";
        Object[] params = new Object[]{boardIdx, userIdx};
        return this.jdbcTemplate.update(deleteBoardLikeQuery, params);
    }

    public int checkBoardLikeStatus(int boardIdx, int userIdx) {
        String checkBoardLikeStatus = "select exists(select * from BoardLike where boardIdx = ? and userIdx = ? and status = 'DELETE')";
        Object[] params = new Object[]{boardIdx, userIdx};
        return this.jdbcTemplate.queryForObject(checkBoardLikeStatus, int.class, params);
    }

    public int updateBoardLikeStatus(int boardIdx, int userIdx) {
        String updateBoardLikeStatus = "update BoardLike set status = 'ACTIVE' where boardIdx = ? and userIdx =?";
        Object[] params = new Object[]{boardIdx, userIdx};
        return this.jdbcTemplate.update(updateBoardLikeStatus, params);
    }

    public List<GetBoardTopLike> getBoardTopLike() {
        String query =
                "select concat('#', categoryName) as categoryName, \n" +
                "    title, viewCount,\n" +
                "    (select count(*) from BoardLike BL where BL.boardIdx = B.boardIdx) as likeCount,\n" +
                "    (select count(*) from Quiz Q where Q.boardIdx = B.boardIdx) as quizCount\n" +
                "from Board B\n" +
                "    left join Category C on C.categoryIdx = B.categoryIdx\n" +
                "order by likeCount desc limit 10;";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetBoardTopLike(
                        rs.getString("categoryName"),
                        rs.getString("title"),
                        rs.getInt("viewCount"),
                        rs.getInt("likeCount"),
                        rs.getInt("quizCount")
                ));
    }
}
