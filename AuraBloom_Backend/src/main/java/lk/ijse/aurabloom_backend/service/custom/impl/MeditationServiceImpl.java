package lk.ijse.aurabloom_backend.service.custom.impl;

import lk.ijse.aurabloom_backend.dto.MeditationSessionDTO;
import lk.ijse.aurabloom_backend.entity.MeditationSession;
import lk.ijse.aurabloom_backend.entity.User;
import lk.ijse.aurabloom_backend.repository.MeditationSessionRepository;
import lk.ijse.aurabloom_backend.repository.UserRepository;
import lk.ijse.aurabloom_backend.service.custom.MeditationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeditationServiceImpl implements MeditationService {

    private final MeditationSessionRepository meditationRepo;
    private final UserRepository userRepository;
    @Autowired
    private final ModelMapper modelMapper;

    @Override
    public MeditationSessionDTO createSession(String email, MeditationSessionDTO dto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MeditationSession session = modelMapper.map(dto, MeditationSession.class);

        session.setSessionDate(
                dto.getSessionDate() != null ? dto.getSessionDate() : LocalDate.now()
        );

        session.setUser(user);

        MeditationSession saved = meditationRepo.save(session);

        return modelMapper.map(saved, MeditationSessionDTO.class);
    }

    @Override
    public List<MeditationSessionDTO> getAllSessions(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<MeditationSession> sessions = meditationRepo.findByUser(user);

        List<MeditationSessionDTO> result = new ArrayList<>();

        for (MeditationSession session : sessions) {
            result.add(modelMapper.map(session, MeditationSessionDTO.class));
        }

        return result;
    }

    @Override
    public MeditationSessionDTO updateSession(String email, Long id, MeditationSessionDTO dto) {

        MeditationSession session = meditationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        session.setTitle(dto.getTitle());
        session.setDuration(dto.getDuration());

        if (dto.getSessionDate() != null) {
            session.setSessionDate(dto.getSessionDate());
        }

        session.setNotes(dto.getNotes());

        MeditationSession updated = meditationRepo.save(session);

        return modelMapper.map(updated, MeditationSessionDTO.class);
    }

    @Override
    public void deleteSession(String email, Long id) {

        MeditationSession session = meditationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        meditationRepo.delete(session);
    }

    @Override
    public int getWeeklyMeditationMinutes(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate weekStart = LocalDate.now().minusDays(6);

        List<MeditationSession> sessions =
                meditationRepo.findByUserAndSessionDateBetween(user, weekStart, LocalDate.now());

        int total = 0;

        for (MeditationSession s : sessions) {
            total += s.getDuration();
        }

        return total;
    }
}