package com.h5.domain.parent.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.domain.auth.service.AuthenticationService;
import com.h5.domain.child.entity.ChildUserEntity;
import com.h5.domain.child.repository.ChildUserRepository;
import com.h5.domain.consultant.dto.request.RegisterParentAccountDto;
import com.h5.domain.consultant.dto.response.EmailCheckResponse;
import com.h5.domain.consultant.entity.ConsultantUserEntity;
import com.h5.domain.parent.mapper.ParentMapper;
import com.h5.global.exception.DomainErrorCode;
import com.h5.global.util.MailUtil;
import com.h5.global.util.PasswordUtil;
import com.h5.domain.parent.dto.info.ConsultantInfo;
import com.h5.domain.parent.dto.info.MyChildInfo;
import com.h5.domain.parent.dto.info.MyInfo;
import com.h5.domain.parent.dto.response.MyChildrenResponseDto;
import com.h5.domain.parent.dto.response.MyPageResponseDto;
import com.h5.domain.parent.entity.ParentUserEntity;
import com.h5.domain.parent.repository.ParentUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParentUserService {

    private final ParentUserRepository parentUserRepository;
    private final ChildUserRepository childUserRepository;
    private final MailUtil mailUtil;
    private final PasswordEncoder passwordEncoder;
    private final PasswordUtil passwordUtil;
    private final AuthenticationService authenticationService;
    private final ParentMapper parentMapper;

    /**
     * 기존에 등록된 부모 계정이 있으면 해당 계정의 ID를 반환하고,
     * 없으면 새로운 부모 계정을 생성한 뒤 초기 비밀번호를 발급하여 이메일로 전송하고 ID를 반환한다.
     *
     * @param dto           부모 계정 등록 정보 DTO (이름, 이메일, 전화번호)
     * @param consultantId  상담사 사용자 ID
     * @return 생성되었거나 조회된 부모 사용자 엔티티의 ID
     * @throws BusinessException 이메일 전송 실패 시 {@link DomainErrorCode#MAIL_SEND_FAILED}
     */
    @Transactional
    public Integer issueOrGetParent(RegisterParentAccountDto dto, Integer consultantId) {
        Optional<ParentUserEntity> optionalParentUser = parentUserRepository.findByEmail(dto.getParentEmail());
        ParentUserEntity parentUser;
        String initPwd = null;

        if (optionalParentUser.isPresent()) {
            parentUser = optionalParentUser.get();
        } else {
            initPwd = passwordUtil.generatePassword();
            parentUser = ParentUserEntity.builder()
                    .name(dto.getParentName())
                    .email(dto.getParentEmail())
                    .pwd(passwordEncoder.encode(initPwd))
                    .phone(dto.getParentPhone())
                    .tempPwd(true)
                    .consultantUserId(consultantId)
                    .build();
            parentUser = parentUserRepository.save(parentUser);

            try {
                mailUtil.sendRegistrationEmail(dto.getParentEmail(), initPwd);
            } catch (Exception e) {
                throw new BusinessException(DomainErrorCode.MAIL_SEND_FAILED);
            }
        }

        return parentUser.getId();
    }

    /**
     * 현재 인증된 부모 사용자의 자녀 목록을 조회한다.
     *
     * @return 등록된 자녀 정보 목록 (자녀 ID, 자녀 이름)
     * @throws BusinessException 사용자가 존재하지 않거나 조회된 자녀가 없을 경우 {@link DomainErrorCode#USER_NOT_FOUND}
     */
    public List<MyChildrenResponseDto> getMyChildren() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String parentEmail = authentication.getName();

        ParentUserEntity parentUserEntity = findByEmailOrThrow(parentEmail);
        List<ChildUserEntity> myChildren = childUserRepository
                .findAllByParentUserEntity_IdAndDeleteDttmIsNull(parentUserEntity.getId())
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        return myChildren.stream()
                .map(child -> MyChildrenResponseDto.builder()
                        .childUserId(child.getId())
                        .childUserName(child.getName())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 주어진 이메일로 부모 계정 존재 여부를 확인하고, 계정이 있으면 해당 부모 정보(이름, 전화번호)를 함께 반환한다.
     *
     * @param email 확인할 부모 이메일
     * @return 계정 존재 여부 및 부모 정보가 담긴 {@link EmailCheckResponse}
     * @throws BusinessException 계정이 존재한다고 판단되었으나 실제 조회되지 않을 경우 {@link DomainErrorCode#USER_NOT_FOUND}
     */
    public EmailCheckResponse searchByEmail(String email) {
        if (parentUserRepository.findByEmail(email).isEmpty()) {
            return EmailCheckResponse.builder()
                    .alreadyAccount(false)
                    .build();
        }

        ParentUserEntity parentUser = parentUserRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        return EmailCheckResponse.builder()
                .alreadyAccount(true)
                .email(email)
                .parentName(parentUser.getName())
                .parentPhone(parentUser.getPhone())
                .build();
    }

    /**
     * 부모 사용자 엔티티를 논리 삭제 처리한다.
     *
     * @param parentUserId 삭제할 부모 사용자 ID
     * @param deleteDttm   삭제 시각 (논리 삭제를 위한 timestamp)
     * @throws BusinessException 사용자가 존재하지 않을 경우 {@link DomainErrorCode#USER_NOT_FOUND}
     */
    public void markDeleted(Integer parentUserId, LocalDateTime deleteDttm) {
        ParentUserEntity parentUserEntity = parentUserRepository.findById(parentUserId)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        parentUserEntity.setDeleteDttm(deleteDttm);
        parentUserRepository.save(parentUserEntity);
    }

    /**
     * 현재 인증된 부모 사용자의 마이페이지 정보(개인 정보, 자녀 목록, 상담사 정보)를 조회한다.
     *
     * @return 마이페이지에 표시할 {@link MyPageResponseDto}
     */
    @Transactional(readOnly = true)
    public MyPageResponseDto getMyPageInfo() {
        String parentEmail = authenticationService.getCurrentUserEmail();

        ParentUserEntity parentUserEntity = findByEmailOrThrow(parentEmail);
        List<ChildUserEntity> childUserEntities = parentUserEntity.getChildUserEntities().stream().toList();
        ConsultantUserEntity consultantUserEntity = parentUserEntity.getConsultantUserEntity();

        MyInfo myInfo = parentMapper.buildMyInfo(parentUserEntity);
        List<MyChildInfo> myChildInfos = parentMapper.buildMyChildInfos(childUserEntities);
        ConsultantInfo consultantInfo = parentMapper.buildConsultantInfo(consultantUserEntity);

        return MyPageResponseDto.builder()
                .myChildren(myChildInfos)
                .myInfo(myInfo)
                .consultantInfo(consultantInfo)
                .build();
    }

    /**
     * 이메일로 부모 사용자 엔티티를 조회하거나, 존재하지 않으면 예외를 던진다.
     *
     * @param email 조회할 부모 이메일
     * @return 조회된 {@link ParentUserEntity}
     * @throws BusinessException 사용자가 존재하지 않을 경우 {@link DomainErrorCode#USER_NOT_FOUND}
     */
    public ParentUserEntity findByEmailOrThrow(String email) {
        return parentUserRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));
    }
}
