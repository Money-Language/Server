package com.moge.moge.domain.user.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserProfileRes {
    private int userIdx;
    private String nickname;
    private String profileImage;
    private int userPoint;
    private int followerCount;
    private int followingCount;
}
