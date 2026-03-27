package com.shk.observabilitydemo.repository;

import com.shk.observabilitydemo.entity.AlertRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {}
