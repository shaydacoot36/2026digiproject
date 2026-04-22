package com.example.aiplanner.dto;

public class Session {
    private String day;
    private String topic;
    private Double durationHours;

    public Session() {}

    public Session(String day, String topic, Double durationHours) {
        this.day = day;
        this.topic = topic;
        this.durationHours = durationHours;
    }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public Double getDurationHours() { return durationHours; }
    public void setDurationHours(Double durationHours) { this.durationHours = durationHours; }
}
