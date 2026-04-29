package com.shay.ai_wrapper_java.controller;

import com.shay.ai_wrapper_java.model.Plan;
import com.example.aiplanner.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DatabaseController {

    @Autowired
    private PlanRepository planRepository;

    @PostMapping("/plans")
    public ResponseEntity<Map<String, Object>> createPlan(@RequestBody Plan plan) {
        Plan saved = planRepository.save(plan);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Plan created successfully");
        response.put("data", saved);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/plans")
    public ResponseEntity<Map<String, Object>> getAllPlans() {
        List<Plan> plans = planRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", plans.size());
        response.put("data", plans);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/plans/{id}")
    public ResponseEntity<Map<String, Object>> getPlanById(@PathVariable Long id) {
        var plan = planRepository.findById(id);
        Map<String, Object> response = new HashMap<>();
        if (plan.isPresent()) {
            response.put("success", true);
            response.put("data", plan.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Plan not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    @GetMapping("/database-status")
    public ResponseEntity<Map<String, Object>> databaseStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("database_connected", true);
        status.put("database_type", "H2");
        status.put("database_file", "./data/devdb.mv.db");
        status.put("total_plans", planRepository.count());
        status.put("message", "Database is ready!");
        return ResponseEntity.ok(status);
    }
}
