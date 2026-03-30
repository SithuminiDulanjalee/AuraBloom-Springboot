package lk.ijse.aurabloom_backend.controller;

import jakarta.validation.Valid;
import lk.ijse.aurabloom_backend.dto.MeditationSessionDTO;
import lk.ijse.aurabloom_backend.service.custom.MeditationService;
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
@RequestMapping("/api/meditations")
@CrossOrigin(origins = "*")
@Validated
@PreAuthorize("hasRole('USER')")
public class MeditationController {

    private final MeditationService meditationService;

    @PostMapping
    public ResponseEntity<APIResponse<MeditationSessionDTO>> createSession(
            Principal principal,
            @RequestBody @Valid MeditationSessionDTO dto) {

        MeditationSessionDTO session =
                meditationService.createSession(principal.getName(), dto);

        return new ResponseEntity<>(
                new APIResponse<>(201,"Session created successfully",session),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<MeditationSessionDTO>>> getAllSessions(
            Principal principal) {

        List<MeditationSessionDTO> sessions =
                meditationService.getAllSessions(principal.getName());

        return new ResponseEntity<>(
                new APIResponse<>(200,"Sessions fetched successfully",sessions),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<MeditationSessionDTO>> updateSession(
            Principal principal,
            @PathVariable Long id,
            @RequestBody @Valid MeditationSessionDTO dto) {

        MeditationSessionDTO session =
                meditationService.updateSession(principal.getName(), id, dto);

        return new ResponseEntity<>(
                new APIResponse<>(200,"Session updated successfully",session),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteSession(
            Principal principal,
            @PathVariable Long id) {

        meditationService.deleteSession(principal.getName(), id);

        return new ResponseEntity<>(
                new APIResponse<>(200,"Session deleted successfully",null),
                HttpStatus.OK
        );
    }

    @GetMapping("/weekly-total")
    public ResponseEntity<APIResponse<Integer>> getWeeklyMeditationTime(
            Principal principal) {

        Integer total =
                meditationService.getWeeklyMeditationMinutes(principal.getName());

        return new ResponseEntity<>(
                new APIResponse<>(200,"Weekly meditation minutes",total),
                HttpStatus.OK
        );
    }
}