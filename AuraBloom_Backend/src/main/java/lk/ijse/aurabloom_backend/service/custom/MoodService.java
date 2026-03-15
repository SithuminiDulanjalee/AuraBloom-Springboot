package lk.ijse.aurabloom_backend.service.custom;


import lk.ijse.aurabloom_backend.dto.MoodEntryDTO;
import lk.ijse.aurabloom_backend.dto.MoodTrendDTO;

import java.util.List;

public interface MoodService {
    public MoodEntryDTO createMood(String email, MoodEntryDTO dto) ;

    public List<MoodEntryDTO> getAllMoods(String email);

    public MoodEntryDTO updateMood(String email, Long id, MoodEntryDTO dto);

    public void deleteMood(String email, Long id) ;

    public List<MoodTrendDTO> getWeeklyTrend(String email);

    public List<MoodTrendDTO> getMonthlyTrend(String email);

    public double getEmotionalRiskScore(String email);
}
