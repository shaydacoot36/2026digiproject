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
        int sessionCount = Math.min(availableDates.size(), Math.max(1, (int) Math.ceil(totalHours / 1.25)));
        double sessionHours = roundHours(totalHours / sessionCount);
        List<Session> sessions = new ArrayList<>();

        for (int i = 0; i < sessionCount; i++) {
            LocalDate date = availableDates.get(i % availableDates.size());
            sessions.add(new Session(date.format(fmt), buildActivity(req.getGoal(), i, sessionCount, sessionHours), sessionHours));
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

    private String buildActivity(String goal, int sessionIndex, int totalSessions, double sessionHours) {
        String subject = goal == null ? "your subject" : goal.trim();
        String subjectType = subject.toLowerCase(Locale.ROOT);
        String task = switch (subjectType) {
            case String value when value.contains("math") || value.contains("calculus") || value.contains("algebra") -> switch (sessionIndex % 4) {
                case 0 -> "Learn one formula set, write a worked example, and solve 6 basic problems";
                case 1 -> "Complete 8 mixed problems and mark every step you were unsure about";
                case 2 -> "Redo missed problems without notes, then explain the method aloud";
                default -> "Complete a timed exam question and correct it using the marking schedule";
            };
            case String value when value.contains("english") || value.contains("history") || value.contains("geography") -> switch (sessionIndex % 4) {
                case 0 -> "Read one source or text section and make a 5-point evidence summary";
                case 1 -> "Plan one paragraph or essay response using specific evidence";
                case 2 -> "Practise recalling key terms, dates, quotes, and cause-and-effect links";
                default -> "Write one timed response, then improve it against the assessment criteria";
            };
            case String value when value.contains("biology") || value.contains("chemistry") || value.contains("physics") || value.contains("science") -> switch (sessionIndex % 4) {
                case 0 -> "Learn the core process, draw it from memory, and label the key terms";
                case 1 -> "Complete 6 application questions and explain why each answer is correct";
                case 2 -> "Make a cause-and-effect concept map, then test yourself without notes";
                default -> "Complete a past-paper question and review the marking points you missed";
            };
            default -> switch (sessionIndex % 4) {
                case 0 -> "Learn the key concepts and make a one-page summary";
                case 1 -> "Complete practice questions and record the topics you found difficult";
                case 2 -> "Use active recall to explain the topic without looking at your notes";
                default -> "Complete an exam-style task and correct your mistakes";
            };
        };

        if (sessionIndex == totalSessions - 1) {
            task = "Complete a final exam-style review, focusing on your weakest areas";
        }
        return subject + " — " + task + " (" + formatHours(sessionHours) + " hours)";
    }

    private String formatHours(double hours) {
        return String.valueOf(Math.round(hours * 10.0) / 10.0);
    }

    private double roundHours(double hours) {
        return Math.round(hours * 100.0) / 100.0;
    }
}
