package com.h5.domain.consultant.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.domain.auth.service.AuthenticationService;
import com.h5.domain.child.service.ChildUserService;
import com.h5.domain.consultant.dto.request.RegisterParentAccountDto;
import com.h5.domain.consultant.dto.request.UpdatePwdRequestDto;
import com.h5.domain.consultant.dto.response.*;
import com.h5.domain.consultant.entity.ConsultantUserEntity;
import com.h5.domain.consultant.mapper.ConsultantMapper;
import com.h5.domain.consultant.repository.ConsultantUserRepository;
import com.h5.domain.parent.service.ParentUserService;
import com.h5.global.exception.DomainErrorCode;
import com.h5.global.util.MailUtil;
import com.h5.global.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 상담사(Consultant) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 *
 * <p>주요 기능:
 * <ul>
 *     <li>상담사 이메일 조회</li>
 *     <li>임시 비밀번호 발급 및 이메일 전송</li>
 *     <li>비밀번호 업데이트</li>
 *     <li>프로필 정보 조회</li>
 *     <li>학부모 계정 및 자녀 계정 등록</li>
 *     <li>담당 자녀 목록 조회</li>
 *     <li>특정 자녀 상세 조회</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ConsultantUserService {

    private final ConsultantUserRepository consultantUserRepository;
    private final AuthenticationService authenticationService;
    private final ParentUserService parentUserService;
    private final ChildUserService childUserService;
    private final PasswordUtil passwordUtil;
    private final PasswordEncoder passwordEncoder;
    private final MailUtil mailUtil;
    private final ConsultantMapper consultantMapper;

    /**
     * 상담사 이름(name)과 전화번호(phone)을 기반으로 이메일을 조회합니다.
     *
     * @param name  조회할 상담사 이름
     * @param phone 조회할 상담사 전화번호
     * @return {@link GetEmailResponse} 조회된 이메일 정보를 담은 DTO
     * @throws BusinessException {@link DomainErrorCode#USER_NOT_FOUND} 조회된 사용자가 없을 경우 발생
     */
    public GetEmailResponse findEmail(String name, String phone) {
        String email = consultantUserRepository.findEmailByNameAndPhone(name, phone)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND))
                .getEmail();

        return GetEmailResponse.builder()
                .email(email)
                .build();
    }

    /**
     * 상담사 이메일(email)에 대해 임시 비밀번호를 생성하고, 해당 비밀번호를 이메일로 전송합니다.
     *
     * @param email 임시 비밀번호를 발급할 상담사 이메일
     * @throws BusinessException {@link DomainErrorCode#USER_NOT_FOUND} 해당 이메일의 사용자가 없을 경우 발생
     */
    public void issueTemporaryPassword(String email) {
        ConsultantUserEntity consultantUser = consultantUserRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        String tempPwd = passwordUtil.generatePassword();
        consultantUser.setPwd(passwordEncoder.encode(tempPwd));
        consultantUser.setTempPwd(true);

        consultantUserRepository.save(consultantUser);
        mailUtil.sendTempPasswordEmail(email, tempPwd);
    }

    /**
     * 현재 인증된 상담사의 기존 비밀번호(oldPwd)를 검증하고, 일치할 경우 새 비밀번호(newPwd)로 업데이트합니다.
     *
     * @param dto {@link UpdatePwdRequestDto} 기존 비밀번호와 새 비밀번호를 포함한 DTO
     * @throws BusinessException        {@link DomainErrorCode#USER_NOT_FOUND} 인증된 사용자를 찾을 수 없을 경우 발생
     * @throws IllegalArgumentException 기존 비밀번호가 일치하지 않을 경우 발생
     */
    public void updatePwd(UpdatePwdRequestDto dto) {
        String email = authenticationService.getCurrentUserEmail();

        ConsultantUserEntity consultantUser = consultantUserRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(dto.getOldPwd(), consultantUser.getPwd())) {
            throw new IllegalArgumentException("Old password does not match.");
        }

        consultantUser.setPwd(passwordEncoder.encode(dto.getNewPwd()));
        consultantUser.setTempPwd(false);

        consultantUserRepository.save(consultantUser);
    }

    /**
     * 현재 인증된 상담사의 프로필 정보를 조회합니다.
     *
     * @return {@link MyProfileResponse} 프로필 이미지 URL, 이름, 이메일, 전화번호, 센터명 및 센터 연락처를 포함한 DTO
     * @throws BusinessException {@link DomainErrorCode#USER_NOT_FOUND} 인증된 사용자를 찾을 수 없을 경우 발생
     */
    public MyProfileResponse getMyProfile() {
        String email = authenticationService.getCurrentUserEmail();

        ConsultantUserEntity consultantUser = consultantUserRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        return consultantMapper.toMyProfileDto(consultantUser);
    }

    /**
     * 인증된 상담사가 새로운 학부모 계정과 자녀 계정을 동시에 등록합니다.
     *
     * <p>1. 인증된 상담사의 정보 조회<br>
     * 2. {@link ParentUserService#issueOrGetParent} 호출하여 학부모 계정 생성 또는 기존 계정 조회<br>
     * 3. {@link ChildUserService#issueChild} 호출하여 자녀 계정 생성
     *
     * @param dto {@link RegisterParentAccountDto} 학부모 및 자녀 정보를 담은 DTO
     * @return {@link RegisterParentAccountResponse} 생성(또는 조회)된 학부모 ID와 자녀 ID를 포함한 DTO
     * @throws BusinessException {@link DomainErrorCode#USER_NOT_FOUND} 인증된 상담사를 찾을 수 없을 경우 발생
     */
    public RegisterParentAccountResponse registerParentAccount(RegisterParentAccountDto dto) {
        String email = authenticationService.getCurrentUserEmail();

        ConsultantUserEntity consultantUser = consultantUserRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        Integer parentUserId = parentUserService.issueOrGetParent(dto, consultantUser.getId());
        Integer childUserId = childUserService.issueChild(dto, parentUserId, consultantUser.getId());

        return RegisterParentAccountResponse.builder()
                .parentUserId(parentUserId)
                .childUserId(childUserId)
                .build();
    }

    /**
     * 인증된 상담사가 담당하는 자녀 목록을 조회합니다.
     *
     * @return {@link List} of {@link GetMyChildrenResponse} 자녀의 ID, 프로필 이미지 URL, 이름, 생일, 나이, 부모 이름을 포함한 DTO 리스트
     * @throws BusinessException {@link DomainErrorCode#USER_NOT_FOUND} 인증된 상담사를 찾을 수 없을 경우 발생
     */
    public List<GetMyChildrenResponse> getMyChildren() {
        String email = authenticationService.getCurrentUserEmail();

        ConsultantUserEntity consultantUser = consultantUserRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        return childUserService.getChildrenByConsultant(consultantUser.getId());
    }

    /**
     * 특정 자녀의 상세 정보를 조회합니다.
     *
     * @param childUserId 조회할 자녀 사용자 ID
     * @return {@link GetChildResponse} 자녀의 상세 정보(프로필 이미지 URL, 이름, 생년월일, 나이, 성별, 첫 상담 일자, 관심사, 추가 정보, 부모 정보)를 포함한 DTO
     * @throws BusinessException {@link DomainErrorCode#USER_NOT_FOUND} 인증된 상담사 또는 자녀를 찾을 수 없을 경우 발생
     */
    public GetChildResponse getChild(Integer childUserId) {
        String consultantEmail = authenticationService.getCurrentUserEmail();

        ConsultantUserEntity consultantUser = consultantUserRepository.findByEmail(consultantEmail)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        return childUserService.getChildDetail(childUserId, consultantUser.getId());
    }
}
