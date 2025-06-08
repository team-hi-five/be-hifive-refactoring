package com.h5.domain.board.qna.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.domain.auth.service.AuthenticationService;
import com.h5.domain.board.common.service.AbstractBoardService;
import com.h5.domain.board.qna.dto.request.QnaIssueRequest;
import com.h5.domain.board.qna.dto.request.QnaUpdateRequest;
import com.h5.domain.board.qna.dto.response.QnaDetailResponse;
import com.h5.domain.board.qna.dto.response.QnaListResponse;
import com.h5.domain.board.qna.dto.response.QnaSaveResponse;
import com.h5.domain.board.qna.entity.QnaCommentEntity;
import com.h5.domain.board.qna.entity.QnaEntity;
import com.h5.domain.board.qna.mapper.QnaMapper;
import com.h5.domain.board.qna.repository.QnaRepository;
import com.h5.domain.user.consultant.service.ConsultantUserService;
import com.h5.domain.user.parent.entity.ParentUserEntity;
import com.h5.domain.user.parent.service.ParentUserService;
import com.h5.global.exception.DomainErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * QnA 게시판의 주요 비즈니스 로직을 처리하는 서비스 클래스.
 * <p>
 * - ROLE_PARENT, ROLE_CONSULTANT 에 따라 조회 범위를 구분합니다.
 * - 페이징, 제목/작성자 검색을 지원합니다.
 * - 상세 조회 시 댓글 목록도 함께 조회합니다.
 * - 작성, 수정, 삭제 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class QnaService extends AbstractBoardService<
        QnaListResponse,
        QnaDetailResponse,
        QnaIssueRequest,
        QnaUpdateRequest,
        QnaSaveResponse> {

    private final AuthenticationService authenticationService;
    private final ConsultantUserService consultantUserService;
    private final ParentUserService parentUserService;
    private final QnaRepository qnaRepository;
    private final QnaMapper qnaMapper;
    private final QnaCommentService qnaCommentService;

    /**
     * QnA 목록을 조회합니다.
     * <p>
     * - title 또는 writer 파라미터가 주어지면 해당 조건에 맞는 게시글을, 없으면 전체를 조회합니다.
     * - ROLE_CONSULTANT인 경우 해당 상담사의 부모가 작성한 게시글을,
     *   그 외(ROLE_PARENT)에는 본인이 작성한 게시글만 조회합니다.
     *
     * @param title  제목 검색 키워드 (null 또는 빈 문자열이면 무시)
     * @param writer 작성자 검색 키워드 (null 또는 빈 문자열이면 무시)
     * @param page   페이지 번호 (0부터 시작, null이면 0)
     * @param size   페이지 크기 (null이거나 0 이하이면 10)
     * @return 페이징된 QnA 목록을 담은 {@link QnaListResponse}
     */
    @Override
    @Transactional(readOnly = true)
    public QnaListResponse findAll(String title, String writer, Integer page, Integer size) {
        boolean isConsultant = "ROLE_CONSULTANT".equals(authenticationService.getCurrentUserRole());
        String email = authenticationService.getCurrentUserEmail();
        Integer filterId = isConsultant
                ? consultantUserService.findByEmailOrThrow(email).getId()
                : parentUserService.findByEmailOrThrow(email).getId();

        Pageable pageable = PageRequest.of(
                page != null && page >= 0 ? page : 0,
                size != null && size > 0 ? size : 10,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        boolean hasTitle  = title  != null && !title.trim().isEmpty();
        boolean hasWriter = writer != null && !writer.trim().isEmpty();

        Page<QnaEntity> qnaPage;
        if (hasTitle) {
            String t = title.trim();
            qnaPage = isConsultant
                    ? qnaRepository.findAllByParentUser_ConsultantUserEntity_IdAndTitleContainingAndDeletedAtIsNull(filterId, t, pageable)
                    : qnaRepository.findAllByParentUser_IdAndTitleContainingAndDeletedAtIsNull(filterId, t, pageable);
        } else if (hasWriter) {
            String w = writer.trim();
            qnaPage = isConsultant
                    ? qnaRepository.findAllByParentUser_ConsultantUserEntity_IdAndParentUser_NameContainingAndDeletedAtIsNull(filterId, w, pageable)
                    : qnaRepository.findAllByParentUser_IdAndParentUser_NameContainingAndDeletedAtIsNull(filterId, w, pageable);
        } else {
            qnaPage = isConsultant
                    ? qnaRepository.findAllByParentUser_ConsultantUserEntity_IdAndDeletedAtIsNull(filterId, pageable)
                    : qnaRepository.findAllByParentUser_IdAndDeletedAtIsNull(filterId, pageable);
        }

        return qnaMapper.toListResponse(qnaPage);
    }

    /**
     * QnA 상세 정보를 조회하고, 해당 게시글의 댓글 목록을 함께 가져옵니다.
     * <p>
     * - 삭제된 게시글은 조회할 수 없습니다.
     * - ROLE_CONSULTANT인 경우 상담사의 부모가 작성한 게시글만 조회 가능하며,
     *   ROLE_PARENT인 경우 본인이 작성한 게시글만 조회 가능합니다.
     *
     * @param id 조회할 QnA 게시글의 ID
     * @return 댓글 목록을 포함한 {@link QnaDetailResponse}
     * @throws BusinessException 게시글이 없거나 접근 권한이 없을 경우
     */
    @Override
    @Transactional(readOnly = true)
    public QnaDetailResponse findById(Integer id) {
        QnaEntity qnaEntity = qnaRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.BOARD_NOT_FOUND));

        String role = authenticationService.getCurrentUserRole();
        String email = authenticationService.getCurrentUserEmail();
        boolean authorized = "ROLE_CONSULTANT".equals(role)
                ? qnaEntity.getParentUser().getConsultantUserEntity().getEmail().equals(email)
                : qnaEntity.getParentUser().getEmail().equals(email);
        if (!authorized) {
            throw new BusinessException(DomainErrorCode.BOARD_ACCESS_DENY);
        }

        List<QnaCommentEntity> qnaCommentList = qnaCommentService.findByQnaId(qnaEntity.getId());

        return qnaMapper.toDetailResponse(qnaEntity, qnaCommentList);
    }

    /**
     * 새로운 QnA 게시글을 등록합니다.
     *
     * @param dto 등록 요청 정보를 담은 {@link QnaIssueRequest}
     * @return 저장된 게시글 ID를 담은 {@link QnaSaveResponse}
     */
    @Override
    public QnaSaveResponse issue(QnaIssueRequest dto) {
        ParentUserEntity parentUserEntity = parentUserService.findByEmailOrThrow(
                authenticationService.getCurrentUserEmail());

        QnaEntity qnaEntity = QnaEntity.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .parentUser(parentUserEntity)
                .build();

        Integer qnaId = qnaRepository.save(qnaEntity).getId();
        return QnaSaveResponse.builder().qnaId(qnaId).build();
    }

    /**
     * 기존 QnA 게시글을 수정합니다.
     *
     * @param dto 수정 요청 정보를 담은 {@link QnaUpdateRequest}
     * @return 수정된 게시글 ID를 담은 {@link QnaSaveResponse}
     * @throws BusinessException 게시글이 없거나 소유자가 아닐 경우
     */
    @Override
    public QnaSaveResponse update(QnaUpdateRequest dto) {
        QnaEntity qnaEntity = qnaRepository.findByIdAndDeletedAtIsNull(dto.getId())
                .orElseThrow(() -> new BusinessException(DomainErrorCode.BOARD_NOT_FOUND));

        if (!qnaEntity.getParentUser().getEmail()
                .equals(authenticationService.getCurrentUserEmail())) {
            throw new BusinessException(DomainErrorCode.BOARD_ACCESS_DENY);
        }

        qnaEntity.setTitle(dto.getTitle());
        qnaEntity.setContent(dto.getContent());
        Integer qnaId = qnaRepository.save(qnaEntity).getId();

        return QnaSaveResponse.builder().qnaId(qnaId).build();
    }

    /**
     * QnA 게시글을 논리 삭제 처리합니다.
     *
     * @param id 삭제할 게시글의 ID
     * @throws BusinessException 게시글이 없거나 소유자가 아닐 경우
     */
    @Override
    public void delete(Integer id) {
        QnaEntity qnaEntity = qnaRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.BOARD_NOT_FOUND));

        if (!qnaEntity.getParentUser().getEmail()
                .equals(authenticationService.getCurrentUserEmail())) {
            throw new BusinessException(DomainErrorCode.BOARD_ACCESS_DENY);
        }

        qnaEntity.setDeletedAt(LocalDateTime.now());
        qnaRepository.save(qnaEntity);
    }

    /**
     * QnA 엔티티를 조회합니다.
     * <p>
     * 주어진 QnA ID로 DB에서 엔티티를 검색하고, 존재하지 않으면 BOARD_NOT_FOUND 예외를 던집니다.
     *
     * @param qnaId 조회할 QnA의 식별자
     * @return 조회된 {@link QnaEntity}
     * @throws BusinessException QnA가 존재하지 않을 경우 {@link DomainErrorCode#BOARD_NOT_FOUND}
     */
    @Transactional(readOnly = true)
    public QnaEntity findQnaEntityByQnaIdOrThrow(Integer qnaId) {
        return qnaRepository.findById(qnaId)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.BOARD_NOT_FOUND));
    }

    /**
     * QnA 엔티티의 댓글 수를 1 증가시키고 저장합니다.
     * <p>
     * 주어진 {@link QnaEntity}의 commentCount 필드를 1 증가시킨 뒤,
     * 변경된 엔티티를 DB에 저장합니다.
     *
     * @param qnaEntity 댓글 수를 업데이트할 {@link QnaEntity}
     */
    public void updateCommentCount(QnaEntity qnaEntity) {
        qnaEntity.setCommentCount(qnaEntity.getCommentCount() + 1);
        qnaRepository.save(qnaEntity);
    }
}
