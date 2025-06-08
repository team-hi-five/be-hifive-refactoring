package com.h5.domain.board.qna.controller;

import com.h5.domain.board.common.controller.AbstractBoardController;
import com.h5.domain.board.qna.dto.request.QnaCommentIssueRequestDto;
import com.h5.domain.board.qna.dto.request.QnaCommentUpdateRequestDto;
import com.h5.domain.board.qna.dto.request.QnaIssueRequestDto;
import com.h5.domain.board.qna.dto.request.QnaUpdateRequestDto;
import com.h5.domain.board.qna.dto.response.QnaCommentResponseDto;
import com.h5.domain.board.qna.dto.response.QnaDetailResponseDto;
import com.h5.domain.board.qna.dto.response.QnaListResponseDto;
import com.h5.domain.board.qna.dto.response.QnaSaveResponseDto;
import com.h5.domain.board.qna.service.QnaService;
import com.h5.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/qna")
@Tag(name = "QnA API", description = "QnA 관련 API")
public class QnaController extends AbstractBoardController<
        QnaListResponseDto,
        QnaDetailResponseDto,
        QnaIssueRequestDto,
        QnaUpdateRequestDto,
        QnaSaveResponseDto,
        QnaService> {

    protected QnaController(QnaService service) {
        super(service);
    }

    @PostMapping("/comments")
    public ResultResponse<QnaCommentResponseDto> createComment(@RequestBody QnaCommentIssueRequestDto qnaCommentCreateRequestDto) {
        return ResultResponse.created(service.createQnaComment(qnaCommentCreateRequestDto));
    }

    @PutMapping("/comments/{id}")
    public ResultResponse<QnaCommentResponseDto> updateComment(@RequestBody QnaCommentUpdateRequestDto dto, @PathVariable String id) {
        return ResultResponse.success(service.updateComment(dto, id));
    }

    @DeleteMapping("/comments/{id}")
    public ResultResponse<Void> deleteComment(@PathVariable Integer id) {
        service.deleteComment(id);
        return ResultResponse.success();
    }
}