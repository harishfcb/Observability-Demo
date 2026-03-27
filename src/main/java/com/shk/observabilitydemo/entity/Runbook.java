package com.shk.observabilitydemo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="runbooks")
public class Runbook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String steps;

}