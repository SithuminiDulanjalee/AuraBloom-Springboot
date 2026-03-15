package lk.ijse.aurabloom_backend.controller;

import jakarta.validation.Valid;
import lk.ijse.aurabloom_backend.dto.MoodEntryDTO;
import lk.ijse.aurabloom_backend.dto.MoodTrendDTO;
import lk.ijse.aurabloom_backend.service.custom.impl.MoodServiceImpl;
import lk.ijse.aurabloom_backend.util.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/moods")
@CrossOrigin(origins = "*")
@Validated
@PreAuthorize("hasRole('USER')")
public class MoodController {

    private final MoodServiceImpl moodService;

    @PostMapping
    public ResponseEntity<APIResponse<MoodEntryDTO>> createMood(
            Principal principal,
            @RequestBody @Valid MoodEntryDTO dto) {

        MoodEntryDTO mood = moodService.createMood(principal.getName(), dto);

        return new ResponseEntity<>(
                new APIResponse<>(
                        201,
                        "Mood created successfully",
                        mood
                ),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<MoodEntryDTO>>> getAllMoods(Principal principal) {

        List<MoodEntryDTO> moods = moodService.getAllMoods(principal.getName());

        return new ResponseEntity<>(
                new APIResponse<>(
                        200,
                        "Moods fetched successfully",
                        moods
                ),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<MoodEntryDTO>> updateMood(
            Principal principal,
            @PathVariable Long id,
            @RequestBody @Valid MoodEntryDTO dto) {

        MoodEntryDTO mood = moodService.updateMood(principal.getName(), id, dto);

        return new ResponseEntity<>(
                new APIResponse<>(
                        200,
                        "Mood updated successfully",
                        mood
                ),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteMood(
            Principal principal,
            @PathVariable Long id) {

        moodService.deleteMood(principal.getName(), id);

        return new ResponseEntity<>(
                new APIResponse<>(
                        200,
                        "Mood deleted successfully",
                        null
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/trend/weekly")
    public ResponseEntity<APIResponse<List<MoodTrendDTO>>> weeklyTrend(Principal principal) {

        List<MoodTrendDTO> trend = moodService.getWeeklyTrend(principal.getName());

        return new ResponseEntity<>(
                new APIResponse<>(
                        200,
                        "Weekly trend fetched successfully",
                        trend
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/trend/monthly")
    public ResponseEntity<APIResponse<List<MoodTrendDTO>>> monthlyTrend(Principal principal) {

        List<MoodTrendDTO> trend = moodService.getMonthlyTrend(principal.getName());

        return new ResponseEntity<>(
                new APIResponse<>(
                        200,
                        "Monthly trend fetched successfully",
                        trend
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/risk")
    public ResponseEntity<APIResponse<Double>> riskScore(Principal principal) {

        Double score = moodService.getEmotionalRiskScore(principal.getName());

        return new ResponseEntity<>(
                new APIResponse<>(
                        200,
                        "Emotional risk score fetched successfully",
                        score
                ),
                HttpStatus.OK
        );
    }
}