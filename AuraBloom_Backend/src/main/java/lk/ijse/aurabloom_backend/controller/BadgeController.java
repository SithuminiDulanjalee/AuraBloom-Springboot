package lk.ijse.aurabloom_backend.controller;

import lombok.RequiredArgsConstructor;
import lk.ijse.aurabloom_backend.dto.BadgeDTO;
import lk.ijse.aurabloom_backend.service.custom.BadgeService;
import lk.ijse.aurabloom_backend.util.APIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/badges")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class BadgeController {

    private final BadgeService badgeService;

    @GetMapping
    public ResponseEntity<APIResponse<List<BadgeDTO>>> getAllBadges(Principal principal) {
        List<BadgeDTO> badges = badgeService.getAllBadges(principal.getName());
        return ResponseEntity.ok(new APIResponse<>(200, "Badges fetched successfully", badges));
    }
}