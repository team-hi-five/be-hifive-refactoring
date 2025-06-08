package com.h5.domain.session.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.domain.schedule.entity.ConsultMeetingScheduleEntity;
import com.h5.domain.schedule.entity.GameMeetingScheduleEntity;
import com.h5.domain.schedule.repository.ConsultMeetingScheduleRepository;
import com.h5.domain.schedule.repository.GameMeetingScheduleRepository;
import com.h5.domain.session.dto.request.CloseSessionRequestDto;
import com.h5.domain.session.dto.request.JoinSessionRequestDto;
import com.h5.domain.session.repository.ConsultSessionRepository;
import com.h5.domain.session.repository.GameSessionRepository;
import com.h5.global.exception.DomainErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionService {
    private final GameMeetingScheduleRepository gameMeetingScheduleRepository;
    private final OpenViduService openViduService;
    private final GameSessionRepository gameSessionRepository;
    private final ConsultMeetingScheduleRepository consultMeetingScheduleRepository;
    private final ConsultSessionRepository consultSessionRepository;

    private String startMeeting(String type, int scheduleId) {

        if("game".equals(type)){
            GameMeetingScheduleEntity gameMeetingScheduleEntity = gameMeetingScheduleRepository.findById(scheduleId)
                    .orElseThrow(()-> new BusinessException(DomainErrorCode.SCHEDULE_NOT_FOUND));

            if(gameMeetingScheduleEntity.getDeletedAt() != null){
                throw new BusinessException(DomainErrorCode.SCHEDULE_OVER_TIME);
            }

            String sessionId = openViduService.createSession();
            gameSessionRepository.updateSessionId(gameMeetingScheduleEntity.getId(), sessionId);

            return sessionId;

        } else if ("consult".equals(type)) {
            ConsultMeetingScheduleEntity consultMeetingScheduleEntity = consultMeetingScheduleRepository.findById(scheduleId)
                    .orElseThrow(()-> new BusinessException(DomainErrorCode.SCHEDULE_NOT_FOUND));

            if(consultMeetingScheduleEntity.getSessionId() != null) {
                throw new IllegalArgumentException("Meeting already started");
            }
            if(consultMeetingScheduleEntity.getDeletedAt() != null){
                throw new IllegalArgumentException("Meeting already ended");
            }

            String sessionId = openViduService.createSession();
            consultSessionRepository.updateSessionId(consultMeetingScheduleEntity.getId(), sessionId);

            return sessionId;

        }else{
            throw new RuntimeException("wrong type");
        }
    }

    public String joinMeeting(JoinSessionRequestDto joinSessionRequestDto) {
        int childId = joinSessionRequestDto.getChildId();
        String type = joinSessionRequestDto.getType();

        LocalDateTime currentDttm = LocalDateTime.now();

        if ("game".equals(type)) {
            GameMeetingScheduleEntity gameMeetingScheduleEntity = gameMeetingScheduleRepository.findNowSchedulesByChildId(childId, currentDttm)
                    .orElseThrow(ScheduleNotFoundException::new);
            int scheduleId = gameMeetingScheduleEntity.getId();

            String sessionId;
            if(gameMeetingScheduleEntity.getSessionId() == null) {
                sessionId = startMeeting(type, scheduleId);
            }else{
                sessionId = gameMeetingScheduleEntity.getSessionId();
            }

            return openViduService.createConnection(sessionId);

        }else if("consult".equals(type)) {
            ConsultMeetingScheduleEntity consultMeetingScheduleEntity = consultMeetingScheduleRepository.findNowSchedulesByChildId(childId, currentDttm)
                    .orElseThrow(ScheduleNotFoundException::new);
            int scheduleId = consultMeetingScheduleEntity.getId();

            String sessionId;
            if(consultMeetingScheduleEntity.getSessionId() == null) {
                sessionId = startMeeting(type, scheduleId);
            }else{
                sessionId = consultMeetingScheduleEntity.getSessionId();
            }

            return openViduService.createConnection(sessionId);

        }else{
            throw new RuntimeException("wrong type");
        }
    }

    public void endMeeting(CloseSessionRequestDto closeSessionRequestDto) {
        String type = closeSessionRequestDto.getType();
        int schdlId = closeSessionRequestDto.getSchdlId();

        if ("game".equals(type)) {
            GameMeetingScheduleEntity gameMeeting = gameMeetingScheduleRepository.findById(schdlId)
                    .orElseThrow(() -> new IllegalArgumentException("Game meeting not found"));

            if ("E".equals(gameMeeting.getStatus())) {
                throw new IllegalStateException("Meeting already ended");
            }

            gameMeeting.setStatus("E");
            gameSessionRepository.save(gameMeeting);

        } else if ("consult".equals(type)) {
            ConsultMeetingScheduleEntity consultMeeting = consultMeetingScheduleRepository.findById(schdlId)
                    .orElseThrow(() -> new IllegalArgumentException("Consult meeting not found"));

            if ("E".equals(consultMeeting.getStatus())) {
                throw new IllegalStateException("Meeting already ended");
            }

            consultMeeting.setStatus("E");
            consultSessionRepository.save(consultMeeting);
        } else{
            throw new RuntimeException("wrong type");
        }

    }

}
