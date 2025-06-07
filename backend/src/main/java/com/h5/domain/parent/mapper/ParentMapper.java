package com.h5.domain.parent.mapper;

import com.h5.domain.child.entity.ChildUserEntity;
import com.h5.domain.consultant.entity.ConsultantUserEntity;
import com.h5.domain.file.dto.response.GetFileUrlResponseDto;
import com.h5.domain.file.entity.FileEntity;
import com.h5.domain.file.service.FileService;
import com.h5.domain.parent.dto.info.ConsultantInfo;
import com.h5.domain.parent.dto.info.MyChildInfo;
import com.h5.domain.parent.dto.info.MyInfo;
import com.h5.domain.parent.entity.ParentUserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ParentMapper {

    private final FileService fileService;

    /**
     * 부모 사용자 엔티티를 MyInfo DTO로 변환한다.
     *
     * @param parentUserEntity 변환할 부모 사용자 엔티티
     * @return 부모 아이디, 이메일, 이름, 전화번호를 담은 {@link MyInfo}
     */
    public MyInfo buildMyInfo(ParentUserEntity parentUserEntity) {
        return MyInfo.builder()
                .parentId(parentUserEntity.getId())
                .email(parentUserEntity.getEmail())
                .name(parentUserEntity.getName())
                .phone(parentUserEntity.getPhone())
                .build();
    }

    /**
     * 자녀 엔티티 목록을 MyChildInfo DTO 목록으로 변환한다.
     * 각 자녀의 나이를 생년월일 기준으로 계산하고, 프로필 이미지를 FileService를 통해 조회한다.
     * 이미지가 없으면 기본값("Default Image")을 사용한다.
     *
     * @param childUserEntities 변환할 자녀 엔티티 목록
     * @return 자녀 ID, 이름, 나이, 성별, 프로필 이미지 URL을 담은 {@link MyChildInfo} 목록
     */
    public List<MyChildInfo> buildMyChildInfos(List<ChildUserEntity> childUserEntities) {
        List<MyChildInfo> myChildInfos = new ArrayList<>();
        for (ChildUserEntity child : childUserEntities) {
            int age = Period.between(child.getBirth(), LocalDate.now()).getYears();

            List<GetFileUrlResponseDto> files = fileService.getFileUrl(FileEntity.TblType.PCD, child.getId());
            String profileImgUrl = files.isEmpty()
                    ? "Default Image"
                    : files.get(0).getUrl();

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

    /**
     * 상담사 사용자 엔티티를 ConsultantInfo DTO로 변환한다.
     *
     * @param consultantUserEntity 변환할 상담사 사용자 엔티티
     * @return 상담사 ID, 이름, 이메일, 전화번호 및 소속 센터 정보를 담은 {@link ConsultantInfo}
     */
    public ConsultantInfo buildConsultantInfo(ConsultantUserEntity consultantUserEntity) {
        return ConsultantInfo.builder()
                .consultantId(consultantUserEntity.getId())
                .consultantName(consultantUserEntity.getName())
                .consultantPhone(consultantUserEntity.getPhone())
                .consultantEmail(consultantUserEntity.getEmail())
                .centerName(consultantUserEntity.getCenter().getCenterName())
                .centerPhone(consultantUserEntity.getCenter().getCenterContact())
                .build();
    }
}
