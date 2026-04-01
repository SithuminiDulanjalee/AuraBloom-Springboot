package lk.ijse.aurabloom_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lk.ijse.aurabloom_backend.dto.AdminDailyChallengeDTO;
import lk.ijse.aurabloom_backend.service.custom.AdminDailyChallengeService;
import lk.ijse.aurabloom_backend.util.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/daily-challenges")
@CrossOrigin(origins = "*")
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class AdminDailyChallengeController {

    private final AdminDailyChallengeService service;

    @GetMapping
    public ResponseEntity<APIResponse<List<AdminDailyChallengeDTO>>> getAllChallenges() {
        return ResponseEntity.ok(
                new APIResponse<>(200, "All admin challenges fetched successfully", service.getAllChallenges())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<AdminDailyChallengeDTO>> getChallengeById(@PathVariable Long id) {
        return ResponseEntity.ok(
                new APIResponse<>(200, "Challenge fetched successfully", service.getChallengeById(id))
        );
    }

    @PostMapping
    public ResponseEntity<APIResponse<AdminDailyChallengeDTO>> createChallenge(
            @RequestBody @Valid AdminDailyChallengeDTO dto) {

        return new ResponseEntity<>(
                new APIResponse<>(201, "Challenge created successfully", service.createChallenge(dto)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<AdminDailyChallengeDTO>> updateChallenge(
            @PathVariable Long id,
            @RequestBody @Valid AdminDailyChallengeDTO dto) {

        return ResponseEntity.ok(
                new APIResponse<>(200, "Challenge updated successfully", service.updateChallenge(id, dto))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteChallenge(@PathVariable Long id) {
        service.deleteChallenge(id);
        return ResponseEntity.ok(new APIResponse<>(200, "Challenge deleted successfully", null));
    }
}