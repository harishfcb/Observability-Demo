package com.shk.observabilitydemo.service;

import com.shk.observabilitydemo.entity.Alert;
import com.shk.observabilitydemo.entity.AlertRule;
import com.shk.observabilitydemo.entity.Runbook;
import com.shk.observabilitydemo.repository.AlertRepository;
import com.shk.observabilitydemo.repository.AlertRuleRepository;
import com.shk.observabilitydemo.repository.RunbookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertRuleRepository ruleRepo;
    private final AlertRepository alertRepo;
    private final RunbookRepository runbookRepo;
    private final OnCallService onCallService;

    // stores when violation started
    private final Map<String, LocalDateTime> violationStartMap = new HashMap<>();

    // prevents duplicate alerts
    private final Set<String> activeAlerts = new HashSet<>();


    public void evaluate(Map<String, Double> metrics) {

        List<AlertRule> rules = ruleRepo.findAll();

        for (AlertRule rule : rules) {

            // skip disabled rules
            if (!Boolean.TRUE.equals(rule.getEnabled())) {
                continue;
            }

            Double value = metrics.get(rule.getMetricName());

            if (value == null) {
                continue;
            }

            boolean conditionMatched =
                    isConditionMatched(rule, value);

            if (conditionMatched) {

                handleViolation(rule, value);

            } else {

                handleRecovery(rule);
            }

        }
    }


    private boolean isConditionMatched(
            AlertRule rule,
            Double value
    ) {

        return switch (rule.getCondition()) {
            case "GT" -> value > rule.getThreshold();
            case "LT" -> value < rule.getThreshold();
            default -> false;
        };
    }


    private void handleViolation(
            AlertRule rule,
            Double value
    ) {

        String ruleKey = rule.getName();

        LocalDateTime violationStart =
                violationStartMap.get(ruleKey);

        // first time violation seen
        if (violationStart == null) {

            violationStartMap.put(
                    ruleKey,
                    LocalDateTime.now()
            );

            return;
        }

        long seconds =
                Duration.between(
                        violationStart,
                        LocalDateTime.now()
                ).getSeconds();

        int requiredDuration =
                rule.getDurationSeconds() != null
                        ? rule.getDurationSeconds()
                        : 0;

        // violation persisted long enough
        if (seconds >= requiredDuration) {

            if (!activeAlerts.contains(ruleKey)) {

                createAlert(rule, value);

                activeAlerts.add(ruleKey);
            }

        }

    }


    private void handleRecovery(AlertRule rule) {

        String ruleKey = rule.getName();

        violationStartMap.remove(ruleKey);

        // mark alert resolved
        if (activeAlerts.contains(ruleKey)) {

            log.info(
                    "RESOLVED: {}",
                    rule.getName()
            );

            activeAlerts.remove(ruleKey);
        }

    }


    private void createAlert(
            AlertRule rule,
            Double value
    ) {

        var alert = new Alert();

        alert.setMetricName(rule.getMetricName());

        alert.setMetricValue(value);

        alert.setSeverity(rule.getSeverity());

        alert.setCreatedAt(LocalDateTime.now());

        Runbook runbook =
                runbookRepo.findById(
                        rule.getRunbookId()
                ).orElse(null);

        String engineer =
                onCallService.getCurrentEngineer();

        String message =
                "ALERT: " + rule.getName()
                        + " value=" + value
                        + " onCall=" + engineer
                        + " runbook="
                        + (runbook != null
                        ? runbook.getTitle()
                        : "none");

        alert.setMessage(message);

        alertRepo.save(alert);

        log.error(message);
    }


    public List<Alert> getAllAlerts() {

        return alertRepo.findAll();
    }

}