package com.example.aiplanner.service;

import com.example.aiplanner.dto.PlanRequest;
import com.example.aiplanner.dto.PlanResponse;
import com.example.aiplanner.model.Plan;
import com.example.aiplanner.repository.PlanRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class PlanService {
    private final OpenAiService openAiService;
    private final PlanRepository planRepository;

    public PlanService(OpenAiService openAiService, PlanRepository planRepository) {
        this.openAiService = openAiService;
        this.planRepository = planRepository;
    }

    public PlanResponse createPlan(PlanRequest req) {
        // Generate plan from AI service
        PlanResponse plan = openAiService.generatePlan(req);
        
        // Save to database
        Plan dbPlan = new Plan();
        dbPlan.setTitle(req.getGoal());
        dbPlan.setDescription(String.format("Plan for %s over %d days, %d hours/week", 
            req.getGoal(), req.getHorizonDays(), req.getHoursPerWeek()));
        dbPlan.setCreatedAt(OffsetDateTime.now());
        
        Plan saved = planRepository.save(dbPlan);
        
        // Update response with database ID
        plan.setId(saved.getId().toString());
        plan.setCreatedAt(saved.getCreatedAt().toString());
        
        return plan;
    }

    public PlanResponse getPlan(String id) {
        try {
            Long planId = Long.parseLong(id);
            return planRepository.findById(planId)
                .map(dbPlan -> {
                    PlanResponse response = new PlanResponse();
                    response.setId(dbPlan.getId().toString());
                    response.setGoal(dbPlan.getTitle());
                    response.setCreatedAt(dbPlan.getCreatedAt().toString());
                    return response;
                })
                .orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }

    public boolean deletePlan(String id) {
        try {
            Long planId = Long.parseLong(id);
            if (planRepository.existsById(planId)) {
                planRepository.deleteById(planId);
                return true;
            }
        } catch (NumberFormatException e) {
            // Invalid ID format
        }
        return false;
    }
}
