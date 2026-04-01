package lk.ijse.aurabloom_backend.repository;

import lk.ijse.aurabloom_backend.entity.RiskReport;
import lk.ijse.aurabloom_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RiskReportRepository extends JpaRepository<RiskReport, Long> {
    List<RiskReport> findByUserOrderByGeneratedAtDesc(User user);
}