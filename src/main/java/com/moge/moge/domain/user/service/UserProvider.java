package com.moge.moge.domain.user.service;

import com.moge.moge.domain.user.dao.UserDao;
import com.moge.moge.domain.user.dto.req.PostUserPasswordValidateReq;
import com.moge.moge.domain.user.dto.res.*;
import com.moge.moge.global.config.security.JwtService;
import com.moge.moge.global.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.moge.moge.global.exception.BaseResponseStatus.*;
import static com.moge.moge.global.util.ValidationRegex.*;
import static com.moge.moge.global.util.ValidationUtils.*;

@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    public int checkEmail(String email) throws BaseException {
        if (userDao.checkEmail(email) == 1) {
            throw new BaseException(FAILED_TO_CHECK_EMAIL);
        }
        try{
            checkEmailNull(email);
            isRegexEmail(email);
            return userDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkNickname(String nickname) throws BaseException {
        if (userDao.checkNickname(nickname) == 1) {
            throw new BaseException(FAILED_TO_CHECK_NICKNAME);
        }
        try {
            checkNicknameNull(nickname);
            isRegexNickname(nickname);
            return userDao.checkNickname(nickname);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void checkPassword(PostUserPasswordValidateReq postUserPasswordValidateReq) throws BaseException {
        try {
            checkPasswordNull(postUserPasswordValidateReq.getPassword());
            checkRePasswordNull(postUserPasswordValidateReq.getRePassword());
            checkSamePassword(postUserPasswordValidateReq.getPassword(), postUserPasswordValidateReq.getRePassword());
            isRegexPassword(postUserPasswordValidateReq.getPassword());

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetUserFollowRes> getUserFollowings(int userIdx, int page) throws BaseException {
        try {
            return userDao.getUserFollowings(userIdx, page);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetUserFollowRes> getUserFollowers(int userIdx, int page) throws BaseException {
        try {
            return userDao.getUserFollowers(userIdx, page);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUser(int userIdx) throws BaseException {
        try {
            return userDao.checkUser(userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetUserProfileRes getUserProfile(int userIdx) throws BaseException {
        try {
            return userDao.getUserProfile(userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetUserBoardRes> getUserBoards(int userIdx) throws BaseException {
        try {
            return userDao.getUserBoards(userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetUserBoardLikeRes> getUserBoardLike(int userIdx) throws BaseException {
        try {
            return userDao.getUserBoardLike(userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUserComment(int userIdx, int commentIdx, int boardIdx) throws BaseException {
        try {
            return userDao.checkUserComment(userIdx, commentIdx, boardIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetUserBoardRes> getUserBoardsByCategory(int userIdx, Integer categoryIdx) throws BaseException {
        try {
            int categoryNumber = categoryIdx.intValue();
            return userDao.getUserBoardsByCategory(userIdx, categoryNumber);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
