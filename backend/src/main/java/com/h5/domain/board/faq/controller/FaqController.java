package com.h5.domain.board.faq.controller;

import com.h5.domain.board.common.controller.AbstractBoardController;
import com.h5.domain.board.faq.dto.request.FaqIssueRequest;
import com.h5.domain.board.faq.dto.request.FaqUpdateRequest;
import com.h5.domain.board.faq.dto.response.FaqDetailResponse;
import com.h5.domain.board.faq.dto.response.FaqListResponse;
import com.h5.domain.board.faq.dto.response.FaqSaveResponse;
import com.h5.domain.board.faq.service.FaqService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
