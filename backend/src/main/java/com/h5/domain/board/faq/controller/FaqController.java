package com.h5.domain.board.faq.controller;

import com.h5.domain.board.common.controller.AbstractBoardController;
import com.h5.domain.board.faq.dto.request.FaqIssueRequest;
import com.h5.domain.board.faq.dto.request.FaqUpdateRequest;
import com.h5.domain.board.faq.dto.response.FaqDetailResponse;
import com.h5.domain.board.faq.dto.response.FaqListResponse;
import com.h5.domain.board.faq.dto.response.FaqSaveResponse;
import com.h5.domain.board.faq.service.FaqService;
import com.h5.global.dto.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/faq")
@Tag(name = "FAQ API", description = "FAQ 관련 API")
public class FaqController extends AbstractBoardController<
        FaqListResponse,
        FaqDetailResponse,
        FaqIssueRequest,
        FaqUpdateRequest,
        FaqSaveResponse,
        FaqService> {

    public FaqController(FaqService service) {
        super(service);
    }

    @Override
    @Operation(
            summary = "목록 조회",
            description = "제목, 작성자, 페이지, 사이즈 파라미터에 따라 페이징된 목록을 조회합니다."
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResultResponse<FaqListResponse> findAll(
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
    public ResultResponse<FaqDetailResponse> findById(@PathVariable Integer id) {
        return super.findById(id);
    }

    @Override
    @Operation(
            summary = "등록",
            description = "요청 바디의 DTO 정보를 기반으로 새 항목을 등록합니다."
    )
    @PreAuthorize("hasAuthority('ROLE_CONSUNTANT')")
    @PostMapping
    public ResultResponse<FaqSaveResponse> issue(@RequestBody FaqIssueRequest dto) {
        return super.issue(dto);
    }

    @Override
    @Operation(
            summary = "수정",
            description = "요청 바디의 DTO 정보로 기존 항목을 수정합니다."
    )
    @PreAuthorize("hasAuthority('ROLE_CONSUNTANT')")
    @PutMapping
    public ResultResponse<FaqSaveResponse> update(@RequestBody FaqUpdateRequest dto) {
        return super.update(dto);
    }

    @Override
    @Operation(
            summary = "삭제",
            description = "경로 변수 ID에 해당하는 항목을 삭제합니다."
    )
    @PreAuthorize("hasAuthority('ROLE_CONSUNTANT')")
    @DeleteMapping("/{id}")
    public ResultResponse<Void> delete(@PathVariable Integer id) {
        return super.delete(id);
    }
}
