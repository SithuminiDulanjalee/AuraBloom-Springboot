package lk.ijse.aurabloom_backend.service.custom;

import lk.ijse.aurabloom_backend.dto.DailyChallengeDTO;

import java.util.List;

public interface DailyChallengeService {

    List<DailyChallengeDTO> getTodayChallenges(String email);

    List<DailyChallengeDTO> getAllChallenges(String email);

    DailyChallengeDTO createChallenge(String email, DailyChallengeDTO dto);

    DailyChallengeDTO updateChallenge(String email, Long id, DailyChallengeDTO dto);

    DailyChallengeDTO completeChallenge(String email, Long id);

    void deleteChallenge(String email, Long id);
}