package com.moge.moge.domain.user.service;

import com.moge.moge.domain.s3.S3Service;
import com.moge.moge.domain.user.dao.UserDao;
import com.moge.moge.domain.user.dto.User;
import com.moge.moge.domain.user.dto.req.*;
import com.moge.moge.domain.user.dto.res.GetUserKeywordRes;
import com.moge.moge.domain.user.dto.res.PostLoginRes;
import com.moge.moge.domain.user.dto.res.PostUserKeywordRes;
import com.moge.moge.domain.user.dto.res.PostUserRes;
import com.moge.moge.global.config.security.JwtService;
import com.moge.moge.global.config.security.SHA256;
import com.moge.moge.global.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.moge.moge.global.exception.BaseResponseStatus.*;
import static com.moge.moge.global.util.ValidationRegex.*;
import static com.moge.moge.global.util.ValidationUtils.*;

@Service
public class UserService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final S3Service s3Service;
    private final JwtService jwtService;

    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, S3Service s3Service, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.s3Service = s3Service;
        this.jwtService = jwtService;
    }

    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        /* 유효성 체크 */
        checkEmailNull(postUserReq.getEmail());
        checkNicknameNull(postUserReq.getNickname());
        checkPasswordNull(postUserReq.getPassword());
        checkRePasswordNull(postUserReq.getRePassword());
        checkContractNull(postUserReq.getContract1(), postUserReq.getContract2(), postUserReq.getContract3());
        isRegexEmail(postUserReq.getEmail());
        isRegexPassword(postUserReq.getPassword());
        isRegexNickname(postUserReq.getNickname());
        checkSamePassword(postUserReq.getPassword(), postUserReq.getRePassword());

        if (userProvider.checkEmail(postUserReq.getEmail()) == 1) {
            throw new BaseException(DUPLICATED_EMAIL);
        }
        if (userProvider.checkNickname(postUserReq.getNickname()) == 1) {
            throw new BaseException(DUPLICATED_NICKNAME);
        }

        /* 비즈니스 로직 */
        try{
            String encryptedPassword = encryptPwd(postUserReq.getPassword());
            postUserReq.setPassword(encryptedPassword);
            int userIdx = userDao.createUser(postUserReq);
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(jwt,userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostLoginRes login(PostLoginReq postLoginReq) throws BaseException {
        /* 유효성 체크 */
        checkEmailNull(postLoginReq.getEmail());
        checkPasswordNull(postLoginReq.getPassword());
        isRegexEmail(postLoginReq.getEmail());
        isRegexPassword(postLoginReq.getPassword());

        /* 비즈니스 로직 */
        User user = userDao.getPwd(postLoginReq);
        String encryptPwd = encryptPwd(postLoginReq.getPassword());
        if (user.getPassword().equals(encryptPwd)) {
            int userIdx = user.getUserIdx();
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx, jwt);
        } else {
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }

    public void updatePassword(int userIdx, PatchUserPasswordReq patchUserPasswordReq) throws BaseException{
        if (!patchUserPasswordReq.getModPassword().equals(patchUserPasswordReq.getReModPassword())) {
            throw new BaseException(POST_USERS_NEW_PASSWORD_NOT_CORRECT);
        }

        String updatedPwd;
        String encryptPwd = encryptPwd(patchUserPasswordReq.getCurrentPassword());

        if (!encryptPwd.equals(userDao.getUser(userIdx).getPassword())) {
            throw new BaseException(USER_CURRENT_PASSWORD_NOT_CORRECT);
        }

        try {
            isRegexPassword(patchUserPasswordReq.getModPassword());
            updatedPwd = encryptPwd(patchUserPasswordReq.getModPassword());
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
            for (int categoryIdx : postUserKeywordReq.getCategoryIdx()) {
                if (userDao.checkCategoryExists(categoryIdx) == 0) {
                    throw new BaseException(USER_CATEGORY_NOT_EXISTS);
                }
            }
            if (userDao.checkUserCategoryExists(userIdx) == 1) {
                throw new BaseException(USER_CATEGORY_ALREADY_EXISTS);
            }
            return userDao.createUserKeyword(userIdx, postUserKeywordReq);
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
                int index = patchUserKeywordReq.getCategoryIdx().get(i++);
                userDao.updateUserKeyword(uIdx, index);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetUserKeywordRes> getUserKeyword(int userIdx) throws BaseException {
        try {
            return userDao.getUserKeyword(userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void updateProfile(int userIdx, MultipartFile profileImage, String nickname) throws BaseException, IOException {
        try {
            String url = s3Service.uploadFile(profileImage);
            userDao.updateUserProfile(userIdx, url, nickname);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteProfileImage(int userIdx) throws BaseException {
        try {
            String userProfileImageUrlInDB = userDao.getUserProfileImage(userIdx);
            s3Service.deleteFile(userProfileImageUrlInDB);
            userDao.deleteUserProfileImage(userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void createUserFollow(int userIdx, int followingIdx) throws BaseException {
        try {
            // 팔로우하려는 유저가 있는지 확인
            if (userDao.checkUserExists(followingIdx) == 0) {
                throw new BaseException(USER_NOT_EXISTS);
            }
            // 이미 팔로우가 등록되어있는지 확인
            if (userDao.checkUserFollowExists(userIdx, followingIdx) == 1) {
                // 등록되어있으면 팔로우 해제 -> status = DELETE
                userDao.deleteUserFollow(userIdx, followingIdx);
            }
            else { // 등록 안되어있고
                // status 가 Delete라면
                if (userDao.checkUserFollowStatus(userIdx, followingIdx) == 1) {
                    userDao.updateUserFollowStatus(userIdx, followingIdx);
                } else {
                    int result = userDao.createUserFollow(userIdx, followingIdx);
                    if (result == 0) {
                        throw new BaseException(FAILED_TO_CREATE_FOLLOW);
                    }
                }
            }

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    private String encryptPwd(String password) throws BaseException {
        String encryptPwd;
        try {
            encryptPwd = new SHA256().encrypt(password);
            return encryptPwd;
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }
    }
}
