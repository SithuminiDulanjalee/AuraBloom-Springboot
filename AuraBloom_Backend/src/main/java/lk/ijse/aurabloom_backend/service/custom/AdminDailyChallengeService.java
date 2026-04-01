package lk.ijse.aurabloom_backend.service.custom;

import lk.ijse.aurabloom_backend.dto.AdminDailyChallengeDTO;

import java.util.List;

public interface AdminDailyChallengeService {

    List<AdminDailyChallengeDTO> getAllChallenges();

    AdminDailyChallengeDTO getChallengeById(Long id);

    AdminDailyChallengeDTO createChallenge(AdminDailyChallengeDTO dto);

    AdminDailyChallengeDTO updateChallenge(Long id, AdminDailyChallengeDTO dto);

    void deleteChallenge(Long id);
}