package com.h5.domain.user.parent.dto.response;

import com.h5.domain.user.parent.dto.response.info.ConsultantInfo;
import com.h5.domain.user.parent.dto.response.info.MyChildInfo;
import com.h5.domain.user.parent.dto.response.info.MyInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MyPageResponse {
    private List<MyChildInfo> myChildren;
    private MyInfo myInfo;
    private ConsultantInfo consultantInfo;
}


