package com.moge.moge.domain.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PushMessage {

    DAILY_QUIZ("오늘의 퀴즈", "오늘의 퀴즈를 풀 시간입니다!"),
    QUIZ_LIKE("퀴즈 좋아요", "내 퀴즈에 좋아요를 눌렀습니다!"),
    QUIZ_COMMENT("퀴즈 댓글", "내 퀴즈에 댓글이 달렸습니다!"),
    COMMENT_LIKE("댓글 좋아요", "내 댓글에 좋아요를 눌렀습니다!"),
    FOLLOW("새로운 팔로워", "나를 팔로우 했습니다!"),
    MENTION("새로운 멘션", "누군가가 나를 멘션하였습니다!");

    String title;
    String body;
}
