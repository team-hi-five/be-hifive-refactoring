package com.h5.domain.schedule.mapper;

import com.h5.domain.schedule.dto.response.ScheduleResponse;
import com.h5.domain.schedule.entity.ConsultMeetingScheduleEntity;
import com.h5.domain.schedule.entity.GameMeetingScheduleEntity;
import org.springframework.stereotype.Component;

@Component
public class ScheduleMapper {


    public ScheduleResponse ofConsult(ConsultMeetingScheduleEntity e) {
        return ScheduleResponse.builder()
                .scheduleId(e.getId())
                .scheduleAt(e.getScheduleAt())
                .type("consult")
                .consultantName(e.getHost().getName())
                .childUserId(e.getChildUserEntity().getId())
                .childName(e.getChildUserEntity().getName())
                .parentName(e.getParentUserEntity().getName())
                .parentEmail(e.getParentUserEntity().getEmail())
                .status(e.getStatus())
                .build();
    }

    public ScheduleResponse ofGame(GameMeetingScheduleEntity e) {
        var child = e.getChildUserEntity();
        return ScheduleResponse.builder()
                .scheduleId(e.getId())
                .scheduleAt(e.getScheduleAt())
                .type("game")
                .consultantName(e.getHost().getName())
                .childUserId(child.getId())
                .childName(child.getName())
                .parentName(child.getParentUserEntity().getName())
                .parentEmail(child.getParentUserEntity().getEmail())
                .status(e.getStatus())
                .build();
    }
}
