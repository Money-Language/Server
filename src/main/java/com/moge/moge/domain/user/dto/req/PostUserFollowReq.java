package com.moge.moge.domain.user.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostUserFollowReq {
    private int userIdx; // 나
    private int followingIdx; // 팔로우하고자 하는 유저의 식별자
}
