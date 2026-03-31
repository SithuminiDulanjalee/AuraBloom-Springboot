package lk.ijse.aurabloom_backend.controller;

import jakarta.validation.Valid;
import lk.ijse.aurabloom_backend.dto.TimeCapsuleDTO;
import lk.ijse.aurabloom_backend.service.custom.TimeCapsuleService;
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
@RequestMapping("/api/timecapsules")
@CrossOrigin(origins = "*")
@Validated
@PreAuthorize("hasRole('USER')")
public class TimeCapsuleController {

    private final TimeCapsuleService timeCapsuleService;

    @PostMapping
    public ResponseEntity<APIResponse<TimeCapsuleDTO>> createCapsule(
            Principal principal,
            @RequestBody @Valid TimeCapsuleDTO dto) {

        TimeCapsuleDTO capsule =
                timeCapsuleService.createCapsule(principal.getName(), dto);

        return new ResponseEntity<>(
                new APIResponse<>(201, "Capsule created successfully", capsule),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<TimeCapsuleDTO>>> getAllCapsules(
            Principal principal) {

        List<TimeCapsuleDTO> capsules =
                timeCapsuleService.getAllCapsules(principal.getName());

        return new ResponseEntity<>(
                new APIResponse<>(200, "Capsules fetched successfully", capsules),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<TimeCapsuleDTO>> updateCapsule(
            Principal principal,
            @PathVariable Long id,
            @RequestBody @Valid TimeCapsuleDTO dto) {

        TimeCapsuleDTO capsule =
                timeCapsuleService.updateCapsule(principal.getName(), id, dto);

        return new ResponseEntity<>(
                new APIResponse<>(200, "Capsule updated successfully", capsule),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteCapsule(
            Principal principal,
            @PathVariable Long id) {

        timeCapsuleService.deleteCapsule(principal.getName(), id);

        return new ResponseEntity<>(
                new APIResponse<>(200, "Capsule deleted successfully", null),
                HttpStatus.OK
        );
    }
}