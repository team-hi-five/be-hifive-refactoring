package com.h5.domain.parent.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.domain.child.entity.ChildUserEntity;
import com.h5.domain.child.repository.ChildUserRepository;
import com.h5.domain.consultant.dto.request.RegisterParentAccountDto;
import com.h5.domain.consultant.dto.response.EmailCheckResponse;
import com.h5.domain.consultant.entity.ConsultantUserEntity;
import com.h5.domain.consultant.repository.ConsultantUserRepository;
import com.h5.domain.file.entity.FileEntity;
import com.h5.domain.file.service.FileService;
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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ParentUserService {

    private final ParentUserRepository parentUserRepository;
    private final ConsultantUserRepository consultantUserRepository;
    private final ChildUserRepository childUserRepository;
    private final MailUtil mailUtil;
    private final PasswordEncoder passwordEncoder;
    private final PasswordUtil passwordUtil;
    private final FileService fileService;

    @Autowired
    public ParentUserService(ParentUserRepository parentUserRepository,
                             ConsultantUserRepository consultantUserRepository,
                             ChildUserRepository childUserRepository,
                             MailUtil mailUtil,
                             PasswordEncoder passwordEncoder,
                             PasswordUtil passwordUtil, FileService fileService) {
        this.parentUserRepository = parentUserRepository;
        this.consultantUserRepository = consultantUserRepository;
        this.childUserRepository = childUserRepository;
        this.mailUtil = mailUtil;
        this.passwordEncoder = passwordEncoder;
        this.passwordUtil = passwordUtil;
        this.fileService = fileService;
    }

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
                mailUtil.sendRegistrationEmail(dto.getParentEmail(),
                        dto.getParentEmail(), initPwd);
            } catch (Exception e) {
                throw new BusinessException(DomainErrorCode.MAIL_SEND_FAILED);
            }
        }

        return parentUser.getId();

    }

    @Transactional
    public MyPageResponseDto getMyPageInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String parentEmail = authentication.getName();

        ParentUserEntity parentUserEntity = findByEmailOrThrow(parentEmail);
        MyInfo myInfo = buildMyInfo(parentUserEntity);
        List<MyChildInfo> myChildInfos = buildMyChildInfos(parentUserEntity.getId());
        ConsultantInfo consultantInfo = buildConsultantInfo(parentUserEntity);

        return MyPageResponseDto.builder()
                .myChildren(myChildInfos)
                .myInfo(myInfo)
                .consultantInfo(consultantInfo)
                .build();
    }

    public ParentUserEntity findByEmailOrThrow(String email) {
        return parentUserRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));
    }

    private MyInfo buildMyInfo(ParentUserEntity parentUserEntity) {
        return MyInfo.builder()
                .parentId(parentUserEntity.getId())
                .email(parentUserEntity.getEmail())
                .name(parentUserEntity.getName())
                .phone(parentUserEntity.getPhone())
                .build();
    }

    private List<MyChildInfo> buildMyChildInfos(int parentId) {
        List<ChildUserEntity> childUserEntities = childUserRepository.findByParentUserEntity_IdAndDeleteDttmIsNull(parentId)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        List<MyChildInfo> myChildInfos = new ArrayList<>();
        for (ChildUserEntity child : childUserEntities) {
            int age = Period.between(child.getBirth(), LocalDate.now()).getYears();

            String profileImgUrl = !fileService.getFileUrl(FileEntity.TblType.PCD, child.getId()).isEmpty() ? fileService.getFileUrl(FileEntity.TblType.PCD, child.getId()).get(0).getUrl() : "Default Image";

            myChildInfos.add(MyChildInfo.builder()
                    .childId(child.getId())
                    .profileImgUrl(profileImgUrl)
                    .name(child.getName())
                    .age(age)
                    .gender(child.getGender())
                    .build());
        }
        return myChildInfos;
    }

    private ConsultantInfo buildConsultantInfo(ParentUserEntity parentUserEntity) {
        ConsultantUserEntity consultant = consultantUserRepository.findById(parentUserEntity.getConsultantUserEntity().getId())
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        return ConsultantInfo.builder()
                .consultantId(consultant.getId())
                .consultantName(consultant.getName())
                .consultantPhone(consultant.getPhone())
                .consultantEmail(consultant.getEmail())
                .centerName(consultant.getCenter().getCenterName())
                .centerPhone(consultant.getCenter().getCenterContact())
                .build();
    }

    public ParentUserEntity findId(String name, String phone) {
        return parentUserRepository.findEmailByNameAndPhone(name, phone)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));
    }

    public void updateToTempPwd(String email) {
        ParentUserEntity parentUserEntity = findByEmailOrThrow(email);

        String tempPwd = passwordUtil.generatePassword();
        parentUserEntity.setPwd(passwordEncoder.encode(tempPwd));
        parentUserEntity.setTempPwd(true);

        parentUserRepository.save(parentUserEntity);
        mailUtil.sendTempPasswordEmail(email, tempPwd);
    }

    public void updatePwd(String oldPwd, String newPwd) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        ParentUserEntity parentUserEntity = findByEmailOrThrow(email);

        if (!passwordEncoder.matches(oldPwd, parentUserEntity.getPwd())) {
            throw new IllegalArgumentException("Old password does not match.");
        }

        parentUserEntity.setPwd(passwordEncoder.encode(newPwd));
        parentUserEntity.setTempPwd(false);

        parentUserRepository.save(parentUserEntity);
    }

    public List<MyChildrenResponseDto> myChildren() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String parentEmail = authentication.getName();

        ParentUserEntity parentUserEntity = findByEmailOrThrow(parentEmail);
        List<ChildUserEntity> myChildren = childUserRepository.findAllByParentUserEntity_IdAndDeleteDttmIsNull(parentUserEntity.getId())
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        return myChildren.stream()
                .map(child -> MyChildrenResponseDto.builder()
                        .childUserId(child.getId())
                        .childUserName(child.getName())
                        .build())
                .collect(Collectors.toList());
    }

    public EmailCheckResponse emailCheck(String email) {
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
     * 논리 삭제 메서드
     *
     * @param parentUserId 부모 일련번호
     * @param deleteDttm 삭제 시간
     */
    public void markDeleted(Integer parentUserId, LocalDateTime deleteDttm) {
        ParentUserEntity parentUserEntity = parentUserRepository.findById(parentUserId)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));

        parentUserEntity.setDeleteDttm(deleteDttm);
        parentUserRepository.save(parentUserEntity);
    }

}
