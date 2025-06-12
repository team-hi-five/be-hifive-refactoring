package com.h5.domain.schedule.entity;

import com.h5.global.enumerate.Status;

import java.time.LocalDateTime;

public interface BaseMeetingSchedule {
    Integer getId();
    LocalDateTime getStartAt();
    String getSessionId();
    LocalDateTime getEndAt();
    void setStatus(Status status);
    Status getStatus();
    void setEndAt(LocalDateTime endAt);
    void setSessionId(String sessionId);
}
