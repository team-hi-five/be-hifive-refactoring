package com.h5.domain.user.parent.dto.response.info;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class MyChildInfo {
    private Integer childId;
    private String profileImgUrl;
    private String name;
    private Integer age;
    private String gender;
}
