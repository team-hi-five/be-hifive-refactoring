package com.h5.domain.alarm.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.domain.alarm.dto.AlarmDto;
import com.h5.domain.alarm.dto.request.AlarmRequestDto;
import com.h5.domain.child.repository.ChildUserRepository;
import com.h5.domain.consultant.repository.ConsultantUserRepository;
import com.h5.global.config.SessionChannelInterceptor;
import com.h5.global.exception.DomainErrorCode;
import com.h5.domain.parent.repository.ParentUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 알람 전송 로직을 처리하는 서비스 클래스입니다.
 * <p>
 * 상담사와 학부모(또는 자녀) 간의 채팅 세션 타입에 따라 맞춤형 알람 메시지를 생성하고,
 * WebSocket을 통해 대상 사용자의 세션으로 전송합니다.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AlarmService {

    private static final String GAME_SESSION = "game";
    private static final String ROLE_CONSULTANT = "ROLE_CONSULTANT";
    private static final String ALARM_DESTINATION = "/queue/alarms";

    private final SimpMessagingTemplate messagingTemplate;
    private final ParentUserRepository parentUserRepository;
    private final ConsultantUserRepository consultantUserRepository;
    private final ChildUserRepository childUserRepository;
    private final SessionChannelInterceptor sessionChannelInterceptor;

    /**
     * 알람 요청 DTO를 기반으로 알람 메시지를 생성하고, 해당 사용자의 WebSocket 세션으로 전송합니다.
     * <p>
     * 1. 요청 DTO에서 sessionType과 senderRole을 확인하여 게임 세션 여부와 발신자 유형(상담사/학부모)을 판별합니다.
     * 2. 알맞은 메시지를 구성하고, 자녀 ID를 바탕으로 수신자 이메일(부모 또는 상담사 이메일)을 조회합니다.
     * 3. 메시지와 현재 시각을 포함한 {@link AlarmDto}를 생성한 뒤, WebSocket을 통해 수신자의 세션으로 전송합니다.
     * </p>
     *
     * @param requestDto 알람 요청 정보를 담은 {@link AlarmRequestDto}
     * @throws BusinessException 자녀, 부모, 또는 상담사 사용자를 찾지 못한 경우(DomainErrorCode.USER_NOT_FOUND)
     */
    @Transactional(readOnly = true)
    public void sendAlarm(AlarmRequestDto requestDto) {
        boolean isGameSession = GAME_SESSION.equals(requestDto.getSessionType());
        boolean isConsultantSender = ROLE_CONSULTANT.equals(requestDto.getSenderRole());

        log.info("SessionType: {}, SenderRole: {}", requestDto.getSessionType(), requestDto.getSenderRole());

        String message = buildMessage(isConsultantSender, isGameSession);
        String recipientEmail = fetchRecipientEmail(isConsultantSender, requestDto.getToUserId());

        AlarmDto alarmDto = AlarmDto.builder()
                .message(message)
                .toUserEmail(recipientEmail)
                .time(LocalDateTime.now())
                .build();

        sendToUserSession(recipientEmail, alarmDto);
    }

    /**
     * 발신자 유형(상담사/학부모)과 세션 타입(게임/일반)에 따라 알람 메시지 텍스트를 구성합니다.
     *
     * @param isConsultantSender 상담사 발신 여부(true일 경우 상담사 발신, false일 경우 학부모 발신)
     * @param isGameSession      게임 세션 여부(true일 경우 게임 세션, false일 경우 일반 세션)
     * @return 전송할 알람 메시지 문자열
     */
    private String buildMessage(boolean isConsultantSender, boolean isGameSession) {
        if (isConsultantSender) {
            return isGameSession
                    ? "선생님이 기다리고 있어요!"
                    : "상담사가 기다리고 있습니다!";
        } else {
            return isGameSession
                    ? "아이가 기다리고 있습니다!"
                    : "학부모님이 기다리고 있습니다!";
        }
    }

    /**
     * 발신자 유형에 따라 자녀 ID를 이용해 대상 사용자의 이메일을 조회합니다.
     * <p>
     * 상담사 발신일 경우 부모 이메일을, 학부모(또는 자녀) 발신일 경우 상담사 이메일을 조회합니다.
     * </p>
     *
     * @param isConsultantSender 상담사 발신 여부
     * @param childUserId        자녀 사용자 ID
     * @return 조회된 대상 사용자 이메일
     * @throws BusinessException 조회 중 자녀, 부모, 또는 상담사 사용자를 찾지 못한 경우(DomainErrorCode.USER_NOT_FOUND)
     */
    private String fetchRecipientEmail(boolean isConsultantSender, int childUserId) {
        if (isConsultantSender) {
            return lookupParentEmail(childUserId);
        } else {
            return lookupConsultantEmail(childUserId);
        }
    }

    /**
     * 자녀 ID를 통해 부모 이메일을 조회합니다.
     *
     * @param childUserId 자녀 사용자 ID
     * @return 조회된 부모 사용자 이메일
     * @throws BusinessException 자녀 또는 부모 사용자를 찾지 못한 경우(DomainErrorCode.USER_NOT_FOUND)
     */
    private String lookupParentEmail(int childUserId) {
        int parentUserId = childUserRepository.findById(childUserId)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND))
                .getParentUserEntity().getId();

        return parentUserRepository.findById(parentUserId)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND))
                .getEmail();
    }

    /**
     * 자녀 ID를 통해 담당 상담사 이메일을 조회합니다.
     *
     * @param childUserId 자녀 사용자 ID
     * @return 조회된 상담사 사용자 이메일
     * @throws BusinessException 자녀 또는 상담사 사용자를 찾지 못한 경우(DomainErrorCode.USER_NOT_FOUND)
     */
    private String lookupConsultantEmail(int childUserId) {
        int consultantUserId = childUserRepository.findById(childUserId)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND))
                .getConsultantUserEntity().getId();

        return consultantUserRepository.findById(consultantUserId)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND))
                .getEmail();
    }

    /**
     * 대상 사용자(이메일 기준)의 세션 ID를 조회하여 WebSocket을 통해 알람 DTO를 전송합니다.
     * <p>
     * 세션 ID가 없으면 로그를 남기고 전송을 중단합니다.
     * </p>
     *
     * @param username 대상 사용자의 이메일(식별자)
     * @param alarmDto 전송할 알람 정보를 담은 {@link AlarmDto}
     */
    private void sendToUserSession(String username, AlarmDto alarmDto) {
        String sessionId = sessionChannelInterceptor.getSessionIdForUser(username);
        if (sessionId == null) {
            log.info("대상 사용자({})의 sessionId를 찾을 수 없습니다.", username);
            return;
        }

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        headerAccessor.setHeader("simpSessionId", sessionId);

        messagingTemplate.convertAndSendToUser(
                username,
                ALARM_DESTINATION,
                alarmDto,
                headerAccessor.getMessageHeaders()
        );
    }
}
