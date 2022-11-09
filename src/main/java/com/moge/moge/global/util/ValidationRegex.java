package com.moge.moge.global.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationRegex {

    public static boolean isRegexEmail(String target) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    // 5글자 이상의 소문자, 대문자, 숫자
    public static boolean isRegexPassword(String target) {
        String regex = "^[A-Za-z0-9]{5,}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    // 8자리의 소문자, 대문자, 숫자
    public static boolean isRegexEmailCode(String target) {
        String regex = "^[A-Za-z0-9]{8}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    // 한글자 이상의 한글, 소문자, 대문자, 숫자 / ‘ㄴㄴ’ ‘ㅇㅇ’ 이런 글자는 안됨
    public static boolean isRegexNickname(String target) {
        String regex = "^(?=.*[a-z0-9가-힣])[a-z0-9가-힣]{1,8}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

}
