package com.example.aiplanner.controller;

import com.example.aiplanner.dto.QuizRequest;
import com.example.aiplanner.dto.QuizResponse;
import com.example.aiplanner.service.QuizGenerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class QuizController {
    private final QuizGenerationService quizGenerationService;

    public QuizController(QuizGenerationService quizGenerationService) {
        this.quizGenerationService = quizGenerationService;
    }

    @PostMapping("/quiz")
    public ResponseEntity<QuizResponse> generateQuiz(@RequestBody QuizRequest request) {
        QuizResponse response = quizGenerationService.generateQuiz(request);
        return ResponseEntity.ok(response);
    }
}
