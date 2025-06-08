package com.h5.domain.user.parent.dto.response;

import com.h5.domain.user.parent.dto.info.ConsultantInfo;
import com.h5.domain.user.parent.dto.info.MyChildInfo;
import com.h5.domain.user.parent.dto.info.MyInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MyPageResponseDto {
    private List<MyChildInfo> myChildren;
    private MyInfo myInfo;
    private ConsultantInfo consultantInfo;
}


