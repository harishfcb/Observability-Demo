package com.shk.observabilitydemo.controller;

import com.shk.observabilitydemo.entity.Alert;
import com.shk.observabilitydemo.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService service;

    @GetMapping
    public List<Alert> getAlerts() {
        return service.getAllAlerts();
    }

}
