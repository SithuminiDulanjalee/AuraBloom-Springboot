package lk.ijse.aurabloom_backend.controller;

import lombok.RequiredArgsConstructor;
import lk.ijse.aurabloom_backend.dto.RiskReportDTO;
import lk.ijse.aurabloom_backend.service.custom.RiskEngineService;
import lk.ijse.aurabloom_backend.util.APIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/risk")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class RiskEngineController {

    private final RiskEngineService riskEngineService;

    @GetMapping("/report")
    public ResponseEntity<APIResponse<RiskReportDTO>> getReport(Principal principal) {
        RiskReportDTO report = riskEngineService.getReport(principal.getName());
        return ResponseEntity.ok(new APIResponse<>(200, "Risk report fetched", report));
    }
}