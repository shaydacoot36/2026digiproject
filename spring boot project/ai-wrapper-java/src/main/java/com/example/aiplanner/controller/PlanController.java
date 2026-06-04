package com.example.aiplanner.controller;

import com.example.aiplanner.dto.PlanRequest;
import com.example.aiplanner.dto.PlanResponse;
import com.example.aiplanner.model.Plan;
import com.example.aiplanner.service.PlanService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plans")
@CrossOrigin(origins = "*")
public class PlanController {
    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @PostMapping
    public ResponseEntity<PlanResponse> createPlan(@RequestBody PlanRequest request) {
        PlanResponse plan = planService.createPlan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(plan);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPlans() {
        List<Plan> plans = planService.getAllPlans();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", plans.size());
        response.put("data", plans);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanResponse> getPlan(@PathVariable String id) {
        PlanResponse plan = planService.getPlan(id);
        if (plan == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(plan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePlan(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        if (planService.deletePlan(id)) {
            response.put("success", true);
            response.put("message", "Plan deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Plan not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
