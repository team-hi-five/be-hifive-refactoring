package com.h5.global.security;

import com.h5.domain.auth.service.ConsultantCustomUserDetailService;
import com.h5.domain.auth.service.ParentCustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ConsultantCustomUserDetailService consultantCustomUserDetailService;
    private final ParentCustomUserDetailService parentCustomUserDetailService;
    private final RedisTemplate<Object, Object> redisTemplate;

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/swagger-ui/",
            "/api/v3/api-docs",
            "/api/swagger-resources",
            "/api/webjars"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String uri = request.getRequestURI();
        if (EXCLUDED_PATHS.stream().anyMatch(uri::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        String token = null;
        String email = null;
        String role = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);

            // Redis에 블랙리스트에 있다면 만료 또는 무효 토큰
            if (Boolean.TRUE.equals(redisTemplate.hasKey(token))) {
                throw new JwtAuthenticationException("Invalid or expired token");
            }

            // 리프레시 엔드포인트는 검증 스킵
            if (!uri.equals("/auth/refresh")) {
                try {
                    if (!jwtUtil.validateToken(token)) {
                        throw new JwtAuthenticationException("Invalid JWT token");
                    }
                } catch (AuthenticationException ex) {
                    throw ex;
                } catch (Exception ex) {
                    throw new JwtAuthenticationException(ex.getMessage());
                }
                email = jwtUtil.getEmailFromToken(token);
                role = jwtUtil.getRoleFromToken(token);
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            setSecurityContext(email, role);
        }

        filterChain.doFilter(request, response);
    }

    private void setSecurityContext(String email, String role) {
        UserDetails userDetails = "ROLE_CONSULTANT".equals(role)
                ? consultantCustomUserDetailService.loadUserByUsername(email)
                : parentCustomUserDetailService.loadUserByUsername(email);

        var auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // 인증 예외용 커스텀 AuthenticationException
    public static class JwtAuthenticationException extends AuthenticationException {
        public JwtAuthenticationException(String msg) {
            super(msg);
        }
    }
}
