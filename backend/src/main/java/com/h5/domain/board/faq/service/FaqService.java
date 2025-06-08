package com.h5.domain.board.faq.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.domain.auth.service.AuthenticationService;
import com.h5.domain.board.common.service.AbstractBoardService;
import com.h5.domain.board.faq.dto.request.FaqIssueRequest;
import com.h5.domain.board.faq.dto.request.FaqUpdateRequest;
import com.h5.domain.board.faq.dto.response.FaqDetailResponse;
import com.h5.domain.board.faq.dto.response.FaqListResponse;
import com.h5.domain.board.faq.dto.response.FaqSaveResponse;
import com.h5.domain.board.faq.entity.FaqEntity;
import com.h5.domain.board.faq.mapper.FaqMapper;
import com.h5.domain.board.faq.repository.FaqRepository;
import com.h5.domain.user.consultant.entity.ConsultantUserEntity;
import com.h5.domain.user.consultant.service.ConsultantUserService;
import com.h5.global.exception.DomainErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class FaqService extends AbstractBoardService<
        FaqListResponse,
        FaqDetailResponse,
        FaqIssueRequest,
        FaqUpdateRequest,
        FaqSaveResponse> {

    private final FaqRepository faqRepository;
    private final FaqMapper faqMapper;
    private final AuthenticationService authenticationService;
    private final ConsultantUserService consultantUserService;

    /**
     * FAQ 목록 조회 (페이징, 검색 포함).
     *
     * @param title  제목 필터 (null/빈 문자열일 경우 전체)
     * @param writer 작성자 필터 (null/빈 문자열일 경우 전체)
     * @param page   페이지 번호 (0부터, null일 경우 0)
     * @param size   페이지 크기 (null이거나 ≤ 0일 경우 10)
     * @return 페이징된 FAQ 리스트 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public FaqListResponse findAll(String title, String writer, Integer page, Integer size) {
        Integer centerId = authenticationService.getCurrentUserCenterId();

        Pageable pageable = PageRequest.of(
                page != null && page >= 0 ? page : 0,
                size != null && size > 0 ? size : 10,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<FaqEntity> faqPage;
        if (title != null && !title.isBlank()) {
            faqPage = faqRepository
                    .findByConsultantUser_Center_IdAndTitleContainingAndDeletedAtIsNull(
                            centerId, title, pageable
                    );
        } else if (writer != null && !writer.isBlank()) {
            faqPage = faqRepository
                    .findByConsultantUser_Center_IdAndConsultantUser_NameContainingAndDeletedAtIsNull(
                            centerId, writer, pageable
                    );
        } else {
            faqPage = faqRepository
                    .findAllByConsultantUser_Center_IdAndDeletedAtIsNull(
                            centerId, pageable
                    );
        }

        return faqMapper.toListResponse(faqPage);
    }

    /**
     * FAQ 상세 조회.
     *
     * @param id 조회할 FAQ ID
     * @return FAQ 상세 DTO
     * @throws BusinessException 존재하지 않거나 접근 권한이 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public FaqDetailResponse findById(Integer id) {
        Integer centerId = authenticationService.getCurrentUserCenterId();

        FaqEntity entity = faqRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.BOARD_NOT_FOUND));

        if (!Objects.equals(entity.getConsultantUser().getCenter().getId(), centerId)) {
            throw new BusinessException(DomainErrorCode.BOARD_ACCESS_DENY);
        }

        return faqMapper.toDetailResponse(entity);
    }

    /**
     * 새로운 FAQ 등록.
     *
     * @param dto FAQ 등록 요청 DTO
     * @return 저장된 FAQ ID DTO
     */
    @Override
    public FaqSaveResponse issue(FaqIssueRequest dto) {
        String email = authenticationService.getCurrentUserEmail();
        ConsultantUserEntity consultant = consultantUserService.findByEmailOrThrow(email);

        FaqEntity entity = FaqEntity.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .consultantUser(consultant)
                .type(dto.getType())
                .build();

        Integer savedId = faqRepository.save(entity).getId();
        return FaqSaveResponse.builder().id(savedId).build();
    }

    /**
     * 기존 FAQ 수정.
     *
     * @param dto FAQ 수정 요청 DTO
     * @return 수정된 FAQ ID DTO
     * @throws BusinessException 존재하지 않거나 소유자 불일치 시
     */
    @Override
    public FaqSaveResponse update(FaqUpdateRequest dto) {
        FaqEntity entity = faqRepository
                .findByIdAndDeletedAtIsNull(dto.getId())
                .orElseThrow(() -> new BusinessException(DomainErrorCode.BOARD_NOT_FOUND));

        if (!entity.getConsultantUser().getEmail()
                .equals(authenticationService.getCurrentUserEmail())) {
            throw new BusinessException(DomainErrorCode.BOARD_ACCESS_DENY);
        }

        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        Integer updatedId = faqRepository.save(entity).getId();

        return FaqSaveResponse.builder().id(updatedId).build();
    }

    /**
     * FAQ 논리 삭제.
     *
     * @param id 삭제할 FAQ ID
     * @throws BusinessException 존재하지 않거나 소유자 불일치 시
     */
    @Override
    public void delete(Integer id) {
        FaqEntity entity = faqRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.BOARD_NOT_FOUND));

        if (!entity.getConsultantUser().getEmail()
                .equals(authenticationService.getCurrentUserEmail())) {
            throw new BusinessException(DomainErrorCode.BOARD_ACCESS_DENY);
        }

        entity.setDeletedAt(LocalDateTime.now());
        faqRepository.save(entity);
    }
}
