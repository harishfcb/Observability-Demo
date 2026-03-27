package com.shk.observabilitydemo.repository;

import com.shk.observabilitydemo.entity.Runbook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunbookRepository extends JpaRepository<Runbook, Long> {}
