package com.shk.observabilitydemo.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MetricService {

    public Map<String, Double> getMetrics() {

        Map<String, Double> metrics = new HashMap<>();

        metrics.put("cpu_usage", 85.0);
        metrics.put("memory_usage", 60.0);
        metrics.put("error_rate", 6.0);

        return metrics;
    }
}