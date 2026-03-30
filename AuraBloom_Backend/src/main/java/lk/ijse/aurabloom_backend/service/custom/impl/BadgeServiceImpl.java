package lk.ijse.aurabloom_backend.service.custom.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lk.ijse.aurabloom_backend.dto.BadgeDTO;
import lk.ijse.aurabloom_backend.entity.*;
import lk.ijse.aurabloom_backend.exception.CustomException;
import lk.ijse.aurabloom_backend.repository.*;
import lk.ijse.aurabloom_backend.service.custom.BadgeService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BadgeServiceImpl implements BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserRepository userRepository;
    private final MoodEntryRepository moodEntryRepository;
    private final MeditationSessionRepository meditationSessionRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final DailyChallengeRepository dailyChallengeRepository;

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));
    }

    private BadgeDTO toDto(Badge badge) {
        return BadgeDTO.builder()
                .id(badge.getId())
                .badgeType(badge.getBadgeType().name())
                .title(badge.getTitle())
                .description(badge.getDescription())
                .awardedDate(badge.getAwardedDate())
                .build();
    }

    private BadgeDTO saveBadge(User user, BadgeType badgeType, String title, String description) {
        return badgeRepository.findByUserAndBadgeType(user, badgeType)
                .map(this::toDto)
                .orElseGet(() -> {
                    Badge badge = Badge.builder()
                            .badgeType(badgeType)
                            .title(title)
                            .description(description)
                            .awardedDate(LocalDate.now())
                            .user(user)
                            .build();
                    return toDto(badgeRepository.save(badge));
                });
    }

    @Override
    public List<BadgeDTO> getAllBadges(String email) {
        User user = getUser(email);
        return badgeRepository.findByUser(user)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public BadgeDTO awardBadge(String email, BadgeType badgeType, String title, String description) {
        User user = getUser(email);
        return saveBadge(user, badgeType, title, description);
    }

    @Override
    public void evaluateMoodBadges(String email) {
        User user = getUser(email);

        long moodCount = moodEntryRepository.countByUser(user);

        if (moodCount >= 1 && !badgeRepository.existsByUserAndBadgeType(user, BadgeType.FIRST_MOOD)) {
            saveBadge(user, BadgeType.FIRST_MOOD, "First Mood Logged", "You logged your first mood.");
        }

        Set<LocalDate> dates = moodEntryRepository
                .findByUserAndCreatedDateAfter(user, LocalDate.now().minusDays(30))
                .stream()
                .map(MoodEntry::getCreatedDate)
                .collect(Collectors.toSet());

        if (!badgeRepository.existsByUserAndBadgeType(user, BadgeType.MOOD_STREAK_7)
                && hasConsecutiveStreakEndingToday(dates, 7)) {
            saveBadge(user, BadgeType.MOOD_STREAK_7, "7-Day Mood Streak", "You logged moods for 7 consecutive days.");
        }
    }

    @Override
    public void evaluateMeditationBadges(String email) {
        User user = getUser(email);

        long sessionCount = meditationSessionRepository.countByUser(user);

        if (sessionCount >= 1 && !badgeRepository.existsByUserAndBadgeType(user, BadgeType.FIRST_MEDITATION)) {
            saveBadge(user, BadgeType.FIRST_MEDITATION, "First Meditation", "You completed your first meditation session.");
        }

        int weeklyMinutes = meditationSessionRepository
                .findByUserAndSessionDateBetween(user, LocalDate.now().minusDays(6), LocalDate.now())
                .stream()
                .mapToInt(MeditationSession::getDuration)
                .sum();

        if (weeklyMinutes >= 60 && !badgeRepository.existsByUserAndBadgeType(user, BadgeType.MEDITATION_60_MIN_WEEK)) {
            saveBadge(user, BadgeType.MEDITATION_60_MIN_WEEK, "60 Minute Week", "You meditated for 60 minutes in a week.");
        }
    }

    @Override
    public void evaluateJournalBadges(String email) {
        User user = getUser(email);

        long journalCount = journalEntryRepository.countByUser(user);

        if (journalCount >= 1 && !badgeRepository.existsByUserAndBadgeType(user, BadgeType.FIRST_JOURNAL)) {
            saveBadge(user, BadgeType.FIRST_JOURNAL, "First Journal", "You wrote your first journal entry.");
        }

        Set<LocalDate> dates = journalEntryRepository
                .findByUserAndCreatedDateAfter(user, LocalDate.now().minusDays(30))
                .stream()
                .map(JournalEntry::getCreatedDate)
                .collect(Collectors.toSet());

        if (!badgeRepository.existsByUserAndBadgeType(user, BadgeType.JOURNAL_STREAK_7)
                && hasConsecutiveStreakEndingToday(dates, 7)) {
            saveBadge(user, BadgeType.JOURNAL_STREAK_7, "7-Day Journal Streak", "You wrote journals for 7 consecutive days.");
        }
    }

    @Override
    public void evaluateChallengeBadges(String email) {
        User user = getUser(email);

        List<DailyChallenge> completed = dailyChallengeRepository.findByUser(user)
                .stream()
                .filter(DailyChallenge::isCompleted)
                .toList();

        if (completed.size() == 1) {
            saveBadge(user, BadgeType.FIRST_CHALLENGE_COMPLETED, "First Challenge Completed", "You completed your first daily challenge.");
        }

        Set<LocalDate> dates = completed.stream()
                .map(DailyChallenge::getChallengeDate)
                .collect(Collectors.toSet());

        if (hasConsecutiveStreakEndingToday(dates, 7)) {
            saveBadge(user, BadgeType.CHALLENGE_STREAK_7, "7-Day Challenge Streak", "You completed challenges for 7 consecutive days.");
        }
    }

    private boolean hasConsecutiveStreakEndingToday(Set<LocalDate> dates, int requiredDays) {
        LocalDate current = LocalDate.now();
        for (int i = 0; i < requiredDays; i++) {
            if (!dates.contains(current.minusDays(i))) {
                return false;
            }
        }
        return true;
    }

}