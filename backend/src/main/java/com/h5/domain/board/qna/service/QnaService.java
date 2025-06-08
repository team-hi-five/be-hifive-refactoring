package com.h5.domain.board.qna.service;

import com.h5.domain.auth.service.AuthenticationService;
import com.h5.domain.board.common.service.AbstractBoardService;
import com.h5.domain.board.qna.dto.request.QnaCommentIssueRequestDto;
import com.h5.domain.board.qna.dto.request.QnaCommentUpdateRequestDto;
import com.h5.domain.board.qna.dto.request.QnaIssueRequestDto;
import com.h5.domain.board.qna.dto.request.QnaUpdateRequestDto;
import com.h5.domain.board.qna.dto.response.QnaCommentResponseDto;
import com.h5.domain.board.qna.dto.response.QnaDetailResponseDto;
import com.h5.domain.board.qna.dto.response.QnaListResponseDto;
import com.h5.domain.board.qna.dto.response.QnaSaveResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QnaService extends AbstractBoardService<
        QnaListResponseDto,
        QnaDetailResponseDto,
        QnaIssueRequestDto,
        QnaUpdateRequestDto,
        QnaSaveResponseDto> {

    private final AuthenticationService authenticationService;

    @Override
    @Transactional(readOnly = true)
    public QnaListResponseDto findAll(String title, String writer, Integer page, Integer size) {
        Integer centerId = authenticationService.getCurrentUserCenterId();

        

        return null;
    }

    @Override
    public QnaDetailResponseDto findById(Integer id) {
        return null;
    }

    @Override
    public QnaSaveResponseDto issue(QnaIssueRequestDto dto) {
        return null;
    }

    @Override
    public QnaSaveResponseDto update(QnaUpdateRequestDto dto) {
        return null;
    }

    @Override
    public void delete(Integer id) {

    }

    public QnaCommentResponseDto createQnaComment(QnaCommentIssueRequestDto dto) {
        return null;
    }

    public QnaCommentResponseDto updateComment(QnaCommentUpdateRequestDto dto, String id) {
        return null;
    }

    public void deleteComment(Integer id) {
    }
}
