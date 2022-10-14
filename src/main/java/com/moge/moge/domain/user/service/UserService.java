package com.moge.moge.domain.user.service;

import com.moge.moge.domain.user.UserDao;
import com.moge.moge.domain.user.UserProvider;
import com.moge.moge.domain.user.model.req.PatchUserKeywordReq;
import com.moge.moge.domain.user.model.req.PatchUserPasswordReq;
import com.moge.moge.domain.user.model.req.PostUserKeywordReq;
import com.moge.moge.domain.user.model.req.PostUserReq;
import com.moge.moge.domain.user.model.res.GetUserCategoryRes;
import com.moge.moge.domain.user.model.res.PostUserKeywordRes;
import com.moge.moge.domain.user.model.res.PostUserRes;
import com.moge.moge.global.common.BaseResponse;
import com.moge.moge.global.config.secret.Secret;
import com.moge.moge.global.config.security.JwtService;
import com.moge.moge.global.config.security.SHA256;
import com.moge.moge.global.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.moge.moge.global.exception.BaseResponseStatus.*;

@Service
public class UserService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;

    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;
    }

    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        if (userProvider.checkEmail(postUserReq.getEmail()) == 1) {
            throw new BaseException(DUPLICATED_EMAIL);
        }
        if (userProvider.checkNickname(postUserReq.getNickname()) == 1) {
            throw new BaseException(DUPLICATED_NICKNAME);
        }

        String pwd;
        try {
            pwd = new SHA256().encrypt(postUserReq.getPassword());
            postUserReq.setPassword(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        try{
            int userIdx = userDao.createUser(postUserReq);
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(jwt,userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void updatePassword(int userIdx, PatchUserPasswordReq patchUserPasswordReq) throws BaseException{
        String updatedPwd;
        String encryptPwd;

        encryptPwd = new SHA256().encrypt(patchUserPasswordReq.getCurrentPassword());

        System.out.println(encryptPwd);

        if (!encryptPwd.equals(userDao.getUser(userIdx).getPassword())) {
            throw new BaseException(USER_CURRENT_PASSWORD_NOT_CORRECT);
        }

        try {
            updatedPwd = new SHA256().encrypt(patchUserPasswordReq.getModPassword());
            patchUserPasswordReq.setModPassword(updatedPwd);
            int result = userDao.updatePassword(userIdx, patchUserPasswordReq);
            if (result == 0) {
                throw new BaseException(FAILED_TO_UPDATE_USERS_PASSWORD);
            }

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public void deleteUser(int userIdx) throws BaseException {
        try {
            int result = userDao.deleteUser(userIdx);
            if (result == 0) {
                throw new BaseException(FAILED_TO_DELETE_USER);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostUserKeywordRes createUserKeyword(int userIdx, PostUserKeywordReq postUserKeywordReq) throws BaseException {
        try {
            // 해당 카테고리가 DB에 있는지 확인
            for (int categoryIdx : postUserKeywordReq.getCategoryIdx()) {
                if (userDao.checkCategoryExists(categoryIdx) == 0) {
                    throw new BaseException(USER_CATEGORY_NOT_EXISTS);
                }
            }
            // 이미 설정 되어있으면 불가능, 수정 api로만 가능하도록
            if (userDao.checkUserCategoryExists(userIdx) == 1) {
                throw new BaseException(USER_CATEGORY_ALREADY_EXISTS);
            }

            PostUserKeywordRes result = userDao.createUserKeyword(userIdx, postUserKeywordReq);
            return result;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void updateUserKeyword(int userIdx, PatchUserKeywordReq patchUserKeywordReq) throws BaseException {
        try {
            // 변경하고자하는 카테고리의 식별자값이 DB에 있는지 확인
            for (int categoryIdx : patchUserKeywordReq.getCategoryIdx()) {
                if (userDao.checkCategoryExists(categoryIdx) == 0) {
                    throw new BaseException(USER_CATEGORY_NOT_EXISTS);
                }
            }

            List<Integer> userCategoryIdxList = userDao.getUserCategoryIdx(userIdx);
            int[] userCategoryIdx = userCategoryIdxList.stream().mapToInt(Integer::intValue).toArray();
            int i = 0;
            for (int uIdx : userCategoryIdx) {
                System.out.println("=== uIdx : " + uIdx + "=====");
                int index = patchUserKeywordReq.getCategoryIdx().get(i++);
                System.out.println("=== index : " + index + "===");
                userDao.updateUserKeyword(uIdx, index);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
