package com.example.aiplanner.service;

import com.example.aiplanner.dto.QuizResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuizGenerationServiceTest {

    @Test
    void generateQuiz_returnsQuestionsFromNotesWhenNoApiKeyIsConfigured() {
        QuizGenerationService service = new QuizGenerationService();

        QuizResponse response = service.generateQuiz(
                "Photosynthesis is the process plants use to turn sunlight into chemical energy. " +
                        "The chloroplast captures light energy and stores it in glucose. " +
                        "Plants need water, carbon dioxide, and sunlight for this process.",
                3
        );

        assertNotNull(response);
        assertNotNull(response.getQuestions());
        assertTrue(response.getQuestions().size() > 0);
        assertTrue(response.getQuestions().size() <= 3);
    }
}
