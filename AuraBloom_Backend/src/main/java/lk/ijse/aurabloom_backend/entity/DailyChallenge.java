package lk.ijse.aurabloom_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "daily_challenges")
public class DailyChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private int points;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeType challengeType;

    @Column(nullable = false)
    private LocalDate challengeDate;

    @Column(nullable = false)
    private boolean completed;

    private LocalDate completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}