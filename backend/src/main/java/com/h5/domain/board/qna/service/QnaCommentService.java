package com.h5.domain.board.qna.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.domain.auth.service.AuthenticationService;
import com.h5.domain.board.qna.dto.request.QnaCommentIssueRequest;
import com.h5.domain.board.qna.dto.request.QnaCommentUpdateRequest;
import com.h5.domain.board.qna.dto.response.QnaCommentResponse;
import com.h5.domain.board.qna.entity.QnaCommentEntity;
import com.h5.domain.board.qna.entity.QnaEntity;
import com.h5.domain.board.qna.mapper.QnaCommentMapper;
import com.h5.domain.board.qna.repository.QnaCommentRepository;
import com.h5.domain.board.qna.repository.QnaRepository;
import com.h5.domain.user.consultant.entity.ConsultantUserEntity;
import com.h5.domain.user.consultant.service.ConsultantUserService;
import com.h5.global.exception.DomainErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * QnA 댓글(답글) 관련 비즈니스 로직을 처리하는 서비스 클래스.
 * <p>
 * - 상담사 권한 검증 및 댓글 등록, 수정, 삭제 기능 제공
 * - 댓글 등록 시 QnA 본문의 댓글 수를 증가시키도록 QnaService와 연동
 * - 댓글 목록 조회 기능 제공
 */
@Service
@RequiredArgsConstructor
@Transactional
public class QnaCommentService {

    private final QnaCommentRepository qnaCommentRepository;
    private final QnaCommentMapper qnaCommentMapper;
    private final AuthenticationService authenticationService;
    private final ConsultantUserService consultantUserService;
    private final QnaRepository qnaRepository;

    /**
     * 새로운 QnA 댓글을 등록합니다.
     * <p>
     * - 현재 로그인된 사용자가 ROLE_CONSULTANT인지 검증
     * - QnA 본문이 존재하는지 조회, 없으면 예외 발생
     * - 댓글 수 증가를 위해 QnaService.updateCommentCount 호출
     *
     * @param dto 댓글 등록 요청 정보를 담은 {@link QnaCommentIssueRequest}
     * @return 저장된 댓글 정보를 담은 {@link QnaCommentResponse}
     * @throws BusinessException 상담사 권한이 없거나 QnA 본문이 존재하지 않을 경우
     */
    public QnaCommentResponse issueQnaComment(QnaCommentIssueRequest dto) {
        String email = authenticationService.getCurrentUserEmail();
        ConsultantUserEntity consultantUserEntity = consultantUserService.findByEmailOrThrow(email);
        QnaEntity qnaEntity = qnaRepository.findByIdAndDeletedAtIsNull(dto.getQnaId())
                .orElseThrow(() -> new BusinessException(DomainErrorCode.BOARD_NOT_FOUND));

        qnaEntity.setCommentCount(qnaEntity.getCommentCount() + 1);
        qnaRepository.save(qnaEntity);

        QnaCommentEntity qnaCommentEntity = QnaCommentEntity.builder()
                .content(dto.getContent())
                .qnaEntity(qnaEntity)
                .consultantUser(consultantUserEntity)
                .build();

        qnaCommentRepository.save(qnaCommentEntity);
        return qnaCommentMapper.toDetailResponse(qnaCommentEntity);
    }

    /**
     * 기존 댓글의 내용을 수정합니다.
     * <p>
     * - 댓글 존재 여부를 확인, 없으면 예외 발생
     * - 현재 로그인된 사용자가 댓글 작성자(상담사)인지 검증
     *
     * @param dto 수정 요청 정보를 담은 {@link QnaCommentUpdateRequest}
     * @param id  수정할 댓글의 식별자
     * @return 수정된 댓글 정보를 담은 {@link QnaCommentResponse}
     * @throws BusinessException 댓글이 없거나 접근 권한이 없을 경우
     */
    public QnaCommentResponse updateComment(QnaCommentUpdateRequest dto, Integer id) {
        String email = authenticationService.getCurrentUserEmail();
        QnaCommentEntity qnaCommentEntity = qnaCommentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.COMMENT_NOT_FOUND));

        if (!qnaCommentEntity.getConsultantUser().getEmail().equals(email)) {
            throw new BusinessException(DomainErrorCode.COMMENT_ACCESS_DENY);
        }

        qnaCommentEntity.setContent(dto.getContent());
        qnaCommentRepository.save(qnaCommentEntity);
        return qnaCommentMapper.toDetailResponse(qnaCommentEntity);
    }

    /**
     * 댓글을 논리 삭제 처리합니다.
     * <p>
     * - 댓글 존재 여부를 확인, 없으면 예외 발생
     * - 현재 로그인된 사용자가 댓글 작성자(상담사)인지 검증
     *
     * @param id 삭제할 댓글의 식별자
     * @throws BusinessException 댓글이 없거나 접근 권한이 없을 경우
     */
    public void deleteComment(Integer id) {
        String email = authenticationService.getCurrentUserEmail();
        QnaCommentEntity qnaCommentEntity = qnaCommentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.COMMENT_NOT_FOUND));

        if (!qnaCommentEntity.getConsultantUser().getEmail().equals(email)) {
            throw new BusinessException(DomainErrorCode.COMMENT_ACCESS_DENY);
        }

        qnaCommentEntity.setDeletedAt(LocalDateTime.now());
        qnaCommentRepository.save(qnaCommentEntity);
    }

    /**
     * 특정 QnA 본문에 달린 댓글 목록을 조회합니다.
     *
     * @param qnaId 댓글을 조회할 QnA 본문 식별자
     * @return 댓글 엔티티 목록 (없으면 빈 리스트)
     */
    @Transactional(readOnly = true)
    public List<QnaCommentEntity> findByQnaId(Integer qnaId) {
        return qnaCommentRepository.findAllByQnaEntity_IdAndDeletedAtIsNull(qnaId)
                .orElse(List.of());
    }
}
