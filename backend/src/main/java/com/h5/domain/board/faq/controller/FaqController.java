package com.h5.domain.board.faq.controller;

import com.h5.domain.board.common.controller.AbstractBoardController;
import com.h5.domain.board.faq.dto.request.FaqIssueRequestDto;
import com.h5.domain.board.faq.dto.request.FaqUpdateRequestDto;
import com.h5.domain.board.faq.dto.response.FaqDetailResponseDto;
import com.h5.domain.board.faq.dto.response.FaqListResponseDto;
import com.h5.domain.board.faq.dto.response.FaqSaveResponseDto;
import com.h5.domain.board.faq.service.FaqService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/faq")
@Tag(name = "FAQ API", description = "FAQ 관련 API")
public class FaqController extends AbstractBoardController<
        FaqListResponseDto,
        FaqDetailResponseDto,
        FaqIssueRequestDto,
        FaqUpdateRequestDto,
        FaqSaveResponseDto,
        FaqService> {

    public FaqController(FaqService service) {
        super(service);
    }

}
