package lk.ijse.aurabloom_backend.service.custom.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lk.ijse.aurabloom_backend.dto.DailyChallengeDTO;
import lk.ijse.aurabloom_backend.entity.*;
import lk.ijse.aurabloom_backend.exception.CustomException;
import lk.ijse.aurabloom_backend.repository.DailyChallengeRepository;
import lk.ijse.aurabloom_backend.repository.UserRepository;
import lk.ijse.aurabloom_backend.service.custom.BadgeService;
import lk.ijse.aurabloom_backend.service.custom.DailyChallengeService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DailyChallengeServiceImpl implements DailyChallengeService {

    private final DailyChallengeRepository dailyChallengeRepository;
    private final UserRepository userRepository;
    private final BadgeService badgeService;

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));
    }

    private DailyChallengeDTO toDto(DailyChallenge challenge) {
        return DailyChallengeDTO.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .points(challenge.getPoints())
                .challengeType(challenge.getChallengeType().name())
                .challengeDate(challenge.getChallengeDate())
                .completed(challenge.isCompleted())
                .completedAt(challenge.getCompletedAt())
                .build();
    }

    private ChallengeType parseChallengeType(String type) {
        try {
            return ChallengeType.valueOf(type.trim().toUpperCase());
        } catch (Exception e) {
            throw new CustomException("Invalid challenge type");
        }
    }

    private void validateDto(DailyChallengeDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new CustomException("Title is required");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            throw new CustomException("Description is required");
        }
        if (dto.getChallengeType() == null || dto.getChallengeType().trim().isEmpty()) {
            throw new CustomException("Challenge type is required");
        }
        if (dto.getPoints() < 1 || dto.getPoints() > 1000) {
            throw new CustomException("Points must be between 1 and 1000");
        }
        if (dto.getChallengeDate() == null) {
            throw new CustomException("Challenge date is required");
        }
    }

    private DailyChallenge toEntity(User user, DailyChallengeDTO dto) {
        return DailyChallenge.builder()
                .title(dto.getTitle().trim())
                .description(dto.getDescription().trim())
                .points(dto.getPoints())
                .challengeType(parseChallengeType(dto.getChallengeType()))
                .challengeDate(dto.getChallengeDate())
                .completed(false)
                .completedAt(null)
                .user(user)
                .build();
    }

    private List<DailyChallenge> createDefaultChallenges(User user, LocalDate today) {
        return List.of(
                DailyChallenge.builder()
                        .title("Log Your Mood")
                        .description("Record how you feel today.")
                        .points(10)
                        .challengeType(ChallengeType.MOOD)
                        .challengeDate(today)
                        .completed(false)
                        .user(user)
                        .build(),
                DailyChallenge.builder()
                        .title("Meditate for 10 Minutes")
                        .description("Complete at least 10 minutes of meditation.")
                        .points(15)
                        .challengeType(ChallengeType.MEDITATION)
                        .challengeDate(today)
                        .completed(false)
                        .user(user)
                        .build(),
                DailyChallenge.builder()
                        .title("Write a Journal Entry")
                        .description("Reflect on your day in your journal.")
                        .points(15)
                        .challengeType(ChallengeType.JOURNAL)
                        .challengeDate(today)
                        .completed(false)
                        .user(user)
                        .build(),
                DailyChallenge.builder()
                        .title("Take a 5 Minute Breath Break")
                        .description("Pause and breathe mindfully for 5 minutes.")
                        .points(10)
                        .challengeType(ChallengeType.GENERAL)
                        .challengeDate(today)
                        .completed(false)
                        .user(user)
                        .build()
        );
    }

    @Override
    public List<DailyChallengeDTO> getTodayChallenges(String email) {
        User user = getUser(email);
        LocalDate today = LocalDate.now();

        List<DailyChallenge> challenges = dailyChallengeRepository.findByUserAndChallengeDate(user, today);

        if (challenges.isEmpty()) {
            challenges = dailyChallengeRepository.saveAll(createDefaultChallenges(user, today));
        }

        return challenges.stream().map(this::toDto).toList();
    }

    @Override
    public List<DailyChallengeDTO> getAllChallenges(String email) {
        User user = getUser(email);
        return dailyChallengeRepository.findByUser(user)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public DailyChallengeDTO createChallenge(String email, DailyChallengeDTO dto) {
        User user = getUser(email);
        validateDto(dto);

        if (dailyChallengeRepository.existsByUserAndChallengeDateAndTitleIgnoreCase(user, dto.getChallengeDate(), dto.getTitle().trim())) {
            throw new CustomException("Challenge with the same title already exists for this date");
        }

        DailyChallenge challenge = toEntity(user, dto);
        return toDto(dailyChallengeRepository.save(challenge));
    }

    @Override
    public DailyChallengeDTO updateChallenge(String email, Long id, DailyChallengeDTO dto) {
        User user = getUser(email);
        validateDto(dto);

        DailyChallenge challenge = dailyChallengeRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new CustomException("Daily challenge not found"));

        boolean duplicateTitle = dailyChallengeRepository.existsByUserAndChallengeDateAndTitleIgnoreCase(user, dto.getChallengeDate(), dto.getTitle().trim());
        if (duplicateTitle &&
                (!challenge.getTitle().equalsIgnoreCase(dto.getTitle().trim())
                        || !challenge.getChallengeDate().equals(dto.getChallengeDate()))) {
            throw new CustomException("Another challenge already exists with the same title on this date");
        }

        challenge.setTitle(dto.getTitle().trim());
        challenge.setDescription(dto.getDescription().trim());
        challenge.setPoints(dto.getPoints());
        challenge.setChallengeType(parseChallengeType(dto.getChallengeType()));
        challenge.setChallengeDate(dto.getChallengeDate());

        return toDto(dailyChallengeRepository.save(challenge));
    }

    @Override
    public DailyChallengeDTO completeChallenge(String email, Long id) {
        User user = getUser(email);

        DailyChallenge challenge = dailyChallengeRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new CustomException("Daily challenge not found"));

        if (!challenge.isCompleted()) {
            challenge.setCompleted(true);
            challenge.setCompletedAt(LocalDate.now());
            dailyChallengeRepository.save(challenge);
            badgeService.evaluateChallengeBadges(email);
        }

        return toDto(challenge);
    }

    @Override
    public void deleteChallenge(String email, Long id) {
        User user = getUser(email);

        DailyChallenge challenge = dailyChallengeRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new CustomException("Daily challenge not found"));

        dailyChallengeRepository.delete(challenge);
    }
}