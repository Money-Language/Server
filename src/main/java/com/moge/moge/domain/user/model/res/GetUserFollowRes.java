package com.moge.moge.domain.user.model.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserFollowRes {
    private int userIdx;
    private String nickname;
    private String profileImage;
}
