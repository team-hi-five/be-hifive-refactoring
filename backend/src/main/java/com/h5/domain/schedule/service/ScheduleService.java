package com.h5.domain.schedule.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.domain.auth.service.AuthenticationService;
import com.h5.domain.schedule.dto.request.ScheduleIssueRequest;
import com.h5.domain.schedule.dto.request.ScheduleUpdateRequest;
import com.h5.domain.schedule.dto.response.IssueScheduleResponse;
import com.h5.domain.schedule.dto.response.ScheduleDatesResponse;
import com.h5.domain.schedule.dto.response.ScheduleResponse;
import com.h5.domain.schedule.entity.ConsultMeetingScheduleEntity;
import com.h5.domain.schedule.entity.GameMeetingScheduleEntity;
import com.h5.domain.schedule.repository.ConsultMeetingScheduleRepository;
import com.h5.domain.schedule.repository.GameMeetingScheduleRepository;
import com.h5.domain.user.child.entity.ChildUserEntity;
import com.h5.domain.user.child.service.ChildUserService;
import com.h5.domain.user.consultant.entity.ConsultantUserEntity;
import com.h5.domain.user.consultant.service.ConsultantUserService;
import com.h5.domain.user.parent.entity.ParentUserEntity;
import com.h5.domain.user.parent.service.ParentUserService;
import com.h5.global.exception.DomainErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.*;

/**
 * 스케줄 관리 비즈니스 로직을 처리하는 서비스 클래스.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

    private final ConsultMeetingScheduleRepository consultMeetingScheduleRepository;
    private final GameMeetingScheduleRepository gameMeetingScheduleRepository;
    private final AuthenticationService authenticationService;
    private final ConsultantUserService consultantUserService;
    private final ChildUserService childUserService;
    private final ParentUserService parentUserService;

    /**
     * 로그인한 상담사의 특정 날짜별 모든 스케줄 조회.
     *
     * @param date 조회할 날짜
     * @return 예약된 스케줄 목록 (시간순 정렬)
     */
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getSchedulesByDate(LocalDate date) {
        int consultantId = consultantUserService
                .findByEmailOrThrow(authenticationService.getCurrentUserEmail())
                .getId();

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = date.plusDays(1).atStartOfDay();

        List<ScheduleResponse> consults = consultMeetingScheduleRepository
                .findAllByHost_IdAndScheduleAtBetweenAndDeletedAtIsNull(consultantId, start, end)
                .stream()
                .map(this::mapConsult)
                .toList();

        List<ScheduleResponse> games = gameMeetingScheduleRepository
                .findAllByHost_IdAndScheduleAtBetweenAndDeletedAtIsNull(consultantId, start, end)
                .stream()
                .map(this::mapGame)
                .toList();

        return Stream.concat(consults.stream(), games.stream())
                .sorted(Comparator.comparing(ScheduleResponse::getScheduleAt))
                .collect(Collectors.toList());
    }

    /**
     * 특정 어린이 사용자의 연·월별 예약 일자 목록 조회.
     *
     * @param childUserId 어린이 사용자 ID
     * @param year        조회 연도
     * @param month       조회 월 (1~12)
     * @return 해당 월의 예약 일시 목록
     */
    @Transactional(readOnly = true)
    public ScheduleDatesResponse getScheduleDatesByChildUserId(Integer childUserId, Integer year, Integer month) {
        Month m = Month.of(month);
        List<LocalDateTime> dates = Stream.concat(
                        consultMeetingScheduleRepository
                                .findAllByChildUserEntity_IdAndScheduleAt_YearAndScheduleAt_MonthAndDeletedAtIsNull(childUserId, year, m)
                                .stream()
                                .map(ConsultMeetingScheduleEntity::getScheduleAt),
                        gameMeetingScheduleRepository
                                .findAllByChildUserEntity_IdAndScheduleAt_YearAndScheduleAt_MonthAndDeletedAtIsNull(childUserId, year, m)
                                .stream()
                                .map(GameMeetingScheduleEntity::getScheduleAt)
                )
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        return ScheduleDatesResponse.builder()
                .dateList(dates)
                .build();
    }

    /**
     * 특정 어린이 사용자의 연·월별 상세 스케줄 조회.
     *
     * @param childUserId 어린이 사용자 ID
     * @param year        조회 연도
     * @param month       조회 월 (1~12)
     * @return 해당 월의 상세 스케줄 목록 (시간순 정렬)
     */
    public List<ScheduleResponse> getSchedulesByChildUserId(Integer childUserId, Integer year, Integer month) {
        Month m = Month.of(month);

        List<ScheduleResponse> consults = consultMeetingScheduleRepository
                .findAllByChildUserEntity_IdAndScheduleAt_YearAndScheduleAt_MonthAndDeletedAtIsNull(childUserId, year, m)
                .stream()
                .map(this::mapConsult)
                .toList();

        List<ScheduleResponse> games = gameMeetingScheduleRepository
                .findAllByChildUserEntity_IdAndScheduleAt_YearAndScheduleAt_MonthAndDeletedAtIsNull(childUserId, year, m)
                .stream()
                .map(this::mapGame)
                .toList();

        return Stream.concat(consults.stream(), games.stream())
                .sorted(Comparator.comparing(ScheduleResponse::getScheduleAt))
                .collect(Collectors.toList());
    }

    /**
     * 로그인한 상담사의 특정 날짜에 가능한 시간(9시~17시) 조회.
     *
     * @param date 조회할 날짜
     * @return 예약되지 않은 시간 리스트
     */
    public List<LocalTime> getAvailableTimes(LocalDate date) {
        int consultantId = consultantUserService
                .findByEmailOrThrow(authenticationService.getCurrentUserEmail())
                .getId();

        LocalDateTime start = date.atTime(9, 0);
        LocalDateTime end   = date.atTime(18, 0);

        Set<LocalTime> booked = Stream.concat(
                        consultMeetingScheduleRepository
                                .findAllByHost_IdAndScheduleAtBetweenAndDeletedAtIsNull(consultantId, start, end)
                                .stream()
                                .map(ConsultMeetingScheduleEntity::getScheduleAt)
                                .map(LocalDateTime::toLocalTime),
                        gameMeetingScheduleRepository
                                .findAllByHost_IdAndScheduleAtBetweenAndDeletedAtIsNull(consultantId, start, end)
                                .stream()
                                .map(GameMeetingScheduleEntity::getScheduleAt)
                                .map(LocalDateTime::toLocalTime)
                )
                .collect(Collectors.toSet());

        return IntStream.rangeClosed(9, 17)
                .mapToObj(hour -> LocalTime.of(hour, 0))
                .filter(time -> !booked.contains(time))
                .collect(Collectors.toList());
    }

    /**
     * 새로운 스케줄(consult 또는 game) 생성.
     *
     * @param scheduleIssueRequest 생성 요청 DTO
     * @return 생성된 스케줄 ID
     */
    public IssueScheduleResponse issueSchedule(ScheduleIssueRequest scheduleIssueRequest) {
        String email = authenticationService.getCurrentUserEmail();
        int consultantId = consultantUserService.findByEmailOrThrow(email).getId();

        ensureNotBooked(scheduleIssueRequest.getType(), consultantId, scheduleIssueRequest.getScheduleAt());

        ChildUserEntity child = childUserService.findByIdOrThrow(scheduleIssueRequest.getChildId());
        ParentUserEntity parent = child.getParentUserEntity();
        ConsultantUserEntity host = parent.getConsultantUserEntity();

        Integer id;
        if ("consult".equals(scheduleIssueRequest.getType())) {
            ConsultMeetingScheduleEntity entity = ConsultMeetingScheduleEntity.builder()
                    .host(host)
                    .childUserEntity(child)
                    .parentUserEntity(parent)
                    .scheduleAt(scheduleIssueRequest.getScheduleAt())
                    .status("P")
                    .build();
            id = consultMeetingScheduleRepository.save(entity).getId();
        } else {
            GameMeetingScheduleEntity entity = GameMeetingScheduleEntity.builder()
                    .host(host)
                    .childUserEntity(child)
                    .scheduleAt(scheduleIssueRequest.getScheduleAt())
                    .status("P")
                    .build();
            id = gameMeetingScheduleRepository.save(entity).getId();
        }

        return IssueScheduleResponse.builder()
                .scheduleId(id)
                .build();
    }

    /**
     * 기존 스케줄 일시 및 대상 어린이 변경.
     *
     * @param id            수정할 스케줄 ID
     * @param childUserId   새 어린이 사용자 ID
     * @param dto           수정 요청 DTO
     * @return 수정된 스케줄 ID
     */
    public IssueScheduleResponse updateSchedule(Integer id, Integer childUserId, ScheduleUpdateRequest dto) {
        String email = authenticationService.getCurrentUserEmail();
        int consultantId = consultantUserService.findByEmailOrThrow(email).getId();

        ensureNotBooked(dto.getType(), consultantId, dto.getScheduleAt());

        ChildUserEntity child = childUserService.findByIdOrThrow(childUserId);

        if ("consult".equals(dto.getType())) {
            ConsultMeetingScheduleEntity entity = consultMeetingScheduleRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(DomainErrorCode.SCHEDULE_NOT_FOUND));
            entity.setScheduleAt(dto.getScheduleAt());
            entity.setChildUserEntity(child);
            consultMeetingScheduleRepository.save(entity);
        } else {
            GameMeetingScheduleEntity entity = gameMeetingScheduleRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(DomainErrorCode.SCHEDULE_NOT_FOUND));
            entity.setScheduleAt(dto.getScheduleAt());
            entity.setChildUserEntity(child);
            gameMeetingScheduleRepository.save(entity);
        }

        return IssueScheduleResponse.builder()
                .scheduleId(id)
                .build();
    }

    /**
     * 스케줄을 soft delete 처리.
     *
     * @param type "consult" 또는 "game"
     * @param id   삭제할 스케줄 ID
     */
    public void deleteSchedule(String type, Integer id) {
        if ("consult".equals(type)) {
            ConsultMeetingScheduleEntity entity = consultMeetingScheduleRepository
                    .findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new BusinessException(DomainErrorCode.SCHEDULE_NOT_FOUND));
            entity.setDeletedAt(LocalDateTime.now());
            consultMeetingScheduleRepository.save(entity);

        } else if ("game".equals(type)) {
            GameMeetingScheduleEntity entity = gameMeetingScheduleRepository
                    .findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new BusinessException(DomainErrorCode.SCHEDULE_NOT_FOUND));
            entity.setDeletedAt(LocalDateTime.now());
            gameMeetingScheduleRepository.save(entity);

        } else {
            throw new BusinessException(DomainErrorCode.INVALID_SCHEDULE_TYPE);
        }
    }

    /**
     * 로그인한 부모 사용자의 모든 자녀에 대한 연·월별 상세 스케줄 조회.
     *
     * @param year  조회 연도
     * @param month 조회 월 (1~12)
     * @return 해당 월의 전체 스케줄 목록 (시간순 정렬)
     */
    public List<ScheduleResponse> getSchedulesByParentUserId(Integer year, Integer month) {
        ParentUserEntity parent = parentUserService.findByEmailOrThrow(authenticationService.getCurrentUserEmail());
        Month m = Month.of(month);

        List<ScheduleResponse> consults = consultMeetingScheduleRepository
                .findAllByChildUserEntity_IdInAndScheduleAt_YearAndScheduleAt_MonthAndDeletedAtIsNull(
                        parent.getChildUserEntities().stream().map(ChildUserEntity::getId).toList(),
                        year, m)
                .stream()
                .map(this::mapConsult)
                .toList();

        List<ScheduleResponse> games = gameMeetingScheduleRepository
                .findAllByChildUserEntity_IdInAndScheduleAt_YearAndScheduleAt_MonthAndDeletedAtIsNull(
                        parent.getChildUserEntities().stream().map(ChildUserEntity::getId).toList(),
                        year, m)
                .stream()
                .map(this::mapGame)
                .toList();

        return Stream.concat(consults.stream(), games.stream())
                .sorted(Comparator.comparing(ScheduleResponse::getScheduleAt))
                .collect(Collectors.toList());
    }

    /**
     * 로그인한 부모 사용자의 연·월별 예약 일자 목록 조회.
     *
     * @param year  조회 연도
     * @param month 조회 월 (1~12)
     * @return 해당 월의 예약 일시 목록
     */
    public ScheduleDatesResponse getScheduleDatesByParentUserId(Integer year, Integer month) {
        ParentUserEntity parent = parentUserService.findByEmailOrThrow(authenticationService.getCurrentUserEmail());
        Month m = Month.of(month);

        List<LocalDateTime> dates = Stream.concat(
                        consultMeetingScheduleRepository
                                .findAllByChildUserEntity_IdInAndScheduleAt_YearAndScheduleAt_MonthAndDeletedAtIsNull(
                                        parent.getChildUserEntities().stream().map(ChildUserEntity::getId).toList(), year, m)
                                .stream()
                                .map(ConsultMeetingScheduleEntity::getScheduleAt),
                        gameMeetingScheduleRepository
                                .findAllByChildUserEntity_IdInAndScheduleAt_YearAndScheduleAt_MonthAndDeletedAtIsNull(
                                        parent.getChildUserEntities().stream().map(ChildUserEntity::getId).toList(), year, m)
                                .stream()
                                .map(GameMeetingScheduleEntity::getScheduleAt)
                )
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        return ScheduleDatesResponse.builder()
                .dateList(dates)
                .build();
    }

    private void ensureNotBooked(String type, int consultantId, LocalDateTime at) {
        boolean conflict = switch (type) {
            case "consult" -> consultMeetingScheduleRepository.existsByHost_IdAndScheduleAtAndDeletedAtIsNull(consultantId, at);
            case "game"    -> gameMeetingScheduleRepository.existsByHost_IdAndScheduleAtAndDeletedAtIsNull(consultantId, at);
            default        -> throw new BusinessException(DomainErrorCode.INVALID_SCHEDULE_TYPE);
        };
        if (conflict) {
            throw new BusinessException(DomainErrorCode.SCHEDULE_CONFLICT);
        }
    }

    private ScheduleResponse mapConsult(ConsultMeetingScheduleEntity e) {
        return ScheduleResponse.builder()
                .scheduleId(e.getId())
                .scheduleAt(e.getScheduleAt())
                .type("consult")
                .consultantName(e.getHost().getName())
                .childUserId(e.getChildUserEntity().getId())
                .childName(e.getChildUserEntity().getName())
                .parentName(e.getParentUserEntity().getName())
                .parentEmail(e.getParentUserEntity().getEmail())
                .status(e.getStatus())
                .build();
    }

    private ScheduleResponse mapGame(GameMeetingScheduleEntity e) {
        var child = e.getChildUserEntity();
        return ScheduleResponse.builder()
                .scheduleId(e.getId())
                .scheduleAt(e.getScheduleAt())
                .type("game")
                .consultantName(e.getHost().getName())
                .childUserId(child.getId())
                .childName(child.getName())
                .parentName(child.getParentUserEntity().getName())
                .parentEmail(child.getParentUserEntity().getEmail())
                .status(e.getStatus())
                .build();
    }
}
