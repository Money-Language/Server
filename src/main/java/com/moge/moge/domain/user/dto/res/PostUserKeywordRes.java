package com.moge.moge.domain.user.dto.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PostUserKeywordRes {
    @JsonProperty
    private String result;
}
