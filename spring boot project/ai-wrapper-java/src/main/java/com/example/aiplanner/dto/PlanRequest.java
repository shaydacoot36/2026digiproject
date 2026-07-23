package com.example.aiplanner.dto;

import java.util.List;

public class PlanRequest {
    private String goal;
    private Double hoursPerWeek;
    private Integer horizonDays;
    private String level;
    private List<String> availableDays;

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public Double getHoursPerWeek() { return hoursPerWeek; }
    public void setHoursPerWeek(Double hoursPerWeek) { this.hoursPerWeek = hoursPerWeek; }

    public Integer getHorizonDays() { return horizonDays; }
    public void setHorizonDays(Integer horizonDays) { this.horizonDays = horizonDays; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public List<String> getAvailableDays() { return availableDays; }
    public void setAvailableDays(List<String> availableDays) { this.availableDays = availableDays; }
}
