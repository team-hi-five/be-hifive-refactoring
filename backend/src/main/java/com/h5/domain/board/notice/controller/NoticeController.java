package com.h5.domain.board.notice.controller;

import com.h5.domain.board.common.controller.AbstractBoardController;
import com.h5.domain.board.notice.dto.request.NoticeIssueRequest;
import com.h5.domain.board.notice.dto.request.NoticeUpdateRequest;
import com.h5.domain.board.notice.dto.response.NoticeDetailResponse;
import com.h5.domain.board.notice.dto.response.NoticeListResponse;
import com.h5.domain.board.notice.dto.response.NoticeSaveResponse;
import com.h5.domain.board.notice.service.NoticeService;
import com.h5.global.dto.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/notice")
@Tag(name = "공지사항 API", description = "공지사항 관련 API")
public class NoticeController extends AbstractBoardController<
        NoticeListResponse,
        NoticeDetailResponse,
        NoticeIssueRequest,
        NoticeUpdateRequest,
        NoticeSaveResponse,
        NoticeService> {

    public NoticeController(NoticeService noticeService) {
        super(noticeService);
    }

    @Override
    @Operation(
            summary = "목록 조회",
            description = "제목, 작성자, 페이지, 사이즈 파라미터에 따라 페이징된 목록을 조회합니다."
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResultResponse<NoticeListResponse> findAll(
            @RequestParam String title,
            @RequestParam String writer,
            @RequestParam Integer page,
            @RequestParam Integer size) {
        return super.findAll(title, writer, page, size);
    }

    @Override
    @Operation(
            summary = "상세 조회",
            description = "경로 변수 ID에 해당하는 항목의 상세 정보를 반환합니다."
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResultResponse<NoticeDetailResponse> findById(@PathVariable Integer id) {
        return super.findById(id);
    }

    @Override
    @Operation(
            summary = "등록",
            description = "요청 바디의 DTO 정보를 기반으로 새 항목을 등록합니다."
    )
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    @PostMapping
    public ResultResponse<NoticeSaveResponse> issue(@RequestBody NoticeIssueRequest dto) {
        return super.issue(dto);
    }

    @Override
    @Operation(
            summary = "수정",
            description = "요청 바디의 DTO 정보로 기존 항목을 수정합니다."
    )
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    @PutMapping
    public ResultResponse<NoticeSaveResponse> update(@RequestBody NoticeUpdateRequest dto) {
        return super.update(dto);
    }

    @Override
    @Operation(
            summary = "삭제",
            description = "경로 변수 ID에 해당하는 항목을 삭제합니다."
    )
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    @DeleteMapping("/{id}")
    public ResultResponse<Void> delete(@PathVariable Integer id) {
        return super.delete(id);
    }
}
