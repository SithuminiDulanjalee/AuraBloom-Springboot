package lk.ijse.aurabloom_backend.repository;

import lk.ijse.aurabloom_backend.entity.MoodEntry;
import lk.ijse.aurabloom_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {

    List<MoodEntry> findByUserOrderByCreatedDateDesc(User user);

    List<MoodEntry> findByUserAndCreatedDateBetween(User user, LocalDate start, LocalDate end);

    List<MoodEntry> findByUserAndCreatedDateAfter(User user, LocalDate after);

    long countByUser(User user);
}