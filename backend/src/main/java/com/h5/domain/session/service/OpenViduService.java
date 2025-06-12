package com.h5.domain.session.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * OpenVidu 서버와의 세션 및 토큰 생성을 담당하는 서비스 클래스입니다.
 * <p>
 * - createSession(): OpenVidu 서버에 새로운 세션 생성 요청
 * - createConnection(String): 기존 세션에 연결할 수 있는 토큰 생성 요청
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OpenViduService {

    private final ObjectMapper objectMapper;

    /**
     * OpenVidu 서버 URL
     */
    @Value("${openvidu.url}")
    private String openviduUrl;

    /**
     * OpenVidu 서버 인증을 위한 시크릿 키
     */
    @Value("${openvidu.secret}")
    private String openviduSecret;

    private final RestTemplate restTemplate;

    /**
     * OpenVidu에 새로운 세션을 생성하고 커스텀 세션 ID를 반환합니다.
     *
     * @return 생성된 커스텀 세션 ID
     * @throws RuntimeException 세션 생성에 실패한 경우
     */
    public String createSession() {
        String url = openviduUrl + "/api/sessions";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("OPENVIDUAPP", openviduSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String customSessionId = UUID.randomUUID().toString();
        String payload = "{ \"customSessionId\": \"" + customSessionId + "\" }";

        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
            return customSessionId;
        } else {
            throw new RuntimeException("Failed to create OpenVidu session: " + response.getStatusCode());
        }
    }

    /**
     * 지정된 세션 ID에 대해 OpenVidu 토큰을 생성하여 반환합니다.
     *
     * @param sessionId 연결할 세션의 ID
     * @return 생성된 토큰 문자열
     * @throws RuntimeException 토큰 생성이나 JSON 파싱에 실패한 경우
     */
    public String createConnection(String sessionId) {
        String url = openviduUrl + "/api/tokens";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("OPENVIDUAPP", openviduSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String payload = "{ \"session\": \"" + sessionId + "\", \"role\": \"PUBLISHER\" }";

        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                return objectMapper
                        .readTree(response.getBody())
                        .get("token")
                        .asText();
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse OpenVidu token response", e);
            }
        } else {
            throw new RuntimeException("Failed to create OpenVidu connection: " + response.getStatusCode());
        }
    }
}