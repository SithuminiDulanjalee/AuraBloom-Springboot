package lk.ijse.aurabloom_backend.service.custom.impl;

import lombok.RequiredArgsConstructor;
import lk.ijse.aurabloom_backend.dto.RiskReportDTO;
import lk.ijse.aurabloom_backend.entity.*;
import lk.ijse.aurabloom_backend.exception.CustomException;
import lk.ijse.aurabloom_backend.repository.*;
import lk.ijse.aurabloom_backend.service.custom.RiskEngineService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RiskEngineServiceImpl implements RiskEngineService {

    private final UserRepository userRepository;
    private final MoodEntryRepository moodEntryRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final MeditationSessionRepository meditationSessionRepository;
    private final DailyChallengeRepository dailyChallengeRepository;
    private final BadgeRepository badgeRepository;

    private final RiskReportRepository riskReportRepository;

    @Value("${groq.api.key}")
    private String groqApiKey;

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Override
    public RiskReportDTO getReport(String email) {
        User user = getUser(email);
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);
        LocalDate monthStart = today.minusDays(30);

        List<MoodEntry> allMoods = moodEntryRepository.findByUserOrderByCreatedDateDesc(user);
        List<MoodEntry> monthMoods = moodEntryRepository.findByUserAndCreatedDateAfter(user, monthStart);

        Map<String, Long> moodDist = new LinkedHashMap<>();
        for (MoodType t : MoodType.values()) moodDist.put(t.name(), 0L);
        moodEntryRepository.findByUserAndCreatedDateBetween(user, weekStart, today)
                .forEach(m -> moodDist.merge(m.getMoodType().name(), 1L, Long::sum));

        double riskScore = 0.0;
        if (!monthMoods.isEmpty()) {
            long neg = monthMoods.stream()
                    .filter(m -> m.getMoodType() == MoodType.SAD || m.getMoodType() == MoodType.STRESSED)
                    .count();
            riskScore = (neg * 100.0) / monthMoods.size();
        }

        long journalCount = journalEntryRepository.countByUser(user);

        int weeklyMinutes = meditationSessionRepository
                .findByUserAndSessionDateBetween(user, weekStart, today)
                .stream().mapToInt(MeditationSession::getDuration).sum();

        long completedChallenges = dailyChallengeRepository.findByUser(user)
                .stream().filter(DailyChallenge::isCompleted).count();

        long badgeCount = badgeRepository.findByUser(user).size();

        List<String> insights = generateInsights(riskScore, (int) allMoods.size(),
                (int) journalCount, weeklyMinutes, (int) completedChallenges, moodDist);

        String riskLevel = riskScore < 25 ? "LOW"
                : riskScore < 50 ? "MODERATE"
                  : riskScore < 75 ? "ELEVATED" : "HIGH";

        return RiskReportDTO.builder()
                .emotionalRiskScore(Math.round(riskScore * 10.0) / 10.0)
                .riskLevel(riskLevel)
                .totalMoodEntries((int) allMoods.size())
                .totalJournalEntries((int) journalCount)
                .totalMeditationMinutes(weeklyMinutes)
                .completedChallengesCount((int) completedChallenges)
                .badgeCount((int) badgeCount)
                .moodDistribution(moodDist)
                .aiInsights(insights)
                .build();
    }

    
    @Override
    public void saveRiskAssessment(String email, RiskReportDTO riskReportDTO) {
        User user = getUser(email);

        RiskReport riskReport = new RiskReport();
        riskReport.setUser(user);
        riskReport.setEmotionalRiskScore(riskReportDTO.getEmotionalRiskScore());
        riskReport.setRiskLevel(riskReportDTO.getRiskLevel());
        riskReport.setTotalMoodEntries(riskReportDTO.getTotalMoodEntries());
        riskReport.setTotalJournalEntries(riskReportDTO.getTotalJournalEntries());
        riskReport.setTotalMeditationMinutes(riskReportDTO.getTotalMeditationMinutes());
        riskReport.setCompletedChallengesCount(riskReportDTO.getCompletedChallengesCount());
        riskReport.setBadgeCount(riskReportDTO.getBadgeCount());
        riskReport.setAiInsights(riskReportDTO.getAiInsights());

        riskReport.setGeneratedAt(LocalDate.now());

        riskReportRepository.save(riskReport);
    }

    private List<String> generateInsights(double riskScore, int moods, int journals,
                                          int meditationMins, int challenges,
                                          Map<String, Long> moodDist) {

        String prompt = String.format(
                "You are a mental wellness analyst. Generate exactly 3 concise insight sentences " +
                        "(max 18 words each) for a user with this data:\n" +
                        "- Emotional risk score: %.1f%%\n" +
                        "- Mood entries: %d\n" +
                        "- Journal entries: %d\n" +
                        "- Weekly meditation minutes: %d\n" +
                        "- Completed challenges: %d\n" +
                        "- Mood distribution: %s\n" +
                        "Format: return a JSON array of 3 strings only. No markdown, no keys, just the array.",
                riskScore, moods, journals, meditationMins, challenges, moodDist
        );

        Map<String, Object> body = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "You are a wellness insight engine. Respond ONLY with a valid JSON array of exactly 3 strings."),
                        Map.of("role", "user", "content", prompt)
                ),
                "max_tokens", 200,
                "temperature", 0.5
        );

        try {
            WebClient client = WebClient.builder()
                    .baseUrl("https://api.groq.com/openai/v1/chat/completions")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + groqApiKey)
                    .build();

            Map<?, ?> response = client.post()
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("choices")) {
                List<?> choices = (List<?>) response.get("choices");
                Map<?, ?> choice = (Map<?, ?>) choices.get(0);
                Map<?, ?> message = (Map<?, ?>) choice.get("message");
                String raw = ((String) message.get("content")).trim();
                raw = raw.replaceAll("```json|```", "").trim();
                raw = raw.replaceAll("^\\[|]$", "").trim();
                String[] parts = raw.split("\",\\s*\"");
                List<String> result = new ArrayList<>();
                for (String p : parts) {
                    result.add(p.replaceAll("^\"|\"$", "").trim());
                }
                if (result.size() >= 2) return result.subList(0, Math.min(3, result.size()));
            }
        } catch (Exception ignored) {}

        return List.of(
                "Your mood patterns suggest elevated stress — consider a short breathing break.",
                "Regular journaling is linked to lower anxiety. Keep it up!",
                "Completing daily challenges builds emotional resilience over time."
        );
    }
}