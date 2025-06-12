package com.h5.domain.user.parent.dto.response.info;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class MyInfo {
    private int parentId;
    private String name;
    private String phone;
    private String email;
}
