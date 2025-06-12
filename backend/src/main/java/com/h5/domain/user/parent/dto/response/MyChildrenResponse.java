package com.h5.domain.user.parent.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Builder
public class MyChildrenResponse {
    private int childUserId;
    private String childUserName;
}
