package com.h5.domain.board.qna.controller;

import com.h5.domain.board.common.controller.AbstractBoardController;
import com.h5.domain.board.qna.dto.request.QnaIssueRequest;
import com.h5.domain.board.qna.dto.request.QnaUpdateRequest;
import com.h5.domain.board.qna.dto.response.QnaDetailResponse;
import com.h5.domain.board.qna.dto.response.QnaListResponse;
import com.h5.domain.board.qna.dto.response.QnaSaveResponse;
import com.h5.domain.board.qna.service.QnaService;
import com.h5.global.dto.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/qna")
@Tag(name = "QnA API", description = "QnA 관련 API")
public class QnaController extends AbstractBoardController<
        QnaListResponse,
        QnaDetailResponse,
        QnaIssueRequest,
        QnaUpdateRequest,
        QnaSaveResponse,
        QnaService> {

    protected QnaController(QnaService service) {
        super(service);
    }

    @Override
    @Operation(
            summary = "목록 조회",
            description = "제목, 작성자, 페이지, 사이즈 파라미터에 따라 페이징된 목록을 조회합니다."
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResultResponse<QnaListResponse> findAll(
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
    public ResultResponse<QnaDetailResponse> findById(@PathVariable Integer id) {
        return super.findById(id);
    }

    @Override
    @Operation(
            summary = "등록",
            description = "요청 바디의 DTO 정보를 기반으로 새 항목을 등록합니다."
    )
    @PreAuthorize("hasAuthority('ROLE_PARENT')")
    @PostMapping
    public ResultResponse<QnaSaveResponse> issue(@RequestBody QnaIssueRequest dto) {
        return super.issue(dto);
    }

    @Override
    @Operation(
            summary = "수정",
            description = "요청 바디의 DTO 정보로 기존 항목을 수정합니다."
    )
    @PreAuthorize("hasAuthority('ROLE_PARENT')")
    @PutMapping
    public ResultResponse<QnaSaveResponse> update(@RequestBody QnaUpdateRequest dto) {
        return super.update(dto);
    }

    @Override
    @Operation(
            summary = "삭제",
            description = "경로 변수 ID에 해당하는 항목을 삭제합니다."
    )
    @PreAuthorize("hasAuthority('ROLE_PARENT')")
    @DeleteMapping("/{id}")
    public ResultResponse<Void> delete(@PathVariable Integer id) {
        return super.delete(id);
    }
}