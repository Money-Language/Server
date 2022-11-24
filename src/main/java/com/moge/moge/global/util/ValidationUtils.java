package com.moge.moge.global.util;

import com.moge.moge.domain.user.service.UserProvider;
import com.moge.moge.global.common.BaseResponse;
import com.moge.moge.global.config.security.JwtService;
import com.moge.moge.global.exception.BaseException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.moge.moge.global.exception.BaseResponseStatus.*;

@Component
public class ValidationUtils {

    private final JwtService jwtService;
    private final UserProvider userProvider;

    public ValidationUtils(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public BaseResponse<Integer> validateJwtToken(int userIdx) throws BaseException {
        int userIdxByJwt = jwtService.getUserIdx();
        if (userIdxByJwt != userIdx) {
            return new BaseResponse<>(INVALID_USER_JWT);
        }
        return new BaseResponse<>(userIdxByJwt);
    }

    public int checkJwtTokenExists() throws BaseException throws BaseException {
        int userIdxByJwt = jwtService.getUserIdx();
        if (userProvider.checkUser(userIdxByJwt) == 0) {
            throw new BaseException(USERS_EMPTY_USER_IDX);
        }
        return userIdxByJwt;
    }

    public void validateSize(int target, int size) throws BaseException {
        if (target != size) {
            throw new BaseException(POST_USERS_CATEGORY_NUM);
        }
    }

    public void validateRange(int target, int range) throws BaseException {
        if  (target <= range) {
            throw new BaseException(POST_FOLLOW_INVALID_PAGE);
        }
    }

    public static void checkEmailNull(String email) throws BaseException {
        if (email == null) {
            throw new BaseException(POST_USERS_EMPTY_EMAIL);
        }
    }

    public static void checkPasswordNull(String password) throws BaseException {
        if (password == null) {
            throw new BaseException(POST_USERS_EMPTY_PASSWORD);
        }
    }

    public static void checkRePasswordNull(String rePassword) throws BaseException {
        if (rePassword == null) {
            throw new BaseException(POST_USERS_EMPTY_REPASSWORD);
        }
    }

    public static void checkNicknameNull(String nickname) throws BaseException {
        if (nickname == null) {
            throw new BaseException(POST_USERS_EMPTY_NICKNAME);
        }
    }

    public static void checkContractNull(Integer contract1, Integer contract2, Integer contract3) throws BaseException {
        if (contract1 == null || contract2 == null || contract3 == null) {
            throw new BaseException(POST_USERS_EMPTY_TERMS);
        }
    }

    public static void checkSamePassword(String password, String rePassword) throws BaseException {
        if (!password.equals(rePassword)) {
            throw new BaseException(POST_USERS_INVALID_REPASSWORD);
        }
    }
}
