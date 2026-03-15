package lk.ijse.aurabloom_backend.service.custom;


import lk.ijse.aurabloom_backend.dto.MeditationSessionDTO;

import java.util.List;

public interface MeditationService {
    public MeditationSessionDTO createSession(String email, MeditationSessionDTO dto) ;

    public List<MeditationSessionDTO> getAllSessions(String email);

    public MeditationSessionDTO updateSession(String email, Long id, MeditationSessionDTO dto) ;

    public void deleteSession(String email, Long id) ;

    public int getWeeklyMeditationMinutes(String email) ;
}