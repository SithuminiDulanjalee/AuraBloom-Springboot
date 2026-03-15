package lk.ijse.aurabloom_backend.repository;

import lk.ijse.aurabloom_backend.entity.MeditationSession;
import lk.ijse.aurabloom_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MeditationSessionRepository extends JpaRepository<MeditationSession, Long> {

    List<MeditationSession> findByUser(User user);

    List<MeditationSession> findByUserAndSessionDateBetween(
            User user,
            LocalDate start,
            LocalDate end
    );
}