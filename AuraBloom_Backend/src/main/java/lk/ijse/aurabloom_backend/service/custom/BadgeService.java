package lk.ijse.aurabloom_backend.service.custom;

import lk.ijse.aurabloom_backend.dto.BadgeDTO;
import lk.ijse.aurabloom_backend.entity.BadgeType;

import java.util.List;

public interface BadgeService {
    List<BadgeDTO> getAllBadges(String email);
    BadgeDTO awardBadge(String email, BadgeType badgeType, String title, String description);
    void evaluateMoodBadges(String email);
    void evaluateMeditationBadges(String email);
    void evaluateJournalBadges(String email);
    void evaluateChallengeBadges(String email);
}