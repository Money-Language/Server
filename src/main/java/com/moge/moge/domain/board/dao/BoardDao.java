package com.moge.moge.domain.board.dao;

import com.moge.moge.domain.board.dto.req.PostBoardReportReq;
import com.moge.moge.domain.board.dto.res.GetBoardQuizAnswerRes;
import com.moge.moge.domain.board.dto.res.GetBoardQuizRes;
import com.moge.moge.domain.board.dto.res.GetBoardRes;
import com.moge.moge.domain.board.dto.req.PostCommentReportReq;
import com.moge.moge.domain.board.model.res.GetBoardCommentRes;
import com.moge.moge.domain.board.model.res.GetBoardSearchRes;
import com.moge.moge.domain.board.model.res.GetBoardTopRes;
import com.moge.moge.domain.board.dto.req.PatchBoardCommentReq;
import com.moge.moge.domain.board.dto.req.PostBoardCommentReq;
import com.moge.moge.domain.board.model.res.GetRecommendKeywordRes;
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
        String getBoardTopLikeQuery =
                "select boardIdx, categoryName, nickname, profileImage, title, viewCount,\n" +
                "\t(select count(*) from BoardLike BL where BL.boardIdx = B.boardIdx) as likeCount,\n" +
                "    (select count(*) from Quiz Q where Q.boardIdx = B.boardIdx) as quizCount,\n" +
                "    (select count(*) from Comment C where C.boardIdx = B.boardIdx) as commentCount,\n" +
                "\tCASE\n" +
                "\t\tWHEN TIMESTAMPDIFF(MINUTE, B.createdAt, NOW()) <= 0 THEN '방금 전'\n" +
                "        WHEN TIMESTAMPDIFF(MINUTE, B.createdAt, NOW()) < 60 THEN CONCAT(TIMESTAMPDIFF(MINUTE, B.createdAt, NOW()), '분 전')\n" +
                "        WHEN TIMESTAMPDIFF(HOUR, B.createdAt, NOW()) < 24 THEN CONCAT(TIMESTAMPDIFF(HOUR, B.createdAt, NOW()), '시간 전')\n" +
                "        WHEN TIMESTAMPDIFF(DAY, B.createdAt, NOW()) < 7 THEN CONCAT(TIMESTAMPDIFF(DAY, B.createdAt, NOW()), '일 전')\n" +
                "        WHEN TIMESTAMPDIFF(WEEK, B.createdAt, NOW()) < 5 THEN CONCAT(TIMESTAMPDIFF(WEEK, B.createdAt, NOW()), '주 전')\n" +
                "    ELSE CONCAT(TIMESTAMPDIFF(MONTH, B.createdAt, NOW()), '달 전')\n" +
                "    END AS 'elapsedTime'  \n" +
                "from Board B\n" +
                "\tleft join Category C on C.categoryIdx = B.categoryIdx\n" +
                "\tleft join User U on B.userIdx = U.userIdx\n" +
                "order by likeCount desc limit 10;";

        return this.jdbcTemplate.query(getBoardTopLikeQuery,
                (rs, rowNum) -> new GetBoardTopRes(
                        rs.getInt("boardIdx"),
                        rs.getString("categoryName"),
                        rs.getString("title"),
                        rs.getInt("viewCount"),
                        rs.getInt("likeCount"),
                        rs.getInt("quizCount"),
                        rs.getInt("commentCount"),
                        rs.getString("nickname"),
                        rs.getString("profileImage"),
                        rs.getString("elapsedTime")
                ));
    }

    public List<GetBoardTopRes> getBoardTopView() {
        String getBoardTopViewQuery =
                "select boardIdx, categoryName, nickname, profileImage, title, viewCount,\n" +
                "\t(select count(*) from BoardLike BL where BL.boardIdx = B.boardIdx) as likeCount,\n" +
                "    (select count(*) from Quiz Q where Q.boardIdx = B.boardIdx) as quizCount,\n" +
                "    (select count(*) from Comment C where C.boardIdx = B.boardIdx) as commentCount,\n" +
                "\tCASE\n" +
                "\t\tWHEN TIMESTAMPDIFF(MINUTE, B.createdAt, NOW()) <= 0 THEN '방금 전'\n" +
                "        WHEN TIMESTAMPDIFF(MINUTE, B.createdAt, NOW()) < 60 THEN CONCAT(TIMESTAMPDIFF(MINUTE, B.createdAt, NOW()), '분 전')\n" +
                "        WHEN TIMESTAMPDIFF(HOUR, B.createdAt, NOW()) < 24 THEN CONCAT(TIMESTAMPDIFF(HOUR, B.createdAt, NOW()), '시간 전')\n" +
                "        WHEN TIMESTAMPDIFF(DAY, B.createdAt, NOW()) < 7 THEN CONCAT(TIMESTAMPDIFF(DAY, B.createdAt, NOW()), '일 전')\n" +
                "        WHEN TIMESTAMPDIFF(WEEK, B.createdAt, NOW()) < 5 THEN CONCAT(TIMESTAMPDIFF(WEEK, B.createdAt, NOW()), '주 전')\n" +
                "    ELSE CONCAT(TIMESTAMPDIFF(MONTH, B.createdAt, NOW()), '달 전')\n" +
                "    END AS 'elapsedTime'  \n" +
                "from Board B\n" +
                "\tleft join Category C on C.categoryIdx = B.categoryIdx\n" +
                "\tleft join User U on B.userIdx = U.userIdx\n" +
                "order by viewCount desc limit 10";

        return this.jdbcTemplate.query(getBoardTopViewQuery,
                (rs, rowNum) -> new GetBoardTopRes(
                        rs.getInt("boardIdx"),
                        rs.getString("categoryName"),
                        rs.getString("title"),
                        rs.getInt("viewCount"),
                        rs.getInt("likeCount"),
                        rs.getInt("quizCount"),
                        rs.getInt("commentCount"),
                        rs.getString("nickname"),
                        rs.getString("profileImage"),
                        rs.getString("elapsedTime")
                ));
    }

    public List<GetBoardSearchRes> getBoardByKeyword(String title) {
        String getBoardByKeywordQuery =
                "select boardIdx, categoryName, nickname, profileImage, title, viewCount,\n" +
                "\t(select count(*) from BoardLike BL where BL.boardIdx = B.boardIdx) as likeCount,\n" +
                "    (select count(*) from Quiz Q where Q.boardIdx = B.boardIdx) as quizCount,\n" +
                "    (select count(*) from Comment C where C.boardIdx = B.boardIdx) as commentCount,\n" +
                "\tCASE\n" +
                "\t\tWHEN TIMESTAMPDIFF(MINUTE, B.createdAt, NOW()) <= 0 THEN '방금 전'\n" +
                "        WHEN TIMESTAMPDIFF(MINUTE, B.createdAt, NOW()) < 60 THEN CONCAT(TIMESTAMPDIFF(MINUTE, B.createdAt, NOW()), '분 전')\n" +
                "        WHEN TIMESTAMPDIFF(HOUR, B.createdAt, NOW()) < 24 THEN CONCAT(TIMESTAMPDIFF(HOUR, B.createdAt, NOW()), '시간 전')\n" +
                "        WHEN TIMESTAMPDIFF(DAY, B.createdAt, NOW()) < 7 THEN CONCAT(TIMESTAMPDIFF(DAY, B.createdAt, NOW()), '일 전')\n" +
                "        WHEN TIMESTAMPDIFF(WEEK, B.createdAt, NOW()) < 5 THEN CONCAT(TIMESTAMPDIFF(WEEK, B.createdAt, NOW()), '주 전')\n" +
                "    ELSE CONCAT(TIMESTAMPDIFF(MONTH, B.createdAt, NOW()), '달 전')\n" +
                "    END AS 'elapsedTime'  \n" +
                "from Board B\n" +
                "\tleft join Category C on C.categoryIdx = B.categoryIdx\n" +
                "\tleft join User U on B.userIdx = U.userIdx\n" +
                "where B.title like concat('%', ?, '%')\n" +
                "order by B.updatedAt desc;";

        return this.jdbcTemplate.query(getBoardByKeywordQuery,
                (rs, rowNum) -> new GetBoardSearchRes(
                        rs.getInt("boardIdx"),
                        rs.getString("categoryName"),
                        rs.getString("title"),
                        rs.getInt("viewCount"),
                        rs.getInt("likeCount"),
                        rs.getInt("quizCount"),
                        rs.getInt("commentCount"),
                        rs.getString("nickname"),
                        rs.getString("profileImage"),
                        rs.getString("elapsedTime")
                ), title);
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
                "    where boardIdx = ? and parentIdx = 0 and C.groupIdx = ?\n" +
                "    order by commentIdx desc\n" +
                ")\n" +
                "order by groupIdx asc, parentIdx asc;\n";

        Object[] params = new Object[]{boardIdx, boardIdx, groupIdx, boardIdx, boardIdx, boardIdx, groupIdx};
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

    public int checkCommentParentIdx(int commentIdx) {
        String checkCommentParentIdxQuery = "select parentIdx from Comment where commentIdx =?";
        System.out.println("what : " + this.jdbcTemplate.queryForObject(checkCommentParentIdxQuery, int.class, commentIdx));
        return this.jdbcTemplate.queryForObject(checkCommentParentIdxQuery, int.class, commentIdx);
    }

    public int deleteParentComment(int boardIdx, int commentIdx) {
        String deleteParentCommentQuery = "update Comment set status = 'DELETE', content = '삭제된 댓글입니다.' where boardIdx =? and commentIdx =? and status = 'ACTIVE'";
        Object[] params = new Object[]{boardIdx, commentIdx};
        return this.jdbcTemplate.update(deleteParentCommentQuery, params);
    }

    public int deleteChildComment(int boardIdx, int commentIdx) {
        String deleteBoardCommentQuery = "update Comment set status = 'DELETE' where boardIdx =? and commentIdx =? and status = 'ACTIVE'";
        Object[] params = new Object[]{boardIdx, commentIdx};
        return this.jdbcTemplate.update(deleteBoardCommentQuery, params);
    }

    public int reportComment(int userIdx, int boardIdx, int commentIdx, PostCommentReportReq postCommentReportReq) {
        String reportCommentQuery = "insert into Report(content, userIdx, boardIdx, commentIdx) values(?,?,?,?)";
        Object[] params = new Object[]{postCommentReportReq.getContent(), userIdx, boardIdx, commentIdx};
        return this.jdbcTemplate.update(reportCommentQuery, params);
    }

    public int checkCommentReportAlreadyExists(int userIdx, int commentIdx) {
        String checkUserCommentReportQuery = "select exists(select * from Report where userIdx = ? and commentIdx = ?)";
        Object[] params = new Object[] {userIdx, commentIdx};
        return this.jdbcTemplate.queryForObject(checkUserCommentReportQuery, int.class, params);
    }

    public int checkCommentExists(int commentIdx) {
        String checkCommentStatusQuery = "select exists(select * from Comment where commentIdx =? and status = 'ACTIVE')";
        return this.jdbcTemplate.queryForObject(checkCommentStatusQuery, int.class, commentIdx);
    }

    public int checkCommentReportCount(int commentIdx) {
        String checkCommentReportCountQuery = "select count(*) as reportCount from Report where commentIdx = ?";
        return this.jdbcTemplate.queryForObject(checkCommentReportCountQuery, int.class, commentIdx);
    }

    public int updateCommentStatus(int commentIdx) {
        String updateCommentStatusQuery = "update Comment set status = 'INACTIVE' where commentIdx = ?";
        return this.jdbcTemplate.update(updateCommentStatusQuery, commentIdx);
    }

    public int checkCommentWriter(int commentIdx) {
        String checkCommentUserIdxQuery = "select userIdx from Comment where commentIdx = ?";
        System.out.println("userIdx : " + this.jdbcTemplate.queryForObject(checkCommentUserIdxQuery, int.class, commentIdx));
        return this.jdbcTemplate.queryForObject(checkCommentUserIdxQuery, int.class, commentIdx);
    }

    public int getAllRecommendKeywordCounts() {
        String getAllRecommendKeywordCountsQuery = "select count(*) from Recommend";
        return this.jdbcTemplate.queryForObject(getAllRecommendKeywordCountsQuery, int.class);
    }

    public GetRecommendKeywordRes getRecommendKeyword(int randomIdx) {
        String getRecommendKeywordQuery = "select keyword from Recommend where recommendIdx = ?";
        return this.jdbcTemplate.queryForObject(getRecommendKeywordQuery,
                (rs, rowNum) -> new GetRecommendKeywordRes(
                        rs.getString("keyword")
                ), randomIdx);
    }

    public int updateViewCount(int boardIdx) {
        String updateViewCountQuery = "update Board set viewCount = viewCount + 1 where boardIdx = ?";
        return this.jdbcTemplate.update(updateViewCountQuery, boardIdx);
    }

    public List<GetBoardQuizRes> getBoardQuiz(int boardIdx) {
        String getBoardQuizQuery = "select quizIdx, quizType, question from Quiz where boardIdx = ? and status = 'ACTIVE'";
        return this.jdbcTemplate.query(getBoardQuizQuery,
                (rs, rowNum) -> new GetBoardQuizRes(
                        rs.getInt("quizIdx"),
                        rs.getInt("quizType"),
                        rs.getString("question")
                ), boardIdx);
    }

    public List<GetBoardQuizAnswerRes> getBoardQuizAnswers(int boardIdx, int quizIdx) {
        String getBoardQuizAnswer =
                "select Q.quizIdx,\n" +
                "    if (Q.quizType = 1, (O.content), (S.content)) as content,\n" +
                "    if (Q.quizType = 1, (O.isAnswer), (S.isAnswer)) as isAnswer,\n" +
                "    if (Q.quizType = 1, 'OBJECTIVE', S.hint) as hint\n" +
                "from Quiz Q\n" +
                "left join ObjectiveAnswer O on Q.quizIdx = O.quizIdx\n" +
                "left join SubjectiveAnswer S on Q.quizIdx = S.quizIdx\n" +
                "where Q.boardIdx = ? and Q.quizIdx = ? and Q.status = 'ACTIVE'";
        Object[] params = new Object[]{boardIdx, quizIdx};
        return this.jdbcTemplate.query(getBoardQuizAnswer,
                (rs, rowNum) -> new GetBoardQuizAnswerRes(
                        rs.getInt("quizIdx"),
                        rs.getString("content"),
                        rs.getInt("isAnswer"),
                        rs.getString("hint")
                ), params);
    }

    public List<GetBoardRes> getBoardsByCategoryIdx(int categoryIdx) {
        String query =
                "SELECT  B.boardIdx, U.nickname, U.profileImage,\n" +
                "    CASE\n" +
                "         WHEN TIMESTAMPDIFF(MINUTE, B.createdAt, NOW()) <= 0 THEN '방금 전'\n" +
                "         WHEN TIMESTAMPDIFF(MINUTE, B.createdAt, NOW()) < 60 THEN CONCAT(TIMESTAMPDIFF(MINUTE, B.createdAt, NOW()), '분 전')\n" +
                "         WHEN TIMESTAMPDIFF(HOUR, B.createdAt, NOW()) < 24 THEN CONCAT(TIMESTAMPDIFF(HOUR, B.createdAt, NOW()), '시간 전')\n" +
                "         WHEN TIMESTAMPDIFF(DAY, B.createdAt, NOW()) < 7 THEN CONCAT(TIMESTAMPDIFF(DAY, B.createdAt, NOW()), '일 전')\n" +
                "         WHEN TIMESTAMPDIFF(WEEK, B.createdAt, NOW()) < 5 THEN CONCAT(TIMESTAMPDIFF(WEEK, B.createdAt, NOW()), '주 전')\n" +
                "    ELSE CONCAT(TIMESTAMPDIFF(MONTH, B.createdAt, NOW()), '달 전')\n" +
                "    END AS 'elapsedTime',\n" +
                "    B.title,\n" +
                "    COUNT(distinct Q.quizIdx) AS 'quizCount',\n" +
                "    B.viewCount,\n" +
                "    COUNT(distinct (case when BL.status='ACTIVE' then BL.boardLikeIdx end)) AS 'likeCount',\n" +
                "    COUNT(distinct (case when CM.status='ACTIVE' then CM.commentIdx end)) AS 'commentCount'\n" +
                "FROM User U\n" +
                "    LEFT JOIN Board B on U.userIdx = B.userIdx\n" +
                "    LEFT JOIN Quiz Q on B.boardIdx = Q.boardIdx\n" +
                "    LEFT JOIN Category C on B.categoryIdx = C.categoryIdx\n" +
                "    LEFT JOIN BoardLike BL on B.boardIdx = BL.boardIdx\n" +
                "    LEFT JOIN Comment CM on B.boardIdx = CM.boardIdx\n" +
                "WHERE C.categoryIdx = ? AND B.status = 'ACTIVE'\n" +
                "GROUP BY B.boardIdx, B.viewCount\n" +
                "ORDER BY elapsedTime ASC\n";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetBoardRes(
                        rs.getInt("boardIdx"),
                        rs.getString("nickname"),
                        rs.getString("profileImage"),
                        rs.getString("elapsedTime"),
                        rs.getString("title"),
                        rs.getInt("quizCount"),
                        rs.getInt("viewCount"),
                        rs.getInt("likeCount"),
                        rs.getInt("commentCount")

                ), categoryIdx);
    }

    public List<GetBoardRes> getBoardsByCategoryIdxOrderByView(int categoryIdx) {
        String query =
                "SELECT  B.boardIdx, U.nickname, U.profileImage,\n" +
                "    CASE\n" +
                "         WHEN TIMESTAMPDIFF(MINUTE, B.createdAt, NOW()) <= 0 THEN '방금 전'\n" +
                "         WHEN TIMESTAMPDIFF(MINUTE, B.createdAt, NOW()) < 60 THEN CONCAT(TIMESTAMPDIFF(MINUTE, B.createdAt, NOW()), '분 전')\n" +
                "         WHEN TIMESTAMPDIFF(HOUR, B.createdAt, NOW()) < 24 THEN CONCAT(TIMESTAMPDIFF(HOUR, B.createdAt, NOW()), '시간 전')\n" +
                "         WHEN TIMESTAMPDIFF(DAY, B.createdAt, NOW()) < 7 THEN CONCAT(TIMESTAMPDIFF(DAY, B.createdAt, NOW()), '일 전')\n" +
                "         WHEN TIMESTAMPDIFF(WEEK, B.createdAt, NOW()) < 5 THEN CONCAT(TIMESTAMPDIFF(WEEK, B.createdAt, NOW()), '주 전')\n" +
                "    ELSE CONCAT(TIMESTAMPDIFF(MONTH, B.createdAt, NOW()), '달 전')\n" +
                "    END AS 'elapsedTime',\n" +
                "    B.title,\n" +
                "    COUNT(distinct Q.quizIdx) AS 'quizCount',\n" +
                "    B.viewCount,\n" +
                "    COUNT(distinct (case when BL.status='ACTIVE' then BL.boardLikeIdx end)) AS 'likeCount',\n" +
                "    COUNT(distinct (case when CM.status='ACTIVE' then CM.commentIdx end)) AS 'commentCount'\n" +
                "FROM User U\n" +
                "    LEFT JOIN Board B on U.userIdx = B.userIdx\n" +
                "    LEFT JOIN Quiz Q on B.boardIdx = Q.boardIdx\n" +
                "    LEFT JOIN Category C on B.categoryIdx = C.categoryIdx\n" +
                "    LEFT JOIN BoardLike BL on B.boardIdx = BL.boardIdx\n" +
                "    LEFT JOIN Comment CM on B.boardIdx = CM.boardIdx\n" +
                "WHERE C.categoryIdx = ? AND B.status = 'ACTIVE'\n" +
                "GROUP BY B.boardIdx, B.viewCount\n" +
                "ORDER BY B.viewCount DESC\n";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetBoardRes(
                        rs.getInt("boardIdx"),
                        rs.getString("nickname"),
                        rs.getString("profileImage"),
                        rs.getString("elapsedTime"),
                        rs.getString("title"),
                        rs.getInt("quizCount"),
                        rs.getInt("viewCount"),
                        rs.getInt("likeCount"),
                        rs.getInt("commentCount")

                ), categoryIdx);
    }

    public List<GetBoardRes> getBoardsByCategoryIdxOrderByLike(int categoryIdx) {
        String query = "SELECT  B.boardIdx, U.nickname, U.profileImage,\n" +
                "    CASE\n" +
                "         WHEN TIMESTAMPDIFF(MINUTE, B.createdAt, NOW()) <= 0 THEN '방금 전'\n" +
                "         WHEN TIMESTAMPDIFF(MINUTE, B.createdAt, NOW()) < 60 THEN CONCAT(TIMESTAMPDIFF(MINUTE, B.createdAt, NOW()), '분 전')\n" +
                "         WHEN TIMESTAMPDIFF(HOUR, B.createdAt, NOW()) < 24 THEN CONCAT(TIMESTAMPDIFF(HOUR, B.createdAt, NOW()), '시간 전')\n" +
                "         WHEN TIMESTAMPDIFF(DAY, B.createdAt, NOW()) < 7 THEN CONCAT(TIMESTAMPDIFF(DAY, B.createdAt, NOW()), '일 전')\n" +
                "         WHEN TIMESTAMPDIFF(WEEK, B.createdAt, NOW()) < 5 THEN CONCAT(TIMESTAMPDIFF(WEEK, B.createdAt, NOW()), '주 전')\n" +
                "    ELSE CONCAT(TIMESTAMPDIFF(MONTH, B.createdAt, NOW()), '달 전')\n" +
                "    END AS 'elapsedTime',\n" +
                "    B.title,\n" +
                "    COUNT(distinct Q.quizIdx) AS 'quizCount',\n" +
                "    B.viewCount,\n" +
                "    COUNT(distinct (case when BL.status='ACTIVE' then BL.boardLikeIdx end)) AS 'likeCount',\n" +
                "    COUNT(distinct (case when CM.status='ACTIVE' then CM.commentIdx end)) AS 'commentCount'\n" +
                "FROM User U\n" +
                "    LEFT JOIN Board B on U.userIdx = B.userIdx\n" +
                "    LEFT JOIN Quiz Q on B.boardIdx = Q.boardIdx\n" +
                "    LEFT JOIN Category C on B.categoryIdx = C.categoryIdx\n" +
                "    LEFT JOIN BoardLike BL on B.boardIdx = BL.boardIdx\n" +
                "    LEFT JOIN Comment CM on B.boardIdx = CM.boardIdx\n" +
                "WHERE C.categoryIdx = ? AND B.status = 'ACTIVE'\n" +
                "GROUP BY B.boardIdx, B.viewCount\n" +
                "ORDER BY B.likeCount DESC\n";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetBoardRes(
                        rs.getInt("boardIdx"),
                        rs.getString("nickname"),
                        rs.getString("profileImage"),
                        rs.getString("elapsedTime"),
                        rs.getString("title"),
                        rs.getInt("quizCount"),
                        rs.getInt("viewCount"),
                        rs.getInt("likeCount"),
                        rs.getInt("commentCount")

                ), categoryIdx);
    }

    public int checkBoardWriter(int boardIdx) {
        String checkCommentUserIdxQuery = "select userIdx from Board where boardIdx = ?";
        return this.jdbcTemplate.queryForObject(checkCommentUserIdxQuery, int.class, boardIdx);
    }

    public int checkBoardReportAlreadyExists(int userIdx, int boardIdx) {
        String checkUserBoardReportQuery = "select exists(select * from Report where userIdx = ? and boardIdx = ?)";
        Object[] params = new Object[] {userIdx, boardIdx};
        return this.jdbcTemplate.queryForObject(checkUserBoardReportQuery, int.class, params);
    }

    public int checkBoardReportCount(int boardIdx) {
        String checkCommentReportCountQuery = "select count(*) as reportCount from Report where boardIdx = ?";
        return this.jdbcTemplate.queryForObject(checkCommentReportCountQuery, int.class, boardIdx);
    }

    public int updateBoardStatus(int boardIdx) {
        String updateBoardStatusQuery = "update Board set status = 'INACTIVE' where boardIdx = ?";
        return this.jdbcTemplate.update(updateBoardStatusQuery, boardIdx);
    }

    public int reportBoard(int userIdx, int boardIdx, PostBoardReportReq postBoardReportReq) {
        String reportCommentQuery = "insert into Report(content, userIdx, boardIdx) values(?,?,?)";
        Object[] params = new Object[]{postBoardReportReq.getContent(), userIdx, boardIdx};
        return this.jdbcTemplate.update(reportCommentQuery, params);
    }
}
