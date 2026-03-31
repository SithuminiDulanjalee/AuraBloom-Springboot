package lk.ijse.aurabloom_backend.repository;

import lk.ijse.aurabloom_backend.entity.TimeCapsule;
import lk.ijse.aurabloom_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeCapsuleRepository extends JpaRepository<TimeCapsule, Long> {

    List<TimeCapsule> findByUser(User user);

    long countByUser(User user);
}