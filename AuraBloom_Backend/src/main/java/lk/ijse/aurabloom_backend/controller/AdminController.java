package lk.ijse.aurabloom_backend.controller;

import jakarta.validation.Valid;
import lk.ijse.aurabloom_backend.dto.PasswordChangeDTO;
import lk.ijse.aurabloom_backend.dto.ProfileResponseDTO;
import lk.ijse.aurabloom_backend.dto.ProfileUpdateDTO;
import lk.ijse.aurabloom_backend.dto.RegisterRequestDTO;
import lk.ijse.aurabloom_backend.dto.UserResponseDTO;
import lk.ijse.aurabloom_backend.service.custom.AdminService;
import lk.ijse.aurabloom_backend.util.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse<UserResponseDTO>> getProfile(Principal principal) {
        return new ResponseEntity<>(
                new APIResponse<>(200, "Profile fetched successfully", adminService.getProfile(principal.getName())),
                HttpStatus.OK
        );
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse<ProfileResponseDTO>> updateProfile(
            Principal principal,
            @RequestBody ProfileUpdateDTO dto
    ) {
        return new ResponseEntity<>(
                new APIResponse<>(200, "Profile updated successfully", adminService.updateProfile(principal.getName(), dto)),
                HttpStatus.OK
        );
    }

    @PutMapping("/profile/password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse<ProfileResponseDTO>> changePassword(
            Principal principal,
            @Valid @RequestBody PasswordChangeDTO dto
    ) {
        return new ResponseEntity<>(
                new APIResponse<>(200, "Password updated successfully", adminService.changePassword(principal.getName(), dto)),
                HttpStatus.OK
        );
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse<UserResponseDTO>> createAdmin(
            @Valid @RequestBody RegisterRequestDTO dto
    ) {
        return new ResponseEntity<>(
                new APIResponse<>(201, "Admin created successfully", adminService.createAdmin(dto)),
                HttpStatus.CREATED
        );
    }
}