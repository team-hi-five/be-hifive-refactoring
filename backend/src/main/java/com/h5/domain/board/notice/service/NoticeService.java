package com.h5.domain.board.notice.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.domain.auth.service.AuthenticationService;
import com.h5.domain.board.common.service.AbstractBoardService;
import com.h5.domain.board.notice.dto.request.NoticeIssueRequest;
import com.h5.domain.board.notice.dto.request.NoticeUpdateRequest;
import com.h5.domain.board.notice.dto.response.NoticeDetailResponse;
import com.h5.domain.board.notice.dto.response.NoticeListResponse;
import com.h5.domain.board.notice.dto.response.NoticeSaveResponse;
import com.h5.domain.board.notice.entity.NoticeEntity;
import com.h5.domain.board.notice.mapper.NoticeMapper;
import com.h5.domain.board.notice.repository.NoticeRepository;
import com.h5.domain.user.consultant.entity.ConsultantUserEntity;
import com.h5.domain.user.consultant.service.ConsultantUserService;
import com.h5.global.exception.DomainErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService extends AbstractBoardService<
        NoticeListResponse,
        NoticeDetailResponse,
        NoticeIssueRequest,
        NoticeUpdateRequest,
        NoticeSaveResponse> {

    private final NoticeRepository noticeRepository;
    private final NoticeMapper noticeMapper;
    private final AuthenticationService authenticationService;
    private final ConsultantUserService consultantUserService;

    /**
     * 공지사항 목록을 조회합니다.
     * <p>
     * 제목 또는 작성자(writer) 필터가 주어지면 해당 값을 포함하는 게시글을,
     * 없으면 전체 게시글을 페이징하여 반환합니다.
     *
     * @param title  제목 필터 (null 또는 빈 문자열이면 전체)
     * @param writer 작성자 필터 (null 또는 빈 문자열이면 전체)
     * @param page   페이지 번호 (0부터 시작, null이면 0)
     * @param size   페이지 크기 (null이거나 0 이하이면 10)
     * @return 페이징된 공지사항 리스트를 담은 {@link NoticeListResponse}
     */
    @Override
    @Transactional(readOnly = true)
    public NoticeListResponse findAll(String title, String writer, Integer page, Integer size) {
        Integer centerId = authenticationService.getCurrentUserCenterId();

        Pageable pageable = PageRequest.of(
                page != null && page >= 0 ? page : 0,
                size != null && size > 0 ? size : 10,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<NoticeEntity> noticePage;
        if (title != null && !title.isEmpty()) {
            noticePage = noticeRepository.findAllByConsultantUser_Center_IdAndTitleContainingAndDeletedAtIsNull(
                    centerId, title, pageable
            );
        } else if (writer != null && !writer.isEmpty()) {
            noticePage = noticeRepository.findAllByConsultantUser_Center_IdAndConsultantUser_NameContainingAndDeletedAtIsNull(
                    centerId, writer, pageable
            );
        } else {
            noticePage = noticeRepository.findAllByConsultantUser_Center_IdAndDeletedAtIsNull(
                    centerId, pageable
            );
        }

        return noticeMapper.toListResponse(noticePage);
    }

    /**
     * 특정 공지사항의 상세 정보를 조회하고, 조회 수를 1 증가시킵니다.
     *
     * @param id 조회할 공지사항 ID
     * @return 공지사항 상세 정보를 담은 {@link NoticeDetailResponse}
     * @throws BusinessException 게시글이 존재하지 않거나 접근 권한이 없을 경우
     */
    @Override
    public NoticeDetailResponse findById(Integer id) {
        Integer centerId = authenticationService.getCurrentUserCenterId();

        NoticeEntity noticeEntity = noticeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.BOARD_NOT_FOUND));

        if (!noticeEntity.getConsultantUser().getCenter().getId().equals(centerId)) {
            throw new BusinessException(DomainErrorCode.BOARD_ACCESS_DENY);
        }

        updateViewCount(noticeEntity);
        return noticeMapper.toDetailResponse(noticeEntity);
    }

    /**
     * 새로운 공지사항을 등록합니다.
     *
     * @param dto 공지사항 등록 요청 정보를 담은 {@link NoticeIssueRequest}
     * @return 등록된 공지사항 ID를 담은 {@link NoticeSaveResponse}
     */
    @Override
    public NoticeSaveResponse issue(NoticeIssueRequest dto) {
        String email = authenticationService.getCurrentUserEmail();
        ConsultantUserEntity consultantUserEntity = consultantUserService.findByEmailOrThrow(email);

        NoticeEntity noticeEntity = NoticeEntity.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .consultantUser(consultantUserEntity)
                .build();

        Integer savedId = noticeRepository.save(noticeEntity).getId();
        return NoticeSaveResponse.builder().noticeId(savedId).build();
    }

    /**
     * 기존 공지사항을 수정합니다.
     *
     * @param dto 수정할 공지사항 정보(ID, 제목, 내용)를 담은 {@link NoticeUpdateRequest}
     * @return 수정된 공지사항 ID를 담은 {@link NoticeSaveResponse}
     * @throws BusinessException 게시글이 없거나 소유자가 아닌 경우
     */
    @Override
    public NoticeSaveResponse update(NoticeUpdateRequest dto) {
        NoticeEntity noticeEntity = noticeRepository.findByIdAndDeletedAtIsNull(dto.getId())
                .orElseThrow(() -> new BusinessException(DomainErrorCode.BOARD_NOT_FOUND));

        if (!noticeEntity.getConsultantUser().getEmail()
                .equals(authenticationService.getCurrentUserEmail())) {
            throw new BusinessException(DomainErrorCode.BOARD_ACCESS_DENY);
        }

        noticeEntity.setTitle(dto.getTitle());
        noticeEntity.setContent(dto.getContent());
        Integer savedId = noticeRepository.save(noticeEntity).getId();

        return NoticeSaveResponse.builder().noticeId(savedId).build();
    }

    /**
     * 공지사항을 논리 삭제 처리합니다.
     *
     * @param id 삭제할 공지사항 ID
     * @throws BusinessException 게시글이 없거나 소유자가 아닌 경우
     */
    @Override
    public void delete(Integer id) {
        NoticeEntity noticeEntity = noticeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.BOARD_NOT_FOUND));

        if (!noticeEntity.getConsultantUser().getEmail()
                .equals(authenticationService.getCurrentUserEmail())) {
            throw new BusinessException(DomainErrorCode.BOARD_ACCESS_DENY);
        }

        noticeEntity.setDeletedAt(LocalDateTime.now());
        noticeRepository.save(noticeEntity);
    }

    /**
     * 조회 수를 1 증가시키고 저장합니다.
     *
     * @param noticeEntity 조회 수를 증가시킬 엔티티
     */
    private void updateViewCount(NoticeEntity noticeEntity) {
        noticeEntity.setViewCnt(noticeEntity.getViewCnt() + 1);
        noticeRepository.save(noticeEntity);
    }
}
