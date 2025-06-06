package com.h5.domain.auth.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.domain.auth.dto.request.LoginRequestDto;
import com.h5.domain.auth.dto.response.GetUserRoleResponseDto;
import com.h5.domain.auth.dto.response.LoginResponseDto;
import com.h5.domain.auth.dto.response.RefreshAccessTokenResponseDto;
import com.h5.domain.consultant.dto.request.UpdatePwdRequestDto;
import com.h5.domain.consultant.dto.request.UpdateToTempPwdRequestDto;
import com.h5.domain.consultant.dto.response.GetEmailResponse;
import com.h5.domain.consultant.entity.ConsultantUserEntity;
import com.h5.domain.consultant.repository.ConsultantUserRepository;
import com.h5.domain.parent.entity.ParentUserEntity;
import com.h5.domain.parent.repository.ParentUserRepository;
import com.h5.global.exception.DomainErrorCode;
import com.h5.global.redis.RedisService;
import com.h5.global.util.JwtUtil;
import com.h5.global.util.MailUtil;
import com.h5.global.util.PasswordUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RedisService redisService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ConsultantCustomUserDetailService consultantUserDetails;
    private final ParentCustomUserDetailService parentUserDetails;
    private final ConsultantUserRepository consultantUserRepository;
    private final ParentUserRepository parentUserRepository;
    private final PasswordUtil passwordUtil;
    private final PasswordEncoder passwordEncoder;
    private final MailUtil mailUtil;

    /**
     * 사용자 이메일과 비밀번호를 인증하고, Access Token과 Refresh Token을 생성 후 저장한다.
     *
     * @param dto 로그인 요청 정보를 담은 DTO(LoginRequestDto)
     * @return 생성된 Access Token과 사용자 이름, 비밀번호 변경 여부를 포함한 LoginResponseDto
     * @throws BusinessException 이메일에 해당하는 사용자를 찾지 못한 경우
     * @throws BusinessException 인증에 실패하거나 기타 비즈니스 로직 오류 발생 시
     */
    @Transactional(readOnly = true)
    public LoginResponseDto authenticateAndGenerateToken(LoginRequestDto dto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPwd())
        );
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        String name;
        boolean isTempPwd;
        if ("ROLE_CONSULTANT".equals(dto.getRole())) {
            ConsultantUserEntity user = consultantUserRepository.findByEmailAndDeleteDttmIsNull(dto.getEmail())
                    .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));
            name = user.getName();
            isTempPwd = user.isTempPwd();
        } else {
            ParentUserEntity user = parentUserRepository.findByEmailAndDeleteDttmIsNull(dto.getEmail())
                    .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));
            name = user.getName();
            isTempPwd = user.isTempPwd();
        }

        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        redisService.saveRefreshToken(userDetails.getUsername(), refreshToken);

        return LoginResponseDto.builder()
                .name(name)
                .accessToken(accessToken)
                .pwdChanged(isTempPwd)
                .build();
    }

    /**
     * 현재 요청의 Authorization 헤더에서 Access Token을 추출하여 블랙리스트에 등록하고,
     * 해당 사용자의 Refresh Token을 삭제한다.
     *
     * @throws BusinessException 토큰이 존재하지 않거나 형식이 잘못된 경우
     */
    public void logout() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new BusinessException(DomainErrorCode.ACCESS_TOKEN_NOTFOUND);
        }

        String accessToken = bearerToken.substring(7);
        long remainingTimeMills = jwtUtil.getRemainExpiredTime(accessToken);
        redisService.saveBlacklistedToken(accessToken, remainingTimeMills);

        String email = jwtUtil.getEmailFromToken(accessToken);
        redisService.deleteRefreshToken(email);
    }

    /**
     * 현재 인증된 사용자 정보를 기반으로 새로운 Access Token을 발급한다.
     * Refresh Token이 만료 임박 상태인 경우에는 새로운 Refresh Token도 발급하여 갱신한다.
     *
     * @return 새롭게 발급된 Access Token을 포함한 RefreshAccessTokenResponseDto
     * @throws IllegalArgumentException Refresh Token이 만료되었거나 Redis에 저장된 토큰이 없을 경우
     */
    public RefreshAccessTokenResponseDto refreshAccessToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        String storedToken = redisService.getRefreshToken(email);
        if (storedToken == null || jwtUtil.isRefreshTokenExpired(storedToken)) {
            redisService.deleteRefreshToken(email);
            throw new IllegalArgumentException("Refresh token expired or missing. Please login again.");
        }

        UserDetails userDetails = loadUserByEmail(email);
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        if (jwtUtil.isTokenNearExpiry(storedToken)) {
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
            long oldTtl = jwtUtil.getExpiration(storedToken) - System.currentTimeMillis();
            redisService.saveBlacklistedToken(storedToken, oldTtl);
            redisService.saveRefreshToken(email, newRefreshToken);
        }
        return RefreshAccessTokenResponseDto.builder()
                .accessToken(newAccessToken)
                .build();
    }

    /**
     * 현재 인증된 사용자의 이메일과 권한 정보를 기반으로 사용자 권한를 조회한다.
     *
     * @return 사용자 이름과 역할 정보를 포함한 GetUserRoleResponseDto
     * @throws BusinessException 해당 이메일에 매핑되는 사용자를 찾지 못한 경우
     */
    @Transactional(readOnly = true)
    public GetUserRoleResponseDto getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName();

        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst().orElse(null);

        String name = consultantUserRepository.findByEmail(email)
                .map(ConsultantUserEntity::getName)
                .orElseGet(() -> parentUserRepository.findByEmail(email)
                        .map(ParentUserEntity::getName)
                        .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND)));

        return GetUserRoleResponseDto.builder()
                .name(name)
                .role(role)
                .build();
    }

    /**
     * 이름(name)과 전화번호(phone), 권한(role)을 기반으로 이메일을 조회합니다.
     *
     * @param name  조회할 이름
     * @param phone 조회할 전화 번호
     * @param role  권한
     * @return {@link GetEmailResponse} 조회된 이메일 정보를 담은 DTO
     * @throws BusinessException {@link DomainErrorCode#USER_NOT_FOUND} 조회된 사용자가 없을 경우 발생
     */
    public GetEmailResponse findEmail(String name, String phone, String role) {

        String email = "";

        if ("consultant".equals(role)) {
            email = consultantUserRepository.findEmailByNameAndPhone(name, phone)
                    .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND))
                    .getEmail();
        } else if ("parent".equals(role)) {
            email = parentUserRepository.findEmailByNameAndPhone(name, phone)
                    .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND))
                    .getEmail();
        }

        return GetEmailResponse.builder()
                .email(email)
                .build();
    }

    /**
     * 상담사 또는 학부모 이메일에 대해 임시 비밀번호를 생성하고, 해당 비밀번호를 이메일로 전송합니다.
     *
     * @param dto 임시 비밀번호 발급 요청 정보 (이메일, 역할)
     * @throws BusinessException {@link DomainErrorCode#USER_NOT_FOUND} 해당 이메일의 사용자가 없을 경우 발생
     * @throws BusinessException {@link DomainErrorCode#INVALID_ROLE} 유효하지 않은 역할이 전달된 경우 발생
     */
    public void issueTemporaryPassword(UpdateToTempPwdRequestDto dto) {
        String role = dto.getRole().toLowerCase();
        String email = dto.getEmail();

        String tempPwd = passwordUtil.generatePassword();
        String encodedPwd = passwordEncoder.encode(tempPwd);

        switch (role) {
            case "consultant":
                ConsultantUserEntity consultantUser = consultantUserRepository.findByEmail(email)
                        .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));
                consultantUser.setPwd(encodedPwd);
                consultantUser.setTempPwd(true);
                consultantUserRepository.save(consultantUser);
                mailUtil.sendTempPasswordEmail(email, tempPwd);
                break;

            case "parent":
                ParentUserEntity parentUser = parentUserRepository.findByEmail(email)
                        .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));
                parentUser.setPwd(encodedPwd);
                parentUser.setTempPwd(true);
                parentUserRepository.save(parentUser);
                mailUtil.sendTempPasswordEmail(email, tempPwd);
                break;

            default:
                throw new BusinessException(DomainErrorCode.INVALID_ROLE);
        }
    }

    /**
     * 현재 인증된 사용자의 기존 비밀번호(oldPwd)를 검증하고, 일치할 경우 새 비밀번호(newPwd)로 업데이트합니다.
     *
     * @param dto {@link UpdatePwdRequestDto} 기존 비밀번호와 새 비밀번호를 포함한 DTO
     * @throws BusinessException        {@link DomainErrorCode#USER_NOT_FOUND} 인증된 사용자를 찾을 수 없을 경우 발생
     * @throws BusinessException        {@link DomainErrorCode#OLD_PASSWORD_MISMATCH} 기존 비밀번호가 일치하지 않을 경우 발생
     * @throws BusinessException        {@link DomainErrorCode#INVALID_ROLE} 지원하지 않는 역할인 경우 발생
     */
    @Transactional
    public void updatePwd(UpdatePwdRequestDto dto) {
        String email = getCurrentUserEmail();
        String role  = getCurrentUserRole();
        String oldPwd = dto.getOldPwd();
        String newPwd = dto.getNewPwd();
        String encodedNewPwd = passwordEncoder.encode(newPwd);

        switch (role) {
            case "ROLE_CONSULTANT": {
                ConsultantUserEntity consultant = consultantUserRepository.findByEmail(email)
                        .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

                if (!passwordEncoder.matches(oldPwd, consultant.getPwd())) {
                    throw new BusinessException(DomainErrorCode.OLD_PASSWORD_MISMATCH);
                }

                consultant.setPwd(encodedNewPwd);
                consultant.setTempPwd(false);
                consultantUserRepository.save(consultant);
                break;
            }
            case "ROLE_PARENT": {
                ParentUserEntity parent = parentUserRepository.findByEmail(email)
                        .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

                if (!passwordEncoder.matches(oldPwd, parent.getPwd())) {
                    throw new BusinessException(DomainErrorCode.OLD_PASSWORD_MISMATCH);
                }

                parent.setPwd(encodedNewPwd);
                parent.setTempPwd(false);
                parentUserRepository.save(parent);
                break;
            }
            default:
                throw new BusinessException(DomainErrorCode.INVALID_ROLE);
        }
    }

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

    /**
     * 현재 인증된 사용자의 역할(Role) 정보를 반환합니다.
     *
     * @return 인증된 사용자의 역할 문자열
     * @throws BusinessException 인증 정보가 없거나 인증되지 않았거나, 역할이 여러 개일 경우 발생
     */
    private String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(DomainErrorCode.AUTHENTICATION_FAILED);
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null || authorities.isEmpty()) {
            throw new BusinessException(DomainErrorCode.AUTHENTICATION_FAILED);
        }
        if (authorities.size() > 1) {
            throw new BusinessException(DomainErrorCode.AUTHENTICATION_FAILED);
        }

        return authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.AUTHENTICATION_FAILED));
    }

    /**
     * 이메일을 기준으로 사용자의 UserDetails를 로드한다.
     * consultantUserRepository에 존재하면 ConsultantCustomUserDetailService를,
     * 아니면 ParentCustomUserDetailService를 사용한다.
     *
     * @param email 조회할 사용자의 이메일
     * @return UserDetails 인터페이스 구현체
     */
    private UserDetails loadUserByEmail(String email) {
        boolean isConsultant = consultantUserRepository.existsByEmail(email);
        return isConsultant
                ? consultantUserDetails.loadUserByUsername(email)
                : parentUserDetails.loadUserByUsername(email);
    }


}
