package com.example.aiplanner.service;

import com.example.aiplanner.dto.QuizQuestion;
import com.example.aiplanner.dto.QuizRequest;
import com.example.aiplanner.dto.QuizResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class QuizGenerationService {
    private static final Pattern SENTENCE_SPLIT = Pattern.compile("(?<=[.!?])\\s+");

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final Environment environment;

    @Value("${ai.quiz.provider:local}")
    private String provider;

    @Value("${ai.quiz.api-key:}")
    private String apiKey;

    public QuizGenerationService(Environment environment) {
        this.environment = environment;
        this.restClient = RestClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    public QuizResponse generateQuiz(String notes, int questionCount) {
        if (notes == null || notes.trim().isEmpty()) {
            return new QuizResponse();
        }

        String providerName = provider == null ? "local" : provider.trim().toLowerCase(Locale.ROOT);
        if ("groq".equals(providerName) || "openai".equals(providerName) || "hf".equals(providerName) || "huggingface".equals(providerName)) {
            if (apiKey == null || apiKey.isBlank()) {
                return generateLocalQuiz(notes, Math.max(1, Math.min(questionCount, 5)));
            }
            try {
                return generateRemoteQuiz(notes, Math.max(1, Math.min(questionCount, 5)));
            } catch (Exception e) {
                return generateLocalQuiz(notes, Math.max(1, Math.min(questionCount, 5)));
            }
        }

        return generateLocalQuiz(notes, Math.max(1, Math.min(questionCount, 5)));
    }

    public QuizResponse generateQuiz(QuizRequest request) {
        if (request == null || request.getNotes() == null || request.getNotes().isBlank()) {
            QuizResponse response = new QuizResponse();
            response.setQuestions(List.of());
            return response;
        }
        return generateQuiz(request.getNotes(), request.getQuestionCount());
    }

    private QuizResponse generateLocalQuiz(String notes, int questionCount) {
        List<String> sentences = parseSentences(notes);
        if (sentences.isEmpty()) {
            return emptyQuiz();
        }

        List<QuizQuestion> questions = new ArrayList<>();
        int totalQuestions = Math.min(questionCount, Math.max(1, Math.min(5, sentences.size())));

        for (int i = 0; i < totalQuestions; i++) {
            String correctSentence = sentences.get(i % sentences.size());
            List<String> distractors = new ArrayList<>();
            for (String sentence : sentences) {
                if (!sentence.equals(correctSentence) && distractors.size() < 3) {
                    distractors.add(sentence);
                }
            }
            if (distractors.size() < 3) {
                distractors.addAll(List.of(
                        "This idea is not covered in the notes.",
                        "This concept only applies to another subject.",
                        "This is a general statement that does not match the notes."
                ));
            }

            List<String> options = new ArrayList<>(distractors.subList(0, Math.min(3, distractors.size())));
            options.add(correctSentence);
            Collections.shuffle(options);

            String question = "Which statement best matches the main idea in the notes?";
            QuizQuestion quizQuestion = new QuizQuestion();
            quizQuestion.setQuestion(question);
            quizQuestion.setOptions(options);
            quizQuestion.setCorrectAnswer(correctSentence);
            quizQuestion.setExplanation("The best answer reflects the idea described most directly in the provided notes.");
            questions.add(quizQuestion);
        }

        QuizResponse response = new QuizResponse();
        response.setQuestions(questions);
        return response;
    }

    private QuizResponse generateRemoteQuiz(String notes, int questionCount) {
        String model = environment.getProperty("ai.quiz.model", "openai/gpt-oss-20b");
        String url = environment.getProperty("ai.quiz.endpoint", "https://api.groq.com/openai/v1/chat/completions");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String prompt = "Generate exactly " + questionCount + " multiple-choice quiz questions from the notes below. " +
                "Return valid JSON only in this shape: {\"questions\":[{\"question\":\"...\",\"options\":[\"...\",\"...\",\"...\",\"...\"],\"correctAnswer\":\"...\",\"explanation\":\"...\"}]}\n\nNotes:\n" + notes;

        String requestBody = "{\"model\":\"" + model + "\",\"messages\":[{\"role\":\"user\",\"content\":\"" + escapeJson(prompt) + "\"}],\"temperature\":0.2}";

        String response = restClient.post()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        try {
            var rootNode = objectMapper.readTree(response);
            String result = rootNode.path("choices").path(0).path("message").path("content").asText();
            if (result == null || result.isBlank()) {
                throw new IllegalStateException("No AI content returned");
            }

            String cleaned = result.trim();
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.replaceFirst("^```(?:json)?\\s*", "").replaceFirst("\\s*```$", "");
            }

            QuizResponse parsed = objectMapper.readValue(cleaned, QuizResponse.class);
            return parsed == null || parsed.getQuestions() == null || parsed.getQuestions().isEmpty() ? generateLocalQuiz(notes, questionCount) : parsed;
        } catch (Exception e) {
            return generateLocalQuiz(notes, questionCount);
        }
    }

    private List<String> parseSentences(String notes) {
        if (notes == null || notes.isBlank()) {
            return List.of();
        }

        List<String> sentences = new ArrayList<>();
        String[] parts = SENTENCE_SPLIT.split(notes);
        for (String part : parts) {
            String cleaned = part.trim();
            if (cleaned.length() > 25) {
                sentences.add(cleaned.replaceAll("\\s+", " "));
            }
        }

        return sentences.isEmpty() ? List.of(notes.trim()) : sentences;
    }

    private QuizResponse emptyQuiz() {
        QuizResponse response = new QuizResponse();
        response.setQuestions(List.of());
        return response;
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
