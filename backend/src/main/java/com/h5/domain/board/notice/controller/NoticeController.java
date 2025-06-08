package com.h5.domain.board.notice.controller;

import com.h5.domain.board.common.controller.AbstractBoardController;
import com.h5.domain.board.notice.dto.request.NoticeIssueRequest;
import com.h5.domain.board.notice.dto.request.NoticeUpdateRequest;
import com.h5.domain.board.notice.dto.response.NoticeDetailResponse;
import com.h5.domain.board.notice.dto.response.NoticeListResponse;
import com.h5.domain.board.notice.dto.response.NoticeSaveResponse;
import com.h5.domain.board.notice.service.NoticeService;
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
}
