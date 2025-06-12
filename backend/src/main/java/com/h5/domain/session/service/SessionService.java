package com.h5.domain.session.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.domain.schedule.entity.BaseMeetingSchedule;
import com.h5.domain.schedule.repository.ConsultMeetingScheduleRepository;
import com.h5.domain.schedule.repository.GameMeetingScheduleRepository;
import com.h5.domain.session.dto.request.CloseSessionRequest;
import com.h5.domain.session.dto.request.JoinSessionRequest;
import com.h5.domain.session.dto.response.JoinSessionResponse;
import com.h5.domain.session.repository.ConsultSessionRepository;
import com.h5.domain.session.repository.GameSessionRepository;
import com.h5.global.enumerate.Status;
import com.h5.global.exception.DomainErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 세션 참여 및 생성, 종료 로직을 처리하는 서비스 클래스입니다.
 * <p>
 * - joinOrCreateMeeting: 주어진 스케줄 ID와 타입에 따라 세션에 참여하거나 세션을 새로 생성합니다.
 * - endMeeting: 주어진 스케줄 ID와 타입에 해당하는 회의를 종료 처리합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SessionService {
    private final GameMeetingScheduleRepository gameMeetingScheduleRepository;
    private final OpenViduService openViduService;
    private final GameSessionRepository gameSessionRepository;
    private final ConsultMeetingScheduleRepository consultMeetingScheduleRepository;
    private final ConsultSessionRepository consultSessionRepository;

    /**
     * 지정된 스케줄에 대해 세션에 참여하거나, 세션이 없는 경우 새로 생성합니다.
     *
     * @param joinSessionRequest 스케줄 ID와 회의 유형(type)을 포함한 요청 DTO
     * @return {@link JoinSessionResponse} 생성된 또는 참여된 세션의 연결 ID 정보를 담은 응답
     * @throws BusinessException 스케줄이 없거나 타입이 유효하지 않거나, 시작 전/종료된 스케줄인 경우
     */
    public JoinSessionResponse joinOrCreateMeeting(JoinSessionRequest joinSessionRequest) {
        int scheduleId = joinSessionRequest.getScheduleId();
        String type = joinSessionRequest.getType();

        String sessionId = switch (type) {
            case "game" -> handleMeeting(
                    () -> gameMeetingScheduleRepository.findById(scheduleId),
                    gameSessionRepository::save
            );
            case "consult" -> handleMeeting(
                    () -> consultMeetingScheduleRepository.findById(scheduleId),
                    consultSessionRepository::save
            );
            default -> throw new BusinessException(DomainErrorCode.INVALID_SCHEDULE_TYPE);
        };

        String connectionId = openViduService.createConnection(sessionId);
        return JoinSessionResponse.builder()
                .sessionId(connectionId)
                .build();
    }

    /**
     * 지정된 스케줄에 대해 회의를 종료 처리합니다.
     *
     * @param dto 스케줄 ID와 회의 유형(type)을 포함한 종료 요청 DTO
     * @throws BusinessException 스케줄이 없거나 타입이 유효하지 않거나, 이미 종료된 스케줄인 경우
     */
    public void endMeeting(CloseSessionRequest dto) {
        String type = dto.getType();
        int scheduleId = dto.getScheduleId();

        switch (type) {
            case "game" -> handleEnd(
                    () -> gameMeetingScheduleRepository.findById(scheduleId),
                    gameSessionRepository::save
            );
            case "consult" -> handleEnd(
                    () -> consultMeetingScheduleRepository.findById(scheduleId),
                    consultSessionRepository::save
            );
            default -> throw new BusinessException(DomainErrorCode.INVALID_SCHEDULE_TYPE);
        }
    }

    /**
     * 세션 참여 또는 생성 처리 공통 로직.
     * <p>
     * 1. 스케줄 조회
     * 2. 시작 시간 전인지 검증, 종료 시간 지났는지 검증
     * 3. 세션이 없으면 OpenVidu 세션 생성 및 엔티티 저장
     * </p>
     *
     * @param finder      스케줄 조회 함수
     * @param entitySaver 변경된 엔티티를 저장할 저장소 함수
     * @param <T>         {@link BaseMeetingSchedule} 구현 타입
     * @return 생성되거나 기존의 세션 ID
     */
    private <T extends BaseMeetingSchedule> String handleMeeting(
            Supplier<Optional<T>> finder,
            Consumer<T> entitySaver
    ) {
        T schedule = finder.get()
                .orElseThrow(() -> new BusinessException(DomainErrorCode.SCHEDULE_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        if (schedule.getStartAt().isAfter(now)) {
            throw new BusinessException(DomainErrorCode.SCHEDULE_NOT_STARTED);
        }
        if (schedule.getEndAt().isBefore(now)) {
            throw new BusinessException(DomainErrorCode.SCHEDULE_OVER_TIME);
        }

        if (schedule.getSessionId() == null) {
            String sessionId = openViduService.createSession();
            schedule.setSessionId(sessionId);
            schedule.setStatus(Status.APPROVED);
            entitySaver.accept(schedule);
            return sessionId;
        }

        return schedule.getSessionId();
    }

    /**
     * 회의 종료 처리 공통 로직.
     * <p>
     * 1. 스케줄 조회
     * 2. 이미 종료 상태인지 검증
     * 3. 상태를 END로 변경하고 종료 시간 설정 후 저장
     * </p>
     *
     * @param finder 스케줄 조회 함수
     * @param saver  변경된 엔티티를 저장할 저장소 함수
     * @param <T>    {@link BaseMeetingSchedule} 구현 타입
     */
    private <T extends BaseMeetingSchedule> void handleEnd(
            Supplier<Optional<T>> finder,
            Consumer<T> saver
    ) {
        T meeting = finder.get()
                .orElseThrow(() -> new BusinessException(DomainErrorCode.SCHEDULE_NOT_FOUND));

        if (Status.END.equals(meeting.getStatus())) {
            throw new BusinessException(DomainErrorCode.SCHEDULE_OVER_TIME);
        }

        meeting.setStatus(Status.END);
        meeting.setEndAt(LocalDateTime.now());
        saver.accept(meeting);
    }
}
