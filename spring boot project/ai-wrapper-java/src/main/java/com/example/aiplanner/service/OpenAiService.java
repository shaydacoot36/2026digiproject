package com.example.aiplanner.service;

import com.example.aiplanner.dto.PlanRequest;
import com.example.aiplanner.dto.PlanResponse;
import com.example.aiplanner.dto.Session;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAiService {
    private final boolean mock = true; // set to false to implement real OpenAI calls later

    public PlanResponse generatePlan(PlanRequest req) {
        // For now return a simple mock plan
        PlanResponse resp = new PlanResponse();
        resp.setGoal(req.getGoal());

        int days = req.getHorizonDays() != null && req.getHorizonDays() > 0 ? req.getHorizonDays() : 14;
        double hoursPerWeek = req.getHoursPerWeek() != null ? req.getHoursPerWeek() : 7;
        double daily = hoursPerWeek / 7.0;

        List<Session> sessions = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ISO_DATE;

        for (int i = 0; i < days; i++) {
            String day = today.plusDays(i).format(fmt);
            String topic = String.format("%s — focus area %d", req.getGoal(), i + 1);
            sessions.add(new Session(day, topic, Math.round(daily * 100.0) / 100.0));
        }

        resp.setSessions(sessions);
        resp.setCreatedAt(LocalDate.now().format(fmt));
        return resp;
    }
}
