package com.shk.observabilitydemo.repository;

import com.shk.observabilitydemo.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {}
