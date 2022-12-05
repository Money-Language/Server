package com.moge.moge.domain.quiz.dao;

import com.moge.moge.domain.quiz.dto.req.PostBoardReq;
import com.moge.moge.domain.quiz.dto.req.PostQuizAnswerReq;
import com.moge.moge.domain.quiz.dto.req.PostQuizPointReq;
import com.moge.moge.domain.quiz.dto.req.PostQuizReq;
import com.moge.moge.domain.quiz.dto.res.PostBoardRes;
import com.moge.moge.domain.quiz.dto.res.PostQuizAnswerRes;
import com.moge.moge.domain.quiz.dto.res.PostQuizRes;
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


    public PostQuizRes createQuiz(PostQuizReq postQuizReq) {
        String createQuizQuery = "insert into Quiz(question, quizType, boardIdx) values(?, ?, ?)";
        Object[] createQuizParams = new Object[] {
                postQuizReq.getQuestion(),
                postQuizReq.getQuizType(),
                postQuizReq.getBoardIdx()
        };

        this.jdbcTemplate.update(createQuizQuery, createQuizParams);
        String lastInsertIdQuery = "select last_insert_id()";
        int quizIdx = this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
        return new PostQuizRes(postQuizReq.getQuestion(), postQuizReq.getQuizType(), postQuizReq.getBoardIdx(), quizIdx);
    }

    public PostQuizAnswerRes createSubjectiveAnswer(PostQuizAnswerReq postQuizAnswerReq) {
        String createSubjectiveAnswerQuery = "insert into SubjectiveAnswer(hint, content, isAnswer, quizIdx) values(?, ?, ?, ?)";
        Object[] createSubjectiveParams = new Object[] {
                postQuizAnswerReq.getHint(),
                postQuizAnswerReq.getContent(),
                postQuizAnswerReq.getIsAnswer(),
                postQuizAnswerReq.getQuizIdx()
        };
        this.jdbcTemplate.update(createSubjectiveAnswerQuery, createSubjectiveParams);
        return new PostQuizAnswerRes(postQuizAnswerReq.getContent(), postQuizAnswerReq.getIsAnswer(), postQuizAnswerReq.getQuizIdx());
    }

    public PostQuizAnswerRes createObjectiveAnswer(PostQuizAnswerReq postQuizAnswerReq) {
        String createObjectiveAnswerQuery = "insert into ObjectiveAnswer(content, isAnswer, quizIdx) values(?, ?, ?)";
        Object[] createObjectiveParams = new Object[] {
                postQuizAnswerReq.getContent(),
                postQuizAnswerReq.getIsAnswer(),
                postQuizAnswerReq.getQuizIdx()
        };
        this.jdbcTemplate.update(createObjectiveAnswerQuery, createObjectiveParams);
        return new PostQuizAnswerRes(postQuizAnswerReq.getContent(), postQuizAnswerReq.getIsAnswer(), postQuizAnswerReq.getQuizIdx());
    }

    public int getQuizType(int quizIdx) {
        String getQuizTypeQuery = "select quizType from Quiz where quizIdx = ? and status = 'ACTIVE'";
        System.out.println("타입 : " + this.jdbcTemplate.queryForObject(getQuizTypeQuery, int.class, quizIdx));
        return this.jdbcTemplate.queryForObject(getQuizTypeQuery, int.class, quizIdx);
    }

    public int updatePointsByObjective(int userIdx, int quizIdx) {
        String updatePointsByObjectiveQuery =
                "update User U\n" +
                "    left join Quiz Q ON Q.quizIdx = ?\n" +
                "    left join ObjectiveAnswer O on Q.quizIdx = O.quizIdx\n" +
                "set U.userPoint = U.userPoint + 10 \n" +
                "where U.userIdx = ? AND O.isAnswer = 1 AND Q.status = 'ACTIVE'";
        Object[] params = new Object[]{quizIdx, userIdx};
        return this.jdbcTemplate.update(updatePointsByObjectiveQuery, params);
    }

    public int updatePointsBySubjective(int userIdx, int quizIdx) {
        String updatePointsBySubjectiveQuery =
                "update User U\n" +
                "    left join Quiz Q ON Q.quizIdx = ?\n" +
                "    left join SubjectiveAnswer S on Q.quizIdx = S.quizIdx\n" +
                "set U.userPoint = U.userPoint + 10 \n" +
                "where U.userIdx = ? AND S.isAnswer = 1 AND Q.status = 'ACTIVE'";
        Object[] params = new Object[]{quizIdx, userIdx};
        return this.jdbcTemplate.update(updatePointsBySubjectiveQuery, params);
    }

    public String getQuizStatus(int quizIdx) {
        String getQuizStatusQuery = "select status from Quiz where quizIdx = ?";
        return this.jdbcTemplate.queryForObject(getQuizStatusQuery, String.class, quizIdx);
    }
}
