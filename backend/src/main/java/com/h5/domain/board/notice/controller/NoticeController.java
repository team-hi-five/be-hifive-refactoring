package com.h5.domain.board.notice.controller;

import com.h5.domain.board.common.controller.AbstractBoardController;
import com.h5.domain.board.notice.dto.request.NoticeIssueRequestDto;
import com.h5.domain.board.notice.dto.request.NoticeUpdateRequestDto;
import com.h5.domain.board.notice.dto.response.NoticeDetailResponseDto;
import com.h5.domain.board.notice.dto.response.NoticeListResponseDto;
import com.h5.domain.board.notice.dto.response.NoticeSaveResponseDto;
import com.h5.domain.board.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/notice")
@Tag(name = "공지사항 API", description = "공지사항 관련 API")
public class NoticeController extends AbstractBoardController<
        NoticeListResponseDto,
        NoticeDetailResponseDto,
        NoticeIssueRequestDto,
        NoticeUpdateRequestDto,
        NoticeSaveResponseDto,
        NoticeService> {

    public NoticeController(NoticeService noticeService) {
        super(noticeService);
    }
}
