package com.h5.domain.deleterequest.mapper;

import com.h5.domain.deleterequest.dto.response.GetMyDeleteChildResponse;
import com.h5.domain.deleterequest.dto.response.GetMyDeleteResponse;
import com.h5.domain.deleterequest.entity.DeleteUserRequestEntity;
import com.h5.domain.file.entity.TblType;
import com.h5.domain.user.child.entity.ChildUserEntity;
import com.h5.domain.file.entity.FileEntity;
import com.h5.global.file.FileUrlHelper;
import com.h5.global.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@code DeleteRequestMapper} 클래스는 탈퇴 요청 엔티티를
 * 클라이언트에 반환할 DTO 객체로 변환하는 역할을 수행합니다.
 * <p>
 * 주요 메서드:
 * <ul>
 *     <li>{@link #toDto(DeleteUserRequestEntity)}: {@link DeleteUserRequestEntity}를
 *         {@link GetMyDeleteResponse}로 매핑</li>
 *     <li>{@link #toChildDto(ChildUserEntity)}: {@link ChildUserEntity}를
 *         {@link GetMyDeleteChildResponse}로 매핑</li>
 * </ul>
 * </p>
 */
@Component
@RequiredArgsConstructor
public class DeleteRequestMapper {

    private final FileUrlHelper fileUrlHelper;

    /**
     * 삭제 요청 엔티티를 {@link GetMyDeleteResponse}로 매핑합니다.
     * 이 메서드는 해당 요청에 연결된 부모 사용자의 자녀 목록을 함께 매핑합니다.
     *
     * @param requestEntity 삭제 요청 정보를 담고 있는 {@link DeleteUserRequestEntity}
     * @return 매핑된 {@link GetMyDeleteResponse} 객체
     */
    public GetMyDeleteResponse toDto(DeleteUserRequestEntity requestEntity) {
        Set<GetMyDeleteChildResponse> childDtos = requestEntity.getParentUser()
                .getChildUserEntities()
                .stream()
                .map(this::toChildDto)
                .collect(Collectors.toSet());

        return GetMyDeleteResponse.builder()
                .deleteUserRequestId(requestEntity.getId())
                .deleteRequestedAt(requestEntity.getDeleteRequestedAt())
                .parentUserId(requestEntity.getParentUser().getId())
                .parentName(requestEntity.getParentUser().getName())
                .children(childDtos)
                .build();
    }

    /**
     * 자녀 사용자 엔티티를 {@link GetMyDeleteChildResponse}로 매핑합니다.
     * 프로필 URL 생성 및 나이 계산 등 부가적인 로직을 포함합니다.
     *
     * @param child 매핑할 {@link ChildUserEntity} 엔티티
     * @return 매핑된 {@link GetMyDeleteChildResponse} 객체
     */
    private GetMyDeleteChildResponse toChildDto(ChildUserEntity child) {
        return GetMyDeleteChildResponse.builder()
                .childUserId(child.getId())
                .childName(child.getName())
                .childUserProfileUrl(
                        fileUrlHelper.getProfileUrlOrDefault(TblType.PCD, child.getId())
                )
                .gender(child.getGender())
                .age(DateUtil.calculateAge(child.getBirth()))
                .parentUserPhone(child.getParentUserEntity().getPhone())
                .parentUserName(child.getParentUserEntity().getName())
                .birth(child.getBirth())
                .parentUserEmail(child.getParentUserEntity().getEmail())
                .firConsultedAt(child.getFirstConsultDt())
                .interest(child.getInterest())
                .additionalInfo(child.getAdditionalInfo())
                .build();
    }
}
