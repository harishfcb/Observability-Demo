package com.shk.observabilitydemo.controller;

import com.shk.observabilitydemo.entity.Runbook;
import com.shk.observabilitydemo.repository.RunbookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/runbooks")
@RequiredArgsConstructor
public class RunbookController {

    private final RunbookRepository repo;

    @PostMapping
    public Runbook create(@RequestBody Runbook r) {
        return repo.save(r);
    }

    @GetMapping
    public List<Runbook> getAll() {
        return repo.findAll();
    }
}
