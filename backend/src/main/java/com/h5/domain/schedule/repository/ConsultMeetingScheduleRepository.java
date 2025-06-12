package com.h5.domain.schedule.repository;

import com.h5.domain.schedule.entity.ConsultMeetingScheduleEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultMeetingScheduleRepository extends JpaRepository<ConsultMeetingScheduleEntity, Integer> {

    List<ConsultMeetingScheduleEntity> findAllByChildUserEntity_IdAndScheduleAtBetweenAndEndAtIsNull(
            Integer childUserId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<ConsultMeetingScheduleEntity> findAllByChildUserEntity_IdInAndScheduleAtBetweenAndEndAtIsNull(
            List<Integer> childIds,
            LocalDateTime start,
            LocalDateTime end
    );

    List<ConsultMeetingScheduleEntity> findAllByHost_IdAndScheduleAtBetweenAndEndAtIsNull(
            Integer hostId,
            LocalDateTime start,
            LocalDateTime end
    );

    boolean existsByHost_IdAndScheduleAtAndEndAtIsNull(
            Integer consultantUserId,
            LocalDateTime dateTime
    );

    Optional<ConsultMeetingScheduleEntity> findByIdAndEndAtIsNull(Integer id);
}
