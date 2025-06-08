package com.h5.domain.board.qna.controller;

import com.h5.domain.board.common.controller.AbstractBoardController;
import com.h5.domain.board.qna.dto.request.QnaIssueRequest;
import com.h5.domain.board.qna.dto.request.QnaUpdateRequest;
import com.h5.domain.board.qna.dto.response.QnaDetailResponse;
import com.h5.domain.board.qna.dto.response.QnaListResponse;
import com.h5.domain.board.qna.dto.response.QnaSaveResponse;
import com.h5.domain.board.qna.service.QnaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
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

}