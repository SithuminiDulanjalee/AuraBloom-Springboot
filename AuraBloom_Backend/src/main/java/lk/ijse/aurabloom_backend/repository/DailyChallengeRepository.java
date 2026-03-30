package lk.ijse.aurabloom_backend.repository;

import lk.ijse.aurabloom_backend.entity.DailyChallenge;
import lk.ijse.aurabloom_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyChallengeRepository extends JpaRepository<DailyChallenge, Long> {

    List<DailyChallenge> findByUser(User user);

    List<DailyChallenge> findByUserAndChallengeDate(User user, LocalDate challengeDate);

    List<DailyChallenge> findByUserAndChallengeDateBetween(User user, LocalDate start, LocalDate end);

    Optional<DailyChallenge> findByIdAndUser(Long id, User user);

    boolean existsByUserAndChallengeDateAndTitleIgnoreCase(User user, LocalDate challengeDate, String title);
}