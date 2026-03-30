package lk.ijse.aurabloom_backend.repository;

import lk.ijse.aurabloom_backend.entity.Badge;
import lk.ijse.aurabloom_backend.entity.BadgeType;
import lk.ijse.aurabloom_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    List<Badge> findByUser(User user);

    boolean existsByUserAndBadgeType(User user, BadgeType badgeType);

    Optional<Badge> findByUserAndBadgeType(User user, BadgeType badgeType);
}