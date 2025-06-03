package com.h5.domain.deleterequest.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.domain.child.service.ChildUserService;
import com.h5.domain.deleterequest.dto.response.DeleteRequestResponseDto;
import com.h5.domain.deleterequest.dto.response.GetMyDeleteResponseDto;
import com.h5.domain.deleterequest.entity.DeleteUserRequestEntity;
import com.h5.domain.deleterequest.mapper.DeleteRequestMapper;
import com.h5.domain.deleterequest.repository.DeleteUserRequestRepository;
import com.h5.domain.parent.entity.ParentUserEntity;
import com.h5.domain.parent.service.ParentUserService;
import com.h5.global.exception.DomainErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@code DeleteUserRequestService} 클래스는
 * 부모 사용자의 탈퇴(삭제) 요청과 관련된 비즈니스 로직을 처리합니다.
 * <p>
 * 주요 책임:
 * <ul>
 *     <li>탈퇴 요청 생성</li>
 *     <li>탈퇴 요청 승인 처리(부모 및 자녀 삭제 처리)</li>
 *     <li>탈퇴 요청 거절 처리</li>
 *     <li>상담사별 보류(P) 상태의 탈퇴 요청 목록 조회</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DeleteUserRequestService {

    private final DeleteUserRequestRepository deleteUserRequestRepository;
    private final DeleteRequestMapper deleteRequestMapper;
    private final ParentUserService parentUserService;
    private final ChildUserService childUserService;

    /**
     * 부모 이메일로 탈퇴 요청을 생성합니다.
     * <p>
     * 이미 같은 부모 사용자에 대해 보류(P) 상태의 요청이 존재하면 {@link BusinessException}을 던집니다.
     *
     * @param parentEmail 탈퇴 요청을 생성할 부모 사용자 이메일
     * @return 생성된 탈퇴 요청 정보를 담은 {@link DeleteRequestResponseDto}
     * @throws BusinessException {@code DELETE_REQUEST_DUPLICATED} - 이미 보류 상태 탈퇴 요청이 존재할 경우
     */
    public DeleteRequestResponseDto deleteRequest(String parentEmail) {
        ParentUserEntity parentUserEntity = parentUserService.findByEmailOrThrow(parentEmail);

        Optional<DeleteUserRequestEntity> existingRequest =
                deleteUserRequestRepository.findByParentUser_IdAndStatus(
                        parentUserEntity.getId(),
                        DeleteUserRequestEntity.Status.P);

        if (existingRequest.isPresent()) {
            throw new BusinessException(DomainErrorCode.DELETE_REQUEST_DUPLICATED);
        }

        DeleteUserRequestEntity deleteUserRequest = deleteUserRequestRepository.save(
                DeleteUserRequestEntity.builder()
                        .status(DeleteUserRequestEntity.Status.P)
                        .parentUserId(parentUserEntity.getId())
                        .consultantUserId(parentUserEntity.getConsultantUserId())
                        .deleteRequestDttm(LocalDateTime.now())
                        .build());

        return DeleteRequestResponseDto.builder()
                .deleteRequestId(deleteUserRequest.getId())
                .status(deleteUserRequest.getStatus())
                .deleteRequestDttm(deleteUserRequest.getDeleteRequestDttm())
                .build();
    }

    /**
     * 탈퇴 요청을 승인하고, 해당 부모 사용자 및 자녀 사용자들을 삭제 처리합니다.
     * <p>
     * - 부모 사용자의 {@code deleteDttm}을 설정하여 삭제 처리<br>
     * - 자녀 사용자들의 {@code deleteDttm}을 일괄 업데이트<br>
     * - 탈퇴 요청 엔티티의 {@code deleteConfirmDttm}과 상태를 승인(A)으로 변경
     *
     * @param deleteUserRequestId 승인할 탈퇴 요청 식별자
     * @throws BusinessException {@code USER_NOT_FOUND} - 해당 식별자의 탈퇴 요청이 존재하지 않을 경우
     */
    public void deleteApprove(int deleteUserRequestId) {
        DeleteUserRequestEntity deleteUserRequestEntity = deleteUserRequestRepository.findById(deleteUserRequestId)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        LocalDateTime deleteDttm = LocalDateTime.now();

        parentUserService.markDeleted(deleteUserRequestEntity.getParentUserId(), deleteDttm);
        childUserService.markDeleted(deleteUserRequestEntity.getParentUserId(), deleteDttm);

        deleteUserRequestEntity.setDeleteConfirmDttm(deleteDttm);
        deleteUserRequestEntity.setStatus(DeleteUserRequestEntity.Status.A);

        deleteUserRequestRepository.save(deleteUserRequestEntity);
    }

    /**
     * 탈퇴 요청을 거절 처리합니다.
     * <p>
     * - 탈퇴 요청 엔티티의 {@code deleteConfirmDttm}을 현재 시각으로 설정<br>
     * - 탈퇴 요청 상태를 거절(R)로 변경
     *
     * @param deleteUserRequestId 거절할 탈퇴 요청 식별자
     * @throws BusinessException {@code USER_NOT_FOUND} - 해당 식별자의 탈퇴 요청이 존재하지 않을 경우
     */
    public void deleteReject(int deleteUserRequestId) {
        DeleteUserRequestEntity deleteUserRequestEntity = deleteUserRequestRepository.findById(deleteUserRequestId)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        deleteUserRequestEntity.setDeleteConfirmDttm(LocalDateTime.now());
        deleteUserRequestEntity.setStatus(DeleteUserRequestEntity.Status.R);

        deleteUserRequestRepository.save(deleteUserRequestEntity);
    }

    /**
     * 특정 상담사 이메일에 연관된 보류(P) 상태의 탈퇴 요청 목록을 조회하여 DTO 리스트로 반환합니다.
     * <p>
     * 조회된 엔티티들은 {@link DeleteRequestMapper}를 통해
     * {@link GetMyDeleteResponseDto}로 매핑됩니다.
     *
     * @param consultantEmail 조회할 상담사 이메일
     * @return {@link GetMyDeleteResponseDto} 리스트
     */
    @Transactional(readOnly = true)
    public List<GetMyDeleteResponseDto> getMyDelete(String consultantEmail) {
        List<DeleteUserRequestEntity> entities =
                deleteUserRequestRepository.findALlByStatusAndConsultantUser_Email(
                        DeleteUserRequestEntity.Status.P,
                        consultantEmail
                );

        return entities.stream()
                .map(deleteRequestMapper::toDto)
                .collect(Collectors.toList());
    }

}
