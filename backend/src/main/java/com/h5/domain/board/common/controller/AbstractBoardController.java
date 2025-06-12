package com.h5.domain.board.common.controller;

import com.h5.domain.board.common.service.AbstractBoardService;
import com.h5.global.dto.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 게시판 공통 CRUD 컨트롤러 추상 클래스.
 *
 * @param <ListResponse>   목록 조회 응답 DTO
 * @param <DetailResponse> 상세 조회 응답 DTO
 * @param <IssueRequest>   등록 요청 DTO
 * @param <UpdateRequest>  수정 요청 DTO
 * @param <SaveResponse>   저장(등록/수정) 응답 DTO
 * @param <Service>        사용 서비스 타입
 */
@Tag(name = "Board API", description = "모든 게시판 공통 CRUD API")
public abstract class AbstractBoardController<
        ListResponse,
        DetailResponse,
        IssueRequest,
        UpdateRequest,
        SaveResponse,
        Service extends AbstractBoardService<
                ListResponse,
                DetailResponse,
                IssueRequest,
                UpdateRequest,
                SaveResponse>> {

    protected final Service service;

    protected AbstractBoardController(final Service service) {
        this.service = service;
    }

    @Operation(
            summary = "목록 조회",
            description = "제목, 작성자, 페이지, 사이즈 파라미터에 따라 페이징된 목록을 조회합니다."
    )
    @GetMapping
    public ResultResponse<ListResponse> findAll(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String writer,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return ResultResponse.success(service.findAll(title, writer, page, size));
    }

    @Operation(
            summary = "상세 조회",
            description = "경로 변수 ID에 해당하는 항목의 상세 정보를 반환합니다."
    )
    @GetMapping("/{id}")
    public ResultResponse<DetailResponse> findById(
            @PathVariable Integer id
    ) {
        return ResultResponse.success(service.findById(id));
    }

    @Operation(
            summary = "등록",
            description = "요청 바디의 DTO 정보를 기반으로 새 항목을 등록합니다."
    )
    @PostMapping
    public ResultResponse<SaveResponse> issue(
            @RequestBody IssueRequest dto
    ) {
        return ResultResponse.created(service.issue(dto));
    }

    @Operation(
            summary = "수정",
            description = "요청 바디의 DTO 정보로 기존 항목을 수정합니다."
    )
    @PutMapping
    public ResultResponse<SaveResponse> update(
            @RequestBody UpdateRequest dto
    ) {
        return ResultResponse.success(service.update(dto));
    }

    @Operation(
            summary = "삭제",
            description = "경로 변수 ID에 해당하는 항목을 삭제합니다."
    )
    @DeleteMapping("/{id}")
    public ResultResponse<Void> delete(
            @PathVariable Integer id
    ) {
        service.delete(id);
        return ResultResponse.success();
    }
}
