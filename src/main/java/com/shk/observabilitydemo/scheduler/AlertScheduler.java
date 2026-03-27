package com.shk.observabilitydemo.scheduler;

import com.shk.observabilitydemo.service.AlertService;
import com.shk.observabilitydemo.service.MetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AlertScheduler {

    private final MetricService metricService;
    private final AlertService alertService;

    @Scheduled(fixedRate = 15000)
    public void runCheck() {

        Map<String, Double> metrics =
                metricService.getMetrics();

        alertService.evaluate(metrics);
    }
}
