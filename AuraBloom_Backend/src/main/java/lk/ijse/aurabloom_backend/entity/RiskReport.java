package lk.ijse.aurabloom_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RiskReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private double emotionalRiskScore;
    private String riskLevel;
    private int totalMoodEntries;
    private int totalJournalEntries;
    private int totalMeditationMinutes;
    private int completedChallengesCount;
    private int badgeCount;

    private LocalDate generatedAt;

    @ElementCollection
    private List<String> aiInsights;
}