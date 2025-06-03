package com.h5.domain.consultant.mapper;

import com.h5.domain.consultant.dto.response.MyProfileResponse;
import com.h5.domain.consultant.entity.ConsultantUserEntity;
import com.h5.domain.file.entity.FileEntity;
import com.h5.global.helper.FileUrlHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultantMapper {

    private final FileUrlHelper fileUrlHelper;

    /**
     * ConsultantUserEntity 를 MyProfileResponse 로 매핑합니다.
     *
     * @param consultantUser 매핑할 ConsultantUserEntity
     * @return MyProfileResponse DTO
     */
    public MyProfileResponse toMyProfileDto(ConsultantUserEntity consultantUser) {
        return MyProfileResponse.builder()
                .profileImgUrl(
                        fileUrlHelper.getProfileUrlOrDefault(
                                FileEntity.TblType.PCT,
                                consultantUser.getId()
                        )
                )
                .name(consultantUser.getName())
                .email(consultantUser.getEmail())
                .phone(consultantUser.getPhone())
                .centerName(consultantUser.getCenter().getCenterName())
                .centerPhone(consultantUser.getCenter().getCenterContact())
                .build();
    }
}
