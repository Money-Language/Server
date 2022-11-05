package com.moge.moge.domain.board;

import com.moge.moge.domain.board.model.res.GetBoardCommentRes;
import com.moge.moge.domain.board.model.res.GetBoardTopRes;
import com.moge.moge.domain.board.model.req.PatchBoardCommentReq;
import com.moge.moge.domain.board.model.req.PostBoardCommentReq;
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

    public List<GetBoardTopRes> getBoardTopLike() {
        String query =
                "select concat('#', categoryName) as categoryName, \n" +
                "    title, viewCount,\n" +
                "    (select count(*) from BoardLike BL where BL.boardIdx = B.boardIdx) as likeCount,\n" +
                "    (select count(*) from Quiz Q where Q.boardIdx = B.boardIdx) as quizCount\n" +
                "from Board B\n" +
                "    left join Category C on C.categoryIdx = B.categoryIdx\n" +
                "order by likeCount desc limit 10;";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetBoardTopRes(
                        rs.getString("categoryName"),
                        rs.getString("title"),
                        rs.getInt("viewCount"),
                        rs.getInt("likeCount"),
                        rs.getInt("quizCount")
                ));
    }

    public List<GetBoardTopRes> getBoardTopView() {
        String getBoardTopViewquery =
                "select concat('#', categoryName) as categoryName, \n" +
                "    title, viewCount,\n" +
                "    (select count(*) from BoardLike BL where BL.boardIdx = B.boardIdx) as likeCount,\n" +
                "    (select count(*) from Quiz Q where Q.boardIdx = B.boardIdx) as quizCount\n" +
                "from Board B\n" +
                "    left join Category C on C.categoryIdx = B.categoryIdx\n" +
                "order by viewCount desc limit 10;";
        return this.jdbcTemplate.query(getBoardTopViewquery,
                (rs, rowNum) -> new GetBoardTopRes(
                        rs.getString("categoryName"),
                        rs.getString("title"),
                        rs.getInt("viewCount"),
                        rs.getInt("likeCount"),
                        rs.getInt("quizCount")
                ));
    }

    public int createBoardComment(PostBoardCommentReq postBoardCommentReq, int boardIdx, int userIdx) {
        String createBoardCommentQuery = "insert into Comment(content, groupIdx, parentIdx, boardIdx, userIdx) values(?,?,?,?,?)";
        Object[] params = new Object[]{postBoardCommentReq.getContent(), postBoardCommentReq.getGroupIdx(), postBoardCommentReq.getParentIdx(), boardIdx, userIdx};
        return this.jdbcTemplate.update(createBoardCommentQuery, params);
    }

    public int checkCommentGroupIdx(int groupIdx) {
        String checkCommentGroupIdxQuery = "select exists(select * from Comment where groupIdx = ? and status != 'DELETE')";
        return this.jdbcTemplate.queryForObject(checkCommentGroupIdxQuery, int.class, groupIdx);

    }

    public int updateBoardComment(PatchBoardCommentReq patchBoardCommentReq, int commentIdx) {
        String updateBoardCommentQuery = "update Comment set content = ? where commentIdx = ? and status = 'ACTIVE'";
        Object[] param = new Object[]{patchBoardCommentReq.getContent(), commentIdx};
        return this.jdbcTemplate.update(updateBoardCommentQuery, param);

    }

    public int deleteBoardComment(int commentIdx) {
        String deleteBoardCommentQuery = "update Comment set status = 'DELETE' where commentIdx =? and status = 'ACTIVE'";
        return this.jdbcTemplate.update(deleteBoardCommentQuery, commentIdx);
    }

    public int createCommentLike(int boardIdx, int commentIdx, int userIdx) {
        String createCommentLikeQuery = "insert into CommentLike(commentIdx, userIdx) values(?,?)";
        Object[] param = new Object[]{commentIdx, userIdx};
        return this.jdbcTemplate.update(createCommentLikeQuery, param);
    }

    public int checkCommentLikeExists(int commentIdx, int userIdx) {
        String checkCommentLikeExistsQuery = "select exists(select * from CommentLike where commentIdx =? and userIdx = ? and status = 'ACTIVE')";
        Object[] params = new Object[]{commentIdx, userIdx};
        return this.jdbcTemplate.queryForObject(checkCommentLikeExistsQuery, int.class, params);
    }

    public int deleteCommentLike(int commentIdx, int userIdx) {
        String deleteCommentLikeQuery = "update CommentLike set status = 'DELETE' where commentIdx =? and userIdx =?";
        Object[] params = new Object[]{commentIdx, userIdx};
        return this.jdbcTemplate.update(deleteCommentLikeQuery, params);
    }

    public int checkCommentLikeStatus(int commentIdx, int userIdx) {
        String checkCommentLikeStatusQuery = "select exists(select * from CommentLike where commentIdx = ? and userIdx = ? and status = 'DELETE')";
        Object[] params = new Object[]{commentIdx, userIdx};
        return this.jdbcTemplate.queryForObject(checkCommentLikeStatusQuery, int.class, params);
    }

    public int updateCommentLikeStatus(int commentIdx, int userIdx) {
        String updateCommentLikeStatusQuery = "update CommentLike set status = 'ACTIVE' where commentIdx = ? and userIdx =?";
        Object[] params = new Object[]{commentIdx, userIdx};
        return this.jdbcTemplate.update(updateCommentLikeStatusQuery, params);
    }

    public List<GetBoardCommentRes> getBoardComments(int boardIdx) {
        String getBoardCommentsQuery =
                "select *\n" +
                "from (\n" +
                "    select C.commentIdx, C.groupIdx, C.content, C.parentIdx, \n" +
                "    CASE\n" +
                "        WHEN TIMESTAMPDIFF(MINUTE, C.updatedAt, NOW()) <= 0 THEN '방금 전'\n" +
                "        WHEN TIMESTAMPDIFF(MINUTE, C.updatedAt, NOW()) < 60 THEN CONCAT(TIMESTAMPDIFF(MINUTE, C.updatedAt, NOW()), '분 전')\n" +
                "        WHEN TIMESTAMPDIFF(HOUR, C.updatedAt, NOW()) < 24 THEN CONCAT(TIMESTAMPDIFF(HOUR, C.updatedAt, NOW()), '시간 전')\n" +
                "        WHEN TIMESTAMPDIFF(DAY, C.updatedAt, NOW()) < 7 THEN CONCAT(TIMESTAMPDIFF(DAY, C.updatedAt, NOW()), '일 전')\n" +
                "        WHEN TIMESTAMPDIFF(WEEK, C.updatedAt, NOW()) < 5 THEN CONCAT(TIMESTAMPDIFF(WEEK, C.updatedAt, NOW()), '주 전')\n" +
                "    ELSE CONCAT(TIMESTAMPDIFF(MONTH, C.updatedAt, NOW()), '달 전')\n" +
                "    END AS 'elapsedTime',\n" +
                "    U.nickname, U.profileImage, \n" +
                "    (select count(*) from Comment where boardIdx = ?) as commentCount,\n" +
                "    (select count(*) from CommentLike CL where CL.commentIdx = C.commentIdx and boardIdx = ?) as commentLike\n" +
                "    from Comment C\n" +
                "    left outer join User U on C.userIdx = U.userIdx\n" +
                "    where parentIdx in (\n" +
                "        select commentIdx from (\n" +
                "            select commentIdx from Comment\n" +
                "            where parentIdx = 0 and C.status = 'ACTIVE'\n" +
                "            order by commentIdx desc\n" +
                "        ) as tmp\n" +
                "        )\n" +
                "    ) as tmp2\n" +
                "union ( \n" +
                "    select C.commentIdx, C.groupIdx, C.content, C.parentIdx, \n" +
                "    CASE\n" +
                "        WHEN TIMESTAMPDIFF(MINUTE, C.updatedAt, NOW()) <= 0 THEN '방금 전'\n" +
                "        WHEN TIMESTAMPDIFF(MINUTE, C.updatedAt, NOW()) < 60 THEN CONCAT(TIMESTAMPDIFF(MINUTE, C.updatedAt, NOW()), '분 전')\n" +
                "        WHEN TIMESTAMPDIFF(HOUR, C.updatedAt, NOW()) < 24 THEN CONCAT(TIMESTAMPDIFF(HOUR, C.updatedAt, NOW()), '시간 전')\n" +
                "        WHEN TIMESTAMPDIFF(DAY, C.updatedAt, NOW()) < 7 THEN CONCAT(TIMESTAMPDIFF(DAY, C.updatedAt, NOW()), '일 전')\n" +
                "        WHEN TIMESTAMPDIFF(WEEK, C.updatedAt, NOW()) < 5 THEN CONCAT(TIMESTAMPDIFF(WEEK, C.updatedAt, NOW()), '주 전')\n" +
                "    ELSE CONCAT(TIMESTAMPDIFF(MONTH, C.updatedAt, NOW()), '달 전')\n" +
                "    END AS 'elapsedTime',\n" +
                "    U.nickname, U.profileImage,\n" +
                "    (select count(*) from Comment where boardIdx = ?) as commentCount,\n" +
                "    (select count(*) from CommentLike CL where CL.commentIdx = C.commentIdx and boardIdx = ?) as commentLike\n" +
                "    from Comment C\n" +
                "    left outer join User U on C.userIdx = U.userIdx\n" +
                "    where boardIdx = ? and parentIdx = 0 and C.status = 'ACTIVE'\n" +
                "    order by commentIdx desc\n" +
                ")\n" +
                "order by groupIdx asc, parentIdx asc;";

        Object[] params = new Object[]{boardIdx, boardIdx, boardIdx, boardIdx, boardIdx};
        return this.jdbcTemplate.query(getBoardCommentsQuery,
                (rs, rowNum) -> new GetBoardCommentRes(
                        rs.getInt("commentIdx"),
                        rs.getInt("groupIdx"),
                        rs.getString("content"),
                        rs.getInt("parentIdx"),
                        rs.getString("elapsedTime"),
                        rs.getString("nickname"),
                        rs.getString("profileImage"),
                        rs.getInt("commentCount"),
                        rs.getInt("commentLike")
                ), params);
    }

    public int checkGroupParentIdx(PostBoardCommentReq postBoardCommentReq, int boardIdx) {
        String checkGroupParentIdxQuery = "select exists(select * from Comment where boardIdx = ? and groupIdx = ? and parentIdx = 0 and status != 'DELETE')";
        Object[] params = new Object[]{boardIdx, postBoardCommentReq.getGroupIdx()};
        return this.jdbcTemplate.queryForObject(checkGroupParentIdxQuery, int.class, params);
    }

    public List<GetBoardCommentRes> getBoardCommentsByGroup(int boardIdx, int groupIdx) {
        String getBoardCommentsByGroupQuery =
                "select *\n" +
                "from (\n" +
                "    select C.commentIdx, C.groupIdx, C.content, C.parentIdx, \n" +
                "    CASE\n" +
                "            WHEN TIMESTAMPDIFF(MINUTE, C.updatedAt, NOW()) <= 0 THEN '방금 전'\n" +
                "            WHEN TIMESTAMPDIFF(MINUTE, C.updatedAt, NOW()) < 60 THEN CONCAT(TIMESTAMPDIFF(MINUTE, C.updatedAt, NOW()), '분 전')\n" +
                "            WHEN TIMESTAMPDIFF(HOUR, C.updatedAt, NOW()) < 24 THEN CONCAT(TIMESTAMPDIFF(HOUR, C.updatedAt, NOW()), '시간 전')\n" +
                "            WHEN TIMESTAMPDIFF(DAY, C.updatedAt, NOW()) < 7 THEN CONCAT(TIMESTAMPDIFF(DAY, C.updatedAt, NOW()), '일 전')\n" +
                "            WHEN TIMESTAMPDIFF(WEEK, C.updatedAt, NOW()) < 5 THEN CONCAT(TIMESTAMPDIFF(WEEK, C.updatedAt, NOW()), '주 전')\n" +
                "        ELSE CONCAT(TIMESTAMPDIFF(MONTH, C.updatedAt, NOW()), '달 전')\n" +
                "        END AS 'elapsedTime',\n" +
                "        U.nickname, U.profileImage, \n" +
                "        (select count(*) from Comment where boardIdx = ?) as commentCount,\n" +
                "        (select count(*) from CommentLike CL where CL.commentIdx = C.commentIdx and boardIdx = ?) as commentLike\n" +
                "    from Comment C\n" +
                "    left outer join User U on C.userIdx = U.userIdx\n" +
                "    where parentIdx in (\n" +
                "        select commentIdx from (\n" +
                "            select commentIdx from Comment\n" +
                "            where parentIdx = 0 and C.status = 'ACTIVE'\n" +
                "            order by commentIdx desc\n" +
                "        ) as tmp\n" +
                "        )\n" +
                "    ) as tmp2\n" +
                "    where groupIdx = ?\n" +
                "union ( \n" +
                "    select C.commentIdx, C.groupIdx, C.content, C.parentIdx, \n" +
                "    CASE\n" +
                "        WHEN TIMESTAMPDIFF(MINUTE, C.updatedAt, NOW()) <= 0 THEN '방금 전'\n" +
                "        WHEN TIMESTAMPDIFF(MINUTE, C.updatedAt, NOW()) < 60 THEN CONCAT(TIMESTAMPDIFF(MINUTE, C.updatedAt, NOW()), '분 전')\n" +
                "        WHEN TIMESTAMPDIFF(HOUR, C.updatedAt, NOW()) < 24 THEN CONCAT(TIMESTAMPDIFF(HOUR, C.updatedAt, NOW()), '시간 전')\n" +
                "        WHEN TIMESTAMPDIFF(DAY, C.updatedAt, NOW()) < 7 THEN CONCAT(TIMESTAMPDIFF(DAY, C.updatedAt, NOW()), '일 전')\n" +
                "        WHEN TIMESTAMPDIFF(WEEK, C.updatedAt, NOW()) < 5 THEN CONCAT(TIMESTAMPDIFF(WEEK, C.updatedAt, NOW()), '주 전')\n" +
                "    ELSE CONCAT(TIMESTAMPDIFF(MONTH, C.updatedAt, NOW()), '달 전')\n" +
                "    END AS 'elapsedTime',\n" +
                "    U.nickname, U.profileImage,\n" +
                "    (select count(*) from Comment where boardIdx = ?) as commentCount,\n" +
                "    (select count(*) from CommentLike CL where CL.commentIdx = C.commentIdx and boardIdx = ?) as commentLike\n" +
                "    from Comment C\n" +
                "    left outer join User U on C.userIdx = U.userIdx\n" +
                "    where boardIdx = ? and parentIdx = 0 and C.groupIdx = 1\n" +
                "    order by commentIdx desc\n" +
                ")\n" +
                "order by groupIdx asc, parentIdx asc;\n";
        
        Object[] params = new Object[]{boardIdx, boardIdx, groupIdx, boardIdx, boardIdx, boardIdx};
        return this.jdbcTemplate.query(getBoardCommentsByGroupQuery,
                (rs, rowNum) -> new GetBoardCommentRes(
                        rs.getInt("commentIdx"),
                        rs.getInt("groupIdx"),
                        rs.getString("content"),
                        rs.getInt("parentIdx"),
                        rs.getString("elapsedTime"),
                        rs.getString("nickname"),
                        rs.getString("profileImage"),
                        rs.getInt("commentCount"),
                        rs.getInt("commentLike")
                ), params);
    }
}
