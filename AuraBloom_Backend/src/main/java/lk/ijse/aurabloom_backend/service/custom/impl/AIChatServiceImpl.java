package lk.ijse.aurabloom_backend.service.custom.impl;

import lombok.RequiredArgsConstructor;
import lk.ijse.aurabloom_backend.dto.AIChatRequest;
import lk.ijse.aurabloom_backend.dto.AIChatResponse;
import lk.ijse.aurabloom_backend.dto.RiskReportDTO;
import lk.ijse.aurabloom_backend.service.custom.AIChatService;
import lk.ijse.aurabloom_backend.service.custom.RiskEngineService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIChatServiceImpl implements AIChatService {

    private final RiskEngineService riskEngineService;

    @Value("${groq.api.key}")
    private String groqApiKey;

    private WebClient buildClient() {
        return WebClient.builder()
                .baseUrl("https://api.groq.com/openai/v1/chat/completions")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + groqApiKey)
                .build();
    }

    @Override
    public AIChatResponse chat(String email, AIChatRequest request) {
        RiskReportDTO report = riskEngineService.getReport(email);

        String systemPrompt = String.format(
            "You are Bloom AI, a compassionate mental wellness assistant inside the AuraBloom app. " +
            "You have access to this user's current wellness data:\n" +
            "- Emotional risk score: %.1f%% (%s)\n" +
            "- Mood entries logged: %d\n" +
            "- Journal entries: %d\n" +
            "- Weekly meditation minutes: %d\n" +
            "- Completed challenges: %d\n" +
            "- Mood distribution this week: %s\n\n" +
            "Use this context to give personalised, empathetic, and actionable advice. " +
            "Keep replies under 80 words. Never diagnose or replace professional help. " +
            "If the user seems in crisis, gently recommend professional support.",
            report.getEmotionalRiskScore(),
            report.getRiskLevel(),
            report.getTotalMoodEntries(),
            report.getTotalJournalEntries(),
            report.getTotalMeditationMinutes(),
            report.getCompletedChallengesCount(),
            report.getMoodDistribution()
        );

        Map<String, Object> body = Map.of(
            "model", "llama-3.3-70b-versatile",
            "messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", request.getMessage())
            ),
            "max_tokens", 200,
            "temperature", 0.7
        );

        try {
            Map<?, ?> response = buildClient().post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

            if (response != null && response.containsKey("choices")) {
                List<?> choices = (List<?>) response.get("choices");
                Map<?, ?> choice = (Map<?, ?>) choices.get(0);
                Map<?, ?> message = (Map<?, ?>) choice.get("message");
                return new AIChatResponse((String) message.get("content"));
            }
        } catch (Exception e) {
            // Fallback — never leave the user with a blank response
        }
        return new AIChatResponse(
            "I'm having a moment of connection trouble — but I'm here for you. " +
            "Try sharing again in a few seconds."
        );
    }
}