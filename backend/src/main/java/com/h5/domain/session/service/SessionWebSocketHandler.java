package com.h5.domain.session.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

/**
 * WebSocket을 통해 클라이언트와 메시지를 주고받는 핸들러 클래스입니다.
 * <p>
 * 클라이언트 연결, 메시지 수신/송신, 연결 종료 이벤트를 처리합니다.
 * </p>
 */
@Slf4j
@Service
public class SessionWebSocketHandler extends TextWebSocketHandler {

    /**
     * WebSocket 연결이 성공적으로 수립된 후 호출됩니다.
     *
     * @param session 연결된 WebSocket 세션 정보
     * @throws Exception 연결 후 처리 중 예외가 발생할 수 있습니다.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket 연결됨: {}", session.getId());
    }

    /**
     * 클라이언트로부터 텍스트 메시지를 수신했을 때 호출됩니다.
     *
     * @param session 연결된 WebSocket 세션 정보
     * @param message 수신한 텍스트 메시지
     * @throws IOException 메시지 전송 중 I/O 오류가 발생할 수 있습니다.
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        log.info("WebSocket 메시지 수신: {}", message.getPayload());
        // 클라이언트에게 수신 메시지를 그대로 회신
        session.sendMessage(new TextMessage("Received: " + message.getPayload()));
    }

    /**
     * WebSocket 연결이 종료된 후 호출됩니다.
     *
     * @param session 연결이 종료된 WebSocket 세션 정보
     * @param status 연결 종료 상태 정보
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("WebSocket 연결 종료: {}", session.getId());
    }
}
