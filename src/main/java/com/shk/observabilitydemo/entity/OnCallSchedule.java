package com.shk.observabilitydemo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name="on_call_schedules")
public class OnCallSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String engineerName;

    private LocalDate startDate;

    private LocalDate endDate;
}
