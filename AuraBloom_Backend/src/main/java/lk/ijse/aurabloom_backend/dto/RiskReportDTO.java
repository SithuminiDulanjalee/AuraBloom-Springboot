package lk.ijse.aurabloom_backend.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskReportDTO {
    private double emotionalRiskScore;
    private String riskLevel;
    private int totalMoodEntries;
    private int totalJournalEntries;
    private int totalMeditationMinutes;
    private int completedChallengesCount;
    private int badgeCount;
    private Map<String, Long> moodDistribution;
    private List<String> aiInsights;
}