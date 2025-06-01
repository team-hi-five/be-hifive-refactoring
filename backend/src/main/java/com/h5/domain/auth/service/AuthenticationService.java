package com.h5.domain.auth.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.global.exception.DomainErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    /**
     * 현재 인증된 사용자의 이메일을 반환합니다.
     * SecurityContextHolder에서 Authentication 객체를 꺼내와
     * getName()으로 이메일(또는 username)을 리턴합니다.
     *
     * @return 인증된 사용자의 이메일
     * @throws BusinessException 인증 정보가 없는 경우
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(DomainErrorCode.AUTHENTICATION_FAILED);
        }
        return authentication.getName();
    }

}
