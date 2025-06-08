package com.h5.domain.board.qna.controller;

import com.h5.domain.board.qna.dto.request.QnaCommentIssueRequest;
import com.h5.domain.board.qna.dto.request.QnaCommentUpdateRequest;
import com.h5.domain.board.qna.dto.response.QnaCommentResponse;
import com.h5.domain.board.qna.service.QnaCommentService;
import com.h5.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/qna/comments")
@Tag(name = "QnA Comments API", description = "QnA 댓글 생성·수정·삭제 API")
public class QnaCommentController {

    private final QnaCommentService qnaCommentService;

    @Operation(
            summary = "댓글 작성",
            description = "특정 QnA 게시글에 댓글을 작성합니다."
    )
    @PostMapping
    public ResultResponse<QnaCommentResponse> createComment(
            @RequestBody QnaCommentIssueRequest dto
    ) {
        return ResultResponse.created(qnaCommentService.issueQnaComment(dto));
    }

    @Operation(
            summary = "댓글 수정",
            description = "기존 댓글의 내용을 수정합니다."
    )
    @PutMapping("/{id}")
    public ResultResponse<QnaCommentResponse> updateComment(
            @PathVariable Integer id,
            @RequestBody QnaCommentUpdateRequest dto
    ) {
        return ResultResponse.success(qnaCommentService.updateComment(dto, id));
    }

    @Operation(
            summary = "댓글 삭제",
            description = "기존 댓글을 논리 삭제 처리합니다."
    )
    @DeleteMapping("/{id}")
    public ResultResponse<Void> deleteComment(
            @PathVariable Integer id
    ) {
        qnaCommentService.deleteComment(id);
        return ResultResponse.success();
    }
}
