package com.h5.domain.consultant.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.domain.child.entity.ChildUserEntity;
import com.h5.domain.child.repository.ChildUserRepository;
import com.h5.domain.consultant.dto.request.ModifyChildRequestDto;
import com.h5.domain.consultant.dto.request.RegisterParentAccountDto;
import com.h5.domain.consultant.dto.response.*;
import com.h5.domain.consultant.entity.ConsultantUserEntity;
import com.h5.domain.consultant.repository.ConsultantUserRepository;
import com.h5.domain.file.entity.FileEntity;
import com.h5.domain.file.service.FileService;
import com.h5.global.exception.DomainErrorCode;
import com.h5.global.util.DateUtil;
import com.h5.global.util.MailUtil;
import com.h5.global.util.PasswordUtil;
import com.h5.domain.parent.entity.ParentUserEntity;
import com.h5.domain.parent.repository.ParentUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultantUserService {

    private final ConsultantUserRepository consultantUserRepository;
    private final ParentUserRepository parentUserRepository;
    private final ChildUserRepository childUserRepository;
    private final PasswordUtil passwordUtil;
    private final PasswordEncoder passwordEncoder;
    private final MailUtil mailUtil;
    private final FileService fileService;


    private String getFileUrl(FileEntity.TblType tblType, int tblId) {
        return !fileService.getFileUrl(tblType, tblId).isEmpty() ? fileService.getFileUrl(tblType, tblId).get(0).getUrl() : "Default Image";
    }

    public GetEmailResponse findEmail(String name, String phone) {
        String email = consultantUserRepository.findEmailByNameAndPhone(name, phone)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND))
                .getEmail();

        return GetEmailResponse.builder()
                .email(email)
                .build();
    }

    public void issueTemporaryPassword(String name, String email) {
        ConsultantUserEntity consultantUser = consultantUserRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        String tempPwd = passwordUtil.generatePassword();
        consultantUser.setPwd(passwordEncoder.encode(tempPwd));
        consultantUser.setTempPwd(true);

        consultantUserRepository.save(consultantUser);
        mailUtil.sendTempPasswordEmail(email, email, tempPwd);
    }

    public void updatePwd(String email, String oldPwd, String newPwd) {
        ConsultantUserEntity consultantUser = consultantUserRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(oldPwd, consultantUser.getPwd())) {
            throw new IllegalArgumentException("Old password does not match.");
        }

        consultantUser.setPwd(passwordEncoder.encode(newPwd));
        consultantUser.setTempPwd(false);

        consultantUserRepository.save(consultantUser);
    }

    public RegisterParentAccountResponse registerParentAccount(RegisterParentAccountDto registerParentAccountDto) {
        String consultantEmail = getAuthenticatedEmail();

        ConsultantUserEntity consultantUser = consultantUserRepository.findByEmail(consultantEmail)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        Optional<ParentUserEntity> optionalParentUser = parentUserRepository.findByEmail(registerParentAccountDto.getParentEmail());
        ParentUserEntity parentUser;
        String initPwd = null;

        if (optionalParentUser.isPresent()) {
            parentUser = optionalParentUser.get();
        } else {
            initPwd = passwordUtil.generatePassword();
            parentUser = ParentUserEntity.builder()
                    .name(registerParentAccountDto.getParentName())
                    .email(registerParentAccountDto.getParentEmail())
                    .pwd(passwordEncoder.encode(initPwd))
                    .phone(registerParentAccountDto.getParentPhone())
                    .tempPwd(true)
                    .consultantUserEntity(consultantUser)
                    .build();
            parentUser = parentUserRepository.save(parentUser);

            try {
                mailUtil.sendRegistrationEmail(registerParentAccountDto.getParentEmail(),
                        registerParentAccountDto.getParentEmail(), initPwd);
            } catch (Exception e) {
                throw new BusinessException(DomainErrorCode.MAIL_SEND_FAILED);
            }
        }

        ChildUserEntity childUser = ChildUserEntity.builder()
                .name(registerParentAccountDto.getChildName())
                .birth(LocalDate.parse(registerParentAccountDto.getChildBirth()))
                .gender(registerParentAccountDto.getChildGender())
                .firstConsultDt(LocalDate.parse(registerParentAccountDto.getFirstConsultDt()))
                .interest(registerParentAccountDto.getChildInterest())
                .additionalInfo(registerParentAccountDto.getChildAdditionalInfo())
                .parentUserEntity(parentUser)
                .consultantUserEntity(consultantUser)
                .build();

        childUser = childUserRepository.save(childUser);

        return RegisterParentAccountResponse.builder()
                .parentUserId(parentUser.getId())
                .childUserId(childUser.getId())
                .build();
    }

    @Transactional
    public List<GetMyChildrenResponse> getChildrenForAuthenticatedConsultant(String email) {
        ConsultantUserEntity consultantUser = consultantUserRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        List<ChildUserEntity> childUsers = childUserRepository.findByConsultantUserEntity_IdAndDeleteDttmIsNull(consultantUser.getId())
                .orElse(new ArrayList<>());

        List<GetMyChildrenResponse> responseDtos = new ArrayList<>();
        for (ChildUserEntity child : childUsers) {
            responseDtos.add(
                    GetMyChildrenResponse.builder()
                            .childUserID(child.getId())
                            .profileImgUrl(getFileUrl(FileEntity.TblType.PCD, child.getId()))
                            .childName(child.getName())
                            .birth(String.valueOf(child.getBirth()))
                            .age(DateUtil.calculateAge(String.valueOf(child.getBirth())))
                            .parentName(child.getParentUserEntity().getName())
                            .build()
            );
        }
        return responseDtos;
    }

    @Transactional
    public GetChildResponse getChild(int childUserId, String email) {

        ConsultantUserEntity consultantUser = consultantUserRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        ChildUserEntity childUser = childUserRepository.findByIdAndConsultantUserEntity_IdAndDeleteDttmIsNull(childUserId, consultantUser.getId())
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        return GetChildResponse.builder()
                .childUserId(childUser.getId())
                .profileImgUrl(getFileUrl(FileEntity.TblType.PCD, childUser.getId()))
                .childName(childUser.getName())
                .age(DateUtil.calculateAge(String.valueOf(childUser.getBirth())))
                .gender(childUser.getGender().equals("M") ? "남" : "여")
                .birth(String.valueOf(childUser.getBirth()))
                .firstConsultDate(String.valueOf(childUser.getFirstConsultDt()))
                .interest(childUser.getInterest())
                .additionalInfo(childUser.getAdditionalInfo())
                .parentName(childUser.getParentUserEntity().getName())
                .parentPhone(childUser.getParentUserEntity().getPhone())
                .parentEmail(childUser.getParentUserEntity().getEmail())
                .build();
    }

    public MyProfileResponse getProfile(String email) {
        ConsultantUserEntity consultantUser = consultantUserRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        return MyProfileResponse.builder()
                .profileImgUrl(getFileUrl(FileEntity.TblType.PCT, consultantUser.getId()))
                .name(consultantUser.getName())
                .email(consultantUser.getEmail())
                .phone(consultantUser.getPhone())
                .centerName(consultantUser.getCenter().getCenterName())
                .centerPhone(consultantUser.getCenter().getCenterContact())
                .build();
    }


    @Transactional
    public List<SearchChildResponse> searchChild(String childUserName) {
        List<ChildUserEntity> childUserEntities = childUserRepository.findALlByNameContainingAndDeleteDttmIsNull(childUserName)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        return childUserEntities.stream()
                .map(child -> SearchChildResponse.builder()
                        .childUserId(child.getId())
                        .childProfileUrl(getFileUrl(FileEntity.TblType.PCD, child.getId()))
                        .childUserName(child.getName())
                        .parentUserName(child.getParentUserEntity().getName())
                        .parentUserEmail(child.getParentUserEntity().getEmail())
                        .build())
                .collect(Collectors.toList());
    }

    public ModifyChildResponse modifyChild(ModifyChildRequestDto modifyChildRequestDto) {
        ChildUserEntity childUserEntity = childUserRepository.findById(modifyChildRequestDto.getChildUserId())
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        childUserEntity.setInterest(modifyChildRequestDto.getInterest());
        childUserEntity.setAdditionalInfo(modifyChildRequestDto.getAdditionalInfo());

        return ModifyChildResponse.builder()
                .childUserId(childUserRepository.save(childUserEntity).getId())
                .build();
    }
}
