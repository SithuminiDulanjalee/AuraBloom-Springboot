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

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/daily-challenges")
@CrossOrigin(origins = "*")
@Validated
@PreAuthorize("hasRole('USER')")
public class DailyChallengeController {

    private final DailyChallengeService dailyChallengeService;

    @GetMapping("/today")
    public ResponseEntity<APIResponse<List<DailyChallengeDTO>>> getTodayChallenges(Principal principal) {
        List<DailyChallengeDTO> challenges = dailyChallengeService.getTodayChallenges(principal.getName());
        return ResponseEntity.ok(new APIResponse<>(200, "Today's challenges fetched successfully", challenges));
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<DailyChallengeDTO>>> getAllChallenges(Principal principal) {
        List<DailyChallengeDTO> challenges = dailyChallengeService.getAllChallenges(principal.getName());
        return ResponseEntity.ok(new APIResponse<>(200, "All challenges fetched successfully", challenges));
    }

    @PostMapping
    public ResponseEntity<APIResponse<DailyChallengeDTO>> createChallenge(
            Principal principal,
            @RequestBody @Valid DailyChallengeDTO dto) {

        DailyChallengeDTO created = dailyChallengeService.createChallenge(principal.getName(), dto);
        return new ResponseEntity<>(new APIResponse<>(201, "Challenge created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<DailyChallengeDTO>> updateChallenge(
            Principal principal,
            @PathVariable Long id,
            @RequestBody @Valid DailyChallengeDTO dto) {

        DailyChallengeDTO updated = dailyChallengeService.updateChallenge(principal.getName(), id, dto);
        return ResponseEntity.ok(new APIResponse<>(200, "Challenge updated successfully", updated));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<APIResponse<DailyChallengeDTO>> completeChallenge(
            Principal principal,
            @PathVariable Long id) {

        DailyChallengeDTO completed = dailyChallengeService.completeChallenge(principal.getName(), id);
        return ResponseEntity.ok(new APIResponse<>(200, "Challenge completed successfully", completed));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteChallenge(
            Principal principal,
            @PathVariable Long id) {

        dailyChallengeService.deleteChallenge(principal.getName(), id);
        return ResponseEntity.ok(new APIResponse<>(200, "Challenge deleted successfully", null));
    }
}