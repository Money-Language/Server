package com.moge.moge.domain.fcm.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FcmDto {
    private String targetToken;
    private String title;
    private String body;
}
