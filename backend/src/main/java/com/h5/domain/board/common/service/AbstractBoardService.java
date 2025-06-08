package com.h5.domain.board.common.service;

/**
 * 게시판 계열 서비스의 공통 CRUD 메서드를 정의한 추상 클래스.
 *
 * @param <ListResponse>   리스트 응답 DTO
 * @param <DetailResponse> 상세 조회 응답 DTO
 * @param <IssueRequest>>  생성 요청 DTO
 * @param <UpdateRequest>  수정 요청 DTO
 * @param <SaveResponse>   생성/수정 응답 DTO
 */
public abstract class AbstractBoardService<
        ListResponse,
        DetailResponse,
        IssueRequest,
        UpdateRequest,
        SaveResponse> {

    public abstract ListResponse findAll(String title, String writer, Integer page, Integer size);

    public abstract DetailResponse findById(Integer id);

    public abstract SaveResponse issue(IssueRequest dto);

    public abstract SaveResponse update(UpdateRequest dto);

    public abstract void delete(Integer id);

}
