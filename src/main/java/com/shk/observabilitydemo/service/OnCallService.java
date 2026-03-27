package com.shk.observabilitydemo.service;

import com.shk.observabilitydemo.entity.OnCallSchedule;
import com.shk.observabilitydemo.repository.OnCallScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class OnCallService {

    private final OnCallScheduleRepository repo;

    public String getCurrentEngineer() {

        return repo.findCurrent(LocalDate.now())
                .map(OnCallSchedule::getEngineerName)
                .orElse("No Engineer Assigned");
    }
}
