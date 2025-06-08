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

    List<ConsultMeetingScheduleEntity>
    findAllByChildUserEntity_IdAndScheduleAt_YearAndScheduleAt_MonthAndDeletedAtIsNull(
            Integer childUserId, Integer year, Month month);

    List<ConsultMeetingScheduleEntity>
    findAllByChildUserEntity_IdInAndScheduleAt_YearAndScheduleAt_MonthAndDeletedAtIsNull(
            List<Integer> childIds, Integer year, Month month);

    List<ConsultMeetingScheduleEntity> findAllByHost_IdAndScheduleAtBetweenAndDeletedAtIsNull(
            Integer hostId,
            LocalDateTime start,
            LocalDateTime end
    );

    boolean existsByHost_IdAndScheduleAtAndDeletedAtIsNull(
            Integer consultantUserId,
            LocalDateTime dateTime
    );

    Optional<ConsultMeetingScheduleEntity> findByIdAndDeletedAtIsNull(Integer id);
}
