package lk.ijse.aurabloom_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lk.ijse.aurabloom_backend.dto.DailyChallengeDTO;
import lk.ijse.aurabloom_backend.service.custom.DailyChallengeService;
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

    private final DailyChallengeService dailyChallengeService;

    @GetMapping("/{email}")
    public ResponseEntity<APIResponse<List<DailyChallengeDTO>>> getAllForUser(@PathVariable String email) {
        List<DailyChallengeDTO> challenges = dailyChallengeService.getAllChallenges(email);
        return ResponseEntity.ok(new APIResponse<>(200, "User challenges fetched successfully", challenges));
    }

    @PostMapping("/{email}")
    public ResponseEntity<APIResponse<DailyChallengeDTO>> createForUser(
            @PathVariable String email,
            @RequestBody @Valid DailyChallengeDTO dto) {

        DailyChallengeDTO created = dailyChallengeService.createChallenge(email, dto);
        return new ResponseEntity<>(new APIResponse<>(201, "Challenge created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{email}/{id}")
    public ResponseEntity<APIResponse<DailyChallengeDTO>> updateForUser(
            @PathVariable String email,
            @PathVariable Long id,
            @RequestBody @Valid DailyChallengeDTO dto) {

        DailyChallengeDTO updated = dailyChallengeService.updateChallenge(email, id, dto);
        return ResponseEntity.ok(new APIResponse<>(200, "Challenge updated successfully", updated));
    }

    @DeleteMapping("/{email}/{id}")
    public ResponseEntity<APIResponse<String>> deleteForUser(
            @PathVariable String email,
            @PathVariable Long id) {

        dailyChallengeService.deleteChallenge(email, id);
        return ResponseEntity.ok(new APIResponse<>(200, "Challenge deleted successfully", null));
    }
}