package com.example.aiplanner.service;

import com.example.aiplanner.dto.PlanRequest;
import com.example.aiplanner.dto.PlanResponse;
import com.example.aiplanner.dto.Session;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class OpenAiService {

    public PlanResponse generatePlan(PlanRequest req) {
        PlanResponse resp = new PlanResponse();
        resp.setGoal(req.getGoal());

        int days = req.getHorizonDays() != null && req.getHorizonDays() > 0 ? req.getHorizonDays() : 14;
        double hoursPerWeek = req.getHoursPerWeek() != null && req.getHoursPerWeek() > 0 ? req.getHoursPerWeek() : 7;
        double totalHours = hoursPerWeek * days / 7.0;

        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ISO_DATE;
        List<LocalDate> availableDates = getAvailableDates(today, days, req.getAvailableDays());

        // Aim for manageable 60-90 minute blocks, while still allocating the requested total time.
        int sessionCount = Math.max(availableDates.size(), (int) Math.ceil(totalHours / 1.5));
        double sessionHours = roundHours(totalHours / sessionCount);
        List<Session> sessions = new ArrayList<>();

        for (int i = 0; i < sessionCount; i++) {
            LocalDate date = availableDates.get(i % availableDates.size());
            sessions.add(new Session(date.format(fmt), buildActivity(req.getGoal(), i, sessionCount), sessionHours));
        }

        resp.setSessions(sessions);
        resp.setCreatedAt(LocalDate.now().format(fmt));
        return resp;
    }

    private List<LocalDate> getAvailableDates(LocalDate start, int horizonDays, List<String> requestedDays) {
        Set<DayOfWeek> availableDays = EnumSet.noneOf(DayOfWeek.class);
        if (requestedDays != null) {
            for (String requestedDay : requestedDays) {
                try {
                    availableDays.add(DayOfWeek.valueOf(requestedDay.trim().toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException | NullPointerException ignored) {
                    // Invalid values are ignored; the safe weekday default is used below if needed.
                }
            }
        }
        if (availableDays.isEmpty()) {
            availableDays = EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
        }

        List<LocalDate> dates = new ArrayList<>();
        for (int offset = 0; offset < horizonDays; offset++) {
            LocalDate date = start.plusDays(offset);
            if (availableDays.contains(date.getDayOfWeek())) dates.add(date);
        }
        return dates.isEmpty() ? List.of(start) : dates;
    }

    private String buildActivity(String goal, int sessionIndex, int totalSessions) {
        if (sessionIndex == totalSessions - 1) return goal + " — final exam-style review and weak-area check";
        return switch (sessionIndex % 4) {
            case 0 -> goal + " — learn key concepts and make concise notes";
            case 1 -> goal + " — practise questions and worked examples";
            case 2 -> goal + " — active recall without notes";
            default -> goal + " — review earlier material and correct mistakes";
        };
    }

    private double roundHours(double hours) {
        return Math.round(hours * 100.0) / 100.0;
    }
}
