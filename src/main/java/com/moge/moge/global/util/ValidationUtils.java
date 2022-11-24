package com.moge.moge.global.util;

import com.moge.moge.domain.user.model.req.PostUserPasswordValidateReq;
import com.moge.moge.global.common.BaseResponse;
import com.moge.moge.global.config.security.JwtService;
import com.moge.moge.global.exception.BaseException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.moge.moge.global.exception.BaseResponseStatus.*;

@Component
public class ValidationUtils implements Validator {

    private final JwtService jwtService;

    public ValidationUtils(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target == null) {
            errors.rejectValue("email", "email이 null값 입니다");
        }
    }

    public BaseResponse<Integer> validateJwtToken(int userIdx) throws BaseException {
        int userIdxByJwt = jwtService.getUserIdx();
        if (userIdxByJwt != userIdx) {
            return new BaseResponse<>(INVALID_USER_JWT);
        }
        return new BaseResponse<>(userIdxByJwt);
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

    public static void checkSamePassword(PostUserPasswordValidateReq postUserPasswordValidateReq) throws BaseException {
        if (!postUserPasswordValidateReq.getPassword().equals(postUserPasswordValidateReq.getRePassword())) {
            throw new BaseException(POST_USERS_INVALID_REPASSWORD);
        }
    }
}
