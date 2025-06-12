package com.h5.domain.session.repository;

import com.h5.domain.schedule.entity.ConsultMeetingScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultSessionRepository extends JpaRepository<ConsultMeetingScheduleEntity, Integer> {
}
