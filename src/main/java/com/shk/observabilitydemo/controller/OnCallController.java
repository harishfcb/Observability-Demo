package com.shk.observabilitydemo.controller;

import com.shk.observabilitydemo.entity.OnCallSchedule;
import com.shk.observabilitydemo.repository.OnCallScheduleRepository;
import com.shk.observabilitydemo.service.OnCallService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oncall")
@RequiredArgsConstructor
public class OnCallController {

    private final OnCallScheduleRepository repo;
    private final OnCallService service;

    @PostMapping
    public OnCallSchedule create(@RequestBody OnCallSchedule s) {
        return repo.save(s);
    }

    @GetMapping("/current")
    public String current() {
        return service.getCurrentEngineer();
    }
}
