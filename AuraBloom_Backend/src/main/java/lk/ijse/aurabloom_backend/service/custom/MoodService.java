package lk.ijse.aurabloom_backend.service.custom;


import lk.ijse.aurabloom_backend.dto.MoodEntryDTO;
import lk.ijse.aurabloom_backend.dto.MoodTrendDTO;

import java.util.List;

public interface MoodService {
    MoodEntryDTO createMood(String email, MoodEntryDTO dto);
    List<MoodEntryDTO> getAllMoods(String email);
    MoodEntryDTO updateMood(String email, Long id, MoodEntryDTO dto);
    void deleteMood(String email, Long id);
    MoodTrendDTO getWeeklyTrend(String email);
    MoodTrendDTO getMonthlyTrend(String email);
    double getEmotionalRiskScore(String email);
}
