package com.example.aiplanner.service;

import com.example.aiplanner.dto.PlanRequest;
import com.example.aiplanner.dto.PlanResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlanService {
    private final Map<String, PlanResponse> store = new ConcurrentHashMap<>();

    private final OpenAiService openAiService;

    @Autowired
    public PlanService(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    public PlanResponse createPlan(PlanRequest req) {
        PlanResponse plan = openAiService.generatePlan(req);
        String id = UUID.randomUUID().toString();
        plan.setId(id);
        store.put(id, plan);
        return plan;
    }

    public PlanResponse getPlan(String id) {
        return store.get(id);
    }
}
