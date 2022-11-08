package com.moge.moge.global.exception;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {

    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),
    SUCCESS_CHECK_CERTIFY_EMAIL(true, 1001, "인증번호 확인에 성공하였습니다"),
    SUCCESS_UPDATE_PASSWORD(true, 1002, "유저의 패스워드 변경에 성공하였습니다."),
    SUCCESS_LOGOUT(true , 1003, "로그아웃에 성공하였습니다."),
    SUCCESS_DELETE_USER(true, 1004, "유저 탈퇴에 성공하였습니다."),
    SUCCESS_CREATE_KEYWORD(true, 1005, "관심 키워드 설정에 성공하였습니다."),
    SUCCESS_UPDATE_KEYWORD(true, 1006, "관심 키워드 수정에 성공하였습니다."),

    SUCCESS_DELETE_AWS_S3(true, 1007, "aws s3 삭제에 성공하였습니다."),
    SUCCESS_DELETE_USER_PROFILE_IMAGE(true, 1008, "유저 프로필 이미지 삭제에 성공하였습니다."),
    SUCCESS_UPDATE_PROFILE(true, 1009, "유저의 프로필 수정에 성공하였습니다."),
    SUCCESS_CREATE_FOLLOW(true, 1010, "팔로우/언팔로우에 성공하였습니다."),

    SUCCESS_CREATE_BOARD_LIKE(true, 1011, "게시글 좋아요 등록/해제에 성공하였습니다."),
    SUCCESS_CREATE_BOARD_COMMENT(true, 1012, "게시글 댓글 생성에 성공하였습니다."),
    SUCCESS_UPDATE_BOARD_COMMENT(true, 1013, "게시글 댓글 수정에 성공하였습니다."),
    SUCCESS_DELETE_BOARD_COMMENT(true, 1014, "게시글 댓글 삭제에 성공하였습니다."),
    SUCCESS_CREATE_BOARD_COMMENT_LIKE(true, 1015, "게시글 댓글 좋아요 등록/해제에 성공하였습니다."),
    SUCCESS_CREATE_COMMENT_REPORT(true, 1016, "댓글 신고에 성공하였습니다."),

    SUCCESS_CHECK_NICKNAME(true, 1016, "중복되지 않는 닉네임입니다."),
    SUCCESS_CHECK_EMAIL(true, 1017, "중복되지 않는 이메일입니다."),
    SUCCESS_CHECK_PASSWORD(true, 1018, "비밀번호 validation에 성공하였습니다."),

    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    POST_USERS_EMPTY_EMAIL(false, 2010, "이메일을 입력해주세요."),
    POST_USERS_EMPTY_NICKNAME(false, 2011, "닉네임을 입력해주세요."),
    POST_USERS_EMPTY_PASSWORD(false, 2012, "비밀번호를 입력해주세요."),
    POST_USERS_EMPTY_TERMS(false, 2013, "필수 약관에 동의해주세요."),
    POST_USERS_EMPTY_REPASSWORD(false, 2014, "확인 비밀번호를 입력해주세요"),
    POST_USERS_EMPTY_CERTIFIED_EMAIL(false, 2015, "인증 이메일을 입력해주세요"),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_INVALID_PASSWORD(false, 2017, "비밀번호 형식을 확인해주세요."),
    POST_USERS_INVALID_REPASSWORD(false, 2018, "비밀번호와 확인 비밀번호가 일치하지 않습니다."),
    POST_USERS_INVALID_NICKNAME(false, 2019, "닉네임 형식을 확인해주세요."),
    POST_USERS_INVALID_EMAIL_CODE(false, 2020, "이메일 코드 형식을 확인해주세요"),
    POST_USERS_NEW_PASSWORD_NOT_CORRECT(false, 2021, "변경하고자 하는 비밀번호가 일치하지 않습니다."),
    POST_USERS_CATEGORY_NUM(false, 2022, "관심 키워드를 3개 선택해주세요."),
    POST_FOLLOW_INVALID_PAGE(false, 2023, "페이지는 1이상의 숫자여야 합니다."),
    USERS_EMPTY_USER_IDX(false, 2024, "유저 식별자가 존재하지 않습니다."),

    POST_BOARDS_EMPTY_COMMENT(false, 2030, "댓글 내용을 입력해주세요."),
    POST_BOARDS_COMMENT_INVALID_JWT(false, 2031, "해당 댓글에 대한 권한이 없습니다."),



    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // User
    DUPLICATED_EMAIL(false, 3010, "중복된 이메일입니다."),
    DUPLICATED_NICKNAME(false, 3011, "중복된 닉네임입니다."),
    FAILED_TO_LOGIN(false, 3012, "없는 아이디거나 비밀번호가 틀렸습니다."),
    FAILED_TO_SEND_MAIL(false, 3013, "인증 이메일 발송에 실패하였습니다."),
    FAILED_TO_CERTIFY_TIME(false, 3014, "인증 코드의 유효시간이 지났습니다."),
    FAILED_TO_CERTIFY_CODE(false, 3015, "인증 코드가 일치하지 않습니다."),
    USER_CURRENT_PASSWORD_NOT_CORRECT(false, 3016, "현재 비밀번호가 일치하지 않습니다."),
    FAILED_TO_UPDATE_USERS_PASSWORD(false, 3017, "비밀번호 수정에 실패하였습니다."),
    FAILED_TO_DELETE_USER(false, 3018, "유저 탈퇴에 실패하였습니다."),
    FAILED_TO_USER_KEYWORD(false, 3019, "관심 카테고리 설정에 실패하였습니다."),
    USER_CATEGORY_NOT_EXISTS(false, 3020, "해당 카테고리가 존재하지 않습니다"),
    USER_CATEGORY_ALREADY_EXISTS(false, 3021, "해당 유저는 이미 관심 키워드(카테고리)를 설정하였습니다."),
    FAILED_TO_CREATE_FOLLOW(false, 3022, "팔로우 등록에 실패하였습니다."),
    USER_FOLLOW_ALREADY_EXISTS(false, 3023, "이미 팔로우한 유저입니다."),
    USER_NOT_EXISTS(false, 3024, "팔로우하려는 유저는 존재하지 않는 유저입니다."),
    FAILED_TO_CHECK_EMAIL(false, 3025, "이미 존재하는 이메일입니다."),
    FAILED_TO_CHECK_NICKNAME(false, 3026, "이미 존재하는 닉네임입니다."),

    // board
    BOARD_NOT_EXISTS(false, 3030, "해당 게시물은 존재하지 않습니다."),
    FAILED_TO_CREATE_BOARD_LIKE(false, 3031, "게시물 좋아요 등록,해제에 실패하였습니다."),
    BOARD_COMMENT_GROUP_IDX_NOT_EXISTS(false, 3032, "해당 댓글 그룹 식별자가 존재하지 않습니다."),
    FAILED_TO_UPDATE_COMMENT(false, 3033, "댓글 수정에 실패하였습니다."),
    FAILED_TO_DELETE_COMMENT(false, 3034, "댓글 삭제에 실패하였습니다."),
    FAILED_TO_CREATE_COMMENT_LIKE(false, 3035, "댓글 좋아요 등록/해제에 실패하였습니다"),
    BOARD_COMMENT_GROUP_PARENT_IDX_EXISTS(false, 3036, "해당 그룹의 부모 댓글이 이미 존재합니다."),
    FAILED_TO_CREATE_COMMENT_REPORT(false, 3037, "자신의 댓글은 신고할 수 없습니다."),
    COMMENT_NOT_EXISTS(false, 3038, "해당 댓글이 삭제되었거나, 존재하지 않습니다."),
    COMMENT_REPORT_EXCEED(false, 3039, "해당 댓글은 신고 누적횟수가 3회 이상이므로 신고가 불가능합니다."),
    COMMENT_REPORT_ALREADY_EXISTS(false, 3040, "이미 신고한 댓글입니다."),

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),
    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
