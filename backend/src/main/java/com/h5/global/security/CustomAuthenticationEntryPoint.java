package com.h5.global.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        writeErrorResponse(response, "Unauthorized", "인증 정보가 없습니다 또는 유효하지 않습니다.");
    }

    public void handleAccessDenied(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        writeErrorResponse(response, "Forbidden", "접근 권한이 없습니다.");
    }

    private void writeErrorResponse(HttpServletResponse response, String error, String message) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("error", error);
        errorBody.put("message", message);
        response.getWriter()
                .write(objectMapper.writeValueAsString(errorBody));
    }
}
