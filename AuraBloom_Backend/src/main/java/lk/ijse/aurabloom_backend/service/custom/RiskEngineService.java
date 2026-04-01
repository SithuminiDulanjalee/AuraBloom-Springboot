package lk.ijse.aurabloom_backend.service.custom;

import lk.ijse.aurabloom_backend.dto.RiskReportDTO;

public interface RiskEngineService {
    
    RiskReportDTO getReport(String email);

    
    void saveRiskAssessment(String email, RiskReportDTO riskReportDTO);
}