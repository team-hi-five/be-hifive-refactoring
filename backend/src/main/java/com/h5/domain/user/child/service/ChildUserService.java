package com.h5.domain.user.child.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.domain.user.child.dto.request.UpdateChildStageRequest;
import com.h5.domain.user.child.dto.response.UpdateChildStageResponse;
import com.h5.domain.user.child.entity.ChildUserEntity;
import com.h5.domain.user.child.mapper.ChildMapper;
import com.h5.domain.user.child.repository.ChildUserRepository;
import com.h5.domain.user.child.dto.request.ModifyChildRequest;
import com.h5.domain.user.consultant.dto.request.RegisterParentAccount;
import com.h5.domain.user.consultant.dto.response.GetChildResponse;
import com.h5.domain.user.consultant.dto.response.GetMyChildrenResponse;
import com.h5.domain.user.child.dto.response.ModifyChildResponse;
import com.h5.domain.user.child.dto.response.SearchChildResponse;
import com.h5.global.exception.DomainErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 자녀(Child) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 *
 * <p>주요 기능:
 * <ul>
 *     <li>새 자녀 정보 등록</li>
 *     <li>상담사 별 자녀 목록 조회</li>
 *     <li>자녀 상세 정보 조회</li>
 *     <li>자녀 정보 수정</li>
 *     <li>이름을 기준으로 자녀 검색</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ChildUserService {

    private final ChildUserRepository childUserRepository;
    private final ChildMapper childMapper;

    /**
     * 부모 계정 등록 과정에서 전달받은 DTO, 부모 ID, 상담사 ID를 기반으로
     * 새로운 자녀(ChildUserEntity)를 생성하고 저장합니다.
     *
     * @param dto               {@link RegisterParentAccount} 등록 요청 데이터를 담은 DTO
     * @param parentUserId      학부모(Parent) 엔티티의 ID
     * @param consultantUserId  상담사(Consultant) 엔티티의 ID
     * @return 생성된 자녀 엔티티의 식별자(ID)
     */
    public Integer issueChild(@Valid RegisterParentAccount dto,
                              Integer parentUserId,
                              Integer consultantUserId) {
        ChildUserEntity childUser = ChildUserEntity.builder()
                .name(dto.getChildName())
                .birth(LocalDate.parse(dto.getChildBirth()))
                .gender(dto.getChildGender())
                .firstConsultDt(LocalDate.parse(dto.getFirstConsultDt()))
                .interest(dto.getChildInterest())
                .additionalInfo(dto.getChildAdditionalInfo())
                .parentUserId(parentUserId)
                .consultantUserId(consultantUserId)
                .build();

        childUser = childUserRepository.save(childUser);
        return childUser.getId();
    }

    /**
     * 특정 상담사(consultantUserId)에 소속된 자녀 목록을 조회하고,
     * 각 자녀 정보를 {@link GetMyChildrenResponse} 형태로 변환하여 반환합니다.
     *
     * @param consultantUserId  상담사 ID
     * @return {@link GetMyChildrenResponse} 리스트 (자녀 ID, 프로필 URL, 이름, 생년월일, 나이, 부모 이름)
     */
    public List<GetMyChildrenResponse> getChildrenByConsultant(Integer consultantUserId) {
        List<ChildUserEntity> childUserEntityList = childUserRepository
                .findByConsultantUserEntity_IdAndDeletedAtIsNull(consultantUserId)
                .orElse(List.of());

        return childUserEntityList.stream()
                .map(childMapper::toMyChildrenDto)
                .collect(Collectors.toList());
    }

    /**
     * 자녀 ID와 상담사 ID를 기준으로 자녀 엔티티를 조회하고 상세 정보를 반환합니다.
     *
     * @param childUserId       조회할 자녀의 ID
     * @param consultantUserId  상담사 ID (검증용)
     * @return {@link GetChildResponse} 자녀 상세 정보를 담은 응답 DTO
     * @throws BusinessException 자녀가 존재하지 않거나 상담사-자녀 매핑이 일치하지 않을 경우 발생
     */
    public GetChildResponse getChildDetail(@Valid int childUserId,
                                           Integer consultantUserId) {
        ChildUserEntity childUser = childUserRepository
                .findByIdAndConsultantUserEntity_IdAndDeletedAtIsNull(
                        childUserId, consultantUserId)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        return childMapper.toChildDetailDto(childUser);
    }

    /**
     * 주어진 자녀 ID를 기반으로 자녀 엔티티를 조회한 뒤,
     * 요청 DTO의 관심사 및 추가 정보를 업데이트하고 저장합니다.
     *
     * @param dto           {@link ModifyChildRequest} 자녀 수정 요청 DTO
     * @param childUserId   수정 대상 자녀의 ID
     * @return {@link ModifyChildResponse} 수정된 자녀 ID를 담은 응답 DTO
     * @throws BusinessException 자녀가 존재하지 않을 경우 발생
     */
    public ModifyChildResponse updateChild(@Valid ModifyChildRequest dto,
                                           Integer childUserId) {
        ChildUserEntity childUser = childUserRepository.findById(childUserId)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        childUser.setInterest(dto.getInterest());
        childUser.setAdditionalInfo(dto.getAdditionalInfo());
        childUserRepository.save(childUser);

        return ModifyChildResponse.builder()
                .childUserId(childUser.getId())
                .build();
    }

    /**
     * 이름(name)을 포함하는 자녀 목록을 검색하고,
     * 각 검색 결과를 {@link SearchChildResponse} 형태로 매핑하여 반환합니다.
     *
     * @param name  검색할 자녀 이름 키워드
     * @return {@link SearchChildResponse} 리스트 (자녀 ID, 프로필 URL, 자녀 이름, 부모 이름, 부모 이메일)
     */
    public List<SearchChildResponse> searchChildByName(@Valid String name) {
        List<ChildUserEntity> childUserEntityList = childUserRepository
                .findALlByNameContainingAndDeletedAtIsNull(name)
                .orElse(List.of());

        return childUserEntityList.stream()
                .map(childMapper::toSearchChildDto)
                .collect(Collectors.toList());
    }

    /**
     * 논리 삭제 메서드
     *
     * @param parentUserId 부모 일련번호
     * @param deleteDttm 삭제 시간
     */
    public void markDeleted(Integer parentUserId, LocalDateTime deleteDttm) {
        childUserRepository.updateChildUserDeletedAtByParentUserEntity_Id(deleteDttm, parentUserId);

    }

    /**
     * 자녀의 학습 단계를 업데이트합니다.
     * <p>
     * 요청된 챕터(chapter)와 스테이지(stage)를 기반으로 전체 클리어 단계를 계산하고,
     * 기존에 클리어된 단계(clearChapter)가 요청 단계 이상인 경우에는 stage=0을 반환합니다.
     * 그렇지 않으면 계산된 단계로 clearChapter를 갱신하고, 해당 단계를 반환합니다.
     *
     * @param childUserId              업데이트할 자녀 사용자의 ID
     * @param updateChildStageRequest  챕터와 스테이지 정보를 담은 요청 DTO
     * @return {@link UpdateChildStageResponse}
     *         - stage 필드에 갱신된 clearChapter 값을 담아 반환
     *         - 이미 해당 단계 이상을 클리어한 경우 stage=0 반환
     * @throws BusinessException       지정한 childUserId에 해당하는 사용자를 찾지 못한 경우
     */
    public UpdateChildStageResponse updateChildStage(
            Integer childUserId,
            UpdateChildStageRequest updateChildStageRequest) {

        int chapter = updateChildStageRequest.getChapter();
        int stage = updateChildStageRequest.getStage();

        ChildUserEntity childUserEntity = childUserRepository.findById(childUserId)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));
        int nowCleared = childUserEntity.getClearChapter();

        int cleared = (chapter - 1) * 5 + stage;

        if (nowCleared >= cleared) {
            return UpdateChildStageResponse.builder()
                    .stage(0)
                    .build();
        }

        childUserEntity.setClearChapter(cleared);
        childUserRepository.save(childUserEntity);

        return UpdateChildStageResponse.builder()
                .stage(cleared)
                .build();
    }

    /**
     * 주어진 ID에 해당하는 자녀 사용자 엔티티를 조회하고, 존재하지 않거나 삭제된 경우 예외를 던집니다.
     *
     * @param id 조회할 자녀 사용자 ID
     * @return 조회된 {@link ChildUserEntity}
     * @throws BusinessException {@link DomainErrorCode#USER_NOT_FOUND}
     *         조회된 엔티티가 없거나 삭제된(id에 해당하며 deleteDttm이 null이 아님) 경우 발생
     */
    public ChildUserEntity findByIdOrThrow(Integer id) {
        return childUserRepository
                .findNameByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));
    }

}
