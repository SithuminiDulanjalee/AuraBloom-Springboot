package lk.ijse.aurabloom_backend.repository;

import lk.ijse.aurabloom_backend.entity.JournalEntry;
import lk.ijse.aurabloom_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    List<JournalEntry> findByUser(User user);

    List<JournalEntry> findByUserAndCreatedDateAfter(User user, LocalDate afterDate);
}