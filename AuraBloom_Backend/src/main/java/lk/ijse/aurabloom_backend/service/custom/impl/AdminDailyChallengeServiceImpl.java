package lk.ijse.aurabloom_backend.service.custom.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lk.ijse.aurabloom_backend.dto.AdminDailyChallengeDTO;
import lk.ijse.aurabloom_backend.entity.AdminDailyChallenge;
import lk.ijse.aurabloom_backend.entity.ChallengeType;
import lk.ijse.aurabloom_backend.exception.CustomException;
import lk.ijse.aurabloom_backend.repository.AdminDailyChallengeRepository;
import lk.ijse.aurabloom_backend.service.custom.AdminDailyChallengeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminDailyChallengeServiceImpl implements AdminDailyChallengeService {

    private final AdminDailyChallengeRepository repository;

    private AdminDailyChallengeDTO toDto(AdminDailyChallenge challenge) {
        return AdminDailyChallengeDTO.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .points(challenge.getPoints())
                .challengeType(challenge.getChallengeType().name())
                .challengeDate(challenge.getChallengeDate())
                .active(challenge.isActive())
                .build();
    }

    private ChallengeType parseChallengeType(String type) {
        try {
            return ChallengeType.valueOf(type.trim().toUpperCase());
        } catch (Exception e) {
            throw new CustomException("Invalid challenge type");
        }
    }

    private void validateDto(AdminDailyChallengeDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new CustomException("Title is required");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            throw new CustomException("Description is required");
        }
        if (dto.getChallengeType() == null || dto.getChallengeType().trim().isEmpty()) {
            throw new CustomException("Challenge type is required");
        }
        if (dto.getChallengeDate() == null) {
            throw new CustomException("Challenge date is required");
        }
        if (dto.getPoints() < 1 || dto.getPoints() > 1000) {
            throw new CustomException("Points must be between 1 and 1000");
        }
    }

    private AdminDailyChallenge toEntity(AdminDailyChallengeDTO dto) {
        return AdminDailyChallenge.builder()
                .title(dto.getTitle().trim())
                .description(dto.getDescription().trim())
                .points(dto.getPoints())
                .challengeType(parseChallengeType(dto.getChallengeType()))
                .challengeDate(dto.getChallengeDate())
                .active(dto.isActive())
                .build();
    }

    @Override
    public List<AdminDailyChallengeDTO> getAllChallenges() {
        return repository.findAllByOrderByChallengeDateDescIdDesc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public AdminDailyChallengeDTO getChallengeById(Long id) {
        AdminDailyChallenge challenge = repository.findById(id)
                .orElseThrow(() -> new CustomException("Challenge not found"));
        return toDto(challenge);
    }

    @Override
    public AdminDailyChallengeDTO createChallenge(AdminDailyChallengeDTO dto) {
        validateDto(dto);

        String title = dto.getTitle().trim();
        if (repository.existsByTitleIgnoreCaseAndChallengeDate(title, dto.getChallengeDate())) {
            throw new CustomException("A challenge with the same title already exists for this date");
        }

        AdminDailyChallenge saved = repository.save(toEntity(dto));
        return toDto(saved);
    }

    @Override
    public AdminDailyChallengeDTO updateChallenge(Long id, AdminDailyChallengeDTO dto) {
        validateDto(dto);

        AdminDailyChallenge challenge = repository.findById(id)
                .orElseThrow(() -> new CustomException("Challenge not found"));

        String title = dto.getTitle().trim();
        if (repository.existsByTitleIgnoreCaseAndChallengeDateAndIdNot(title, dto.getChallengeDate(), id)) {
            throw new CustomException("Another challenge already exists with the same title for this date");
        }

        challenge.setTitle(title);
        challenge.setDescription(dto.getDescription().trim());
        challenge.setPoints(dto.getPoints());
        challenge.setChallengeType(parseChallengeType(dto.getChallengeType()));
        challenge.setChallengeDate(dto.getChallengeDate());
        challenge.setActive(dto.isActive());

        return toDto(repository.save(challenge));
    }

    @Override
    public void deleteChallenge(Long id) {
        AdminDailyChallenge challenge = repository.findById(id)
                .orElseThrow(() -> new CustomException("Challenge not found"));
        repository.delete(challenge);
    }
}