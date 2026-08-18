package com.example.aiplanner.dto;

import java.util.List;

public class QuizResponse {
    private List<QuizQuestion> questions;

    public List<QuizQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuizQuestion> questions) {
        this.questions = questions;
    }
}
