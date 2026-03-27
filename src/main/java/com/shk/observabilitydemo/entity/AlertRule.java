package com.shk.observabilitydemo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "alert_rule")
public class AlertRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;

    private String metricName;

    private Double threshold;

    private String condition;
    // GT = Greater Than
    // LT = Less Than

    private String severity;
    // LOW, MEDIUM, HIGH, CRITICAL

    private Long runbookId;

    // NEW FIELD
    private Integer durationSeconds;
    // how long condition must be true before alert triggers

    // NEW FIELD
    private Boolean enabled = true;
    // allows disabling rule without deleting
}