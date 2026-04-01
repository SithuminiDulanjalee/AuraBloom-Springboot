package lk.ijse.aurabloom_backend.repository;

import lk.ijse.aurabloom_backend.entity.AdminDailyChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AdminDailyChallengeRepository extends JpaRepository<AdminDailyChallenge, Long> {

    List<AdminDailyChallenge> findAllByOrderByChallengeDateDescIdDesc();

    boolean existsByTitleIgnoreCaseAndChallengeDate(String title, LocalDate challengeDate);

    boolean existsByTitleIgnoreCaseAndChallengeDateAndIdNot(String title, LocalDate challengeDate, Long id);
}