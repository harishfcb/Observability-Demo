package com.shk.observabilitydemo.repository;

import com.shk.observabilitydemo.entity.OnCallSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface OnCallScheduleRepository extends JpaRepository<OnCallSchedule, Long> {

    @Query("""
        select o from OnCallSchedule o
        where :today between o.startDate and o.endDate
    """)
    Optional<OnCallSchedule> findCurrent(LocalDate today);

}
