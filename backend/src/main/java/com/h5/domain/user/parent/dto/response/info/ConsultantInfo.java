package com.h5.domain.user.parent.dto.response.info;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class ConsultantInfo {
    private Integer consultantId;
    private String consultantName;
    private String consultantPhone;
    private String consultantEmail;
    private String centerName;
    private String centerPhone;
}
