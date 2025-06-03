package com.h5.domain.child.mapper;

import com.h5.domain.child.entity.ChildUserEntity;
import com.h5.domain.consultant.dto.response.GetChildResponse;
import com.h5.domain.consultant.dto.response.GetMyChildrenResponse;
import com.h5.domain.child.dto.response.SearchChildResponse;
import com.h5.domain.file.entity.FileEntity;
import com.h5.global.helper.FileUrlHelper;
import com.h5.global.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Consultant 도메인에서 엔티티를 응답 DTO로 변환하는 Mapper 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class ChildMapper {

    private final FileUrlHelper fileUrlHelper;

    /**
     * ChildUserEntity → GetMyChildrenResponse 매핑
     *
     * @param child 자녀 엔티티
     * @return GetMyChildrenResponse DTO
     */
    public GetMyChildrenResponse toMyChildrenDto(ChildUserEntity child) {
        return GetMyChildrenResponse.builder()
                .childUserId(child.getId())
                .profileImgUrl(
                        fileUrlHelper.getProfileUrlOrDefault(FileEntity.TblType.PCD, child.getId()))
                .childName(child.getName())
                .birth(child.getBirth())
                .age(DateUtil.calculateAge(child.getBirth()))
                .parentName(child.getParentUserEntity().getName())
                .build();
    }

    /**
     * ChildUserEntity → GetChildResponse 매핑
     *
     * @param child 자녀 엔티티
     * @return GetChildResponse DTO
     */
    public GetChildResponse toChildDetailDto(ChildUserEntity child) {
        return GetChildResponse.builder()
                .childUserId(child.getId())
                .profileImgUrl(
                        fileUrlHelper.getProfileUrlOrDefault(FileEntity.TblType.PCD, child.getId()))
                .childName(child.getName())
                .age(DateUtil.calculateAge(child.getBirth()))
                .birth(child.getBirth())
                .gender(child.getGender())
                .firstConsultDate(child.getFirstConsultDt())
                .interest(child.getInterest())
                .additionalInfo(child.getAdditionalInfo())
                .parentName(child.getParentUserEntity().getName())
                .parentPhone(child.getParentUserEntity().getPhone())
                .parentEmail(child.getParentUserEntity().getEmail())
                .build();
    }

    /**
     * ChildUserEntity → SearchChildResponse 매핑
     *
     * @param child 자녀 엔티티
     * @return SearchChildResponse DTO
     */
    public SearchChildResponse toSearchChildDto(ChildUserEntity child) {
        return SearchChildResponse.builder()
                .childUserId(child.getId())
                .childProfileUrl(
                        fileUrlHelper.getProfileUrlOrDefault(FileEntity.TblType.PCD, child.getId()))
                .childUserName(child.getName())
                .parentUserName(child.getParentUserEntity().getName())
                .parentUserEmail(child.getParentUserEntity().getEmail())
                .build();
    }
}
