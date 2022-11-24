package com.moge.moge.global.util;

import com.moge.moge.global.exception.BaseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.moge.moge.global.exception.BaseResponseStatus.*;

public class ValidationRegex {

    public static void isRegexEmail(String target) throws BaseException {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        if (matcher.find() == false) {
            throw new BaseException(POST_USERS_INVALID_EMAIL);
        }
    }

    // 5글자 이상의 소문자, 대문자, 숫자
    public static void isRegexPassword(String target) throws BaseException {
        String regex = "^[A-Za-z0-9]{5,}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        if (matcher.find() == false) {
            throw new BaseException(POST_USERS_INVALID_PASSWORD);
        }
    }

    // 8자리의 소문자, 대문자, 숫자
    public static void isRegexEmailCode(String target) throws BaseException {
        String regex = "^[A-Za-z0-9]{8}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        if (matcher.find() == false) {
            throw new BaseException(POST_USERS_INVALID_EMAIL_CODE);
        }
    }

    // 한글자 이상의 한글, 소문자, 대문자, 숫자 / ‘ㄴㄴ’ ‘ㅇㅇ’ 이런 글자는 안됨
    public static void isRegexNickname(String target) throws BaseException {
        String regex = "^(?=.*[a-z0-9가-힣])[a-z0-9가-힣]{1,8}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        if (matcher.find() == false) {
            throw new BaseException(POST_USERS_INVALID_NICKNAME);
        }
    }

}
