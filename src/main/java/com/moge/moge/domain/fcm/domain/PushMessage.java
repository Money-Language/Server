package com.moge.moge.domain.fcm.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PushMessage {

    MORNING_DIET("아침 식사 드셨나요?", "아침 식사를 드셨다면, 식사를 기록해주세요!"),
    LUNCH_DIET("점심 식사 드셨나요?", "점심 식사를 드셨다면, 식사를 기록해주세요!"),
    DINNER_DIET("저녁 식사 드셨나요?", "저녁 식사를 드셨다면, 식사를 기록해주세요!");


    String title;
    String body;
}
