package lk.ijse.aurabloom_backend.controller;

import jakarta.validation.Valid;
import lk.ijse.aurabloom_backend.dto.PasswordChangeDTO;
import lk.ijse.aurabloom_backend.dto.ProfileResponseDTO;
import lk.ijse.aurabloom_backend.dto.ProfileUpdateDTO;
import lk.ijse.aurabloom_backend.dto.UserResponseDTO;
import lk.ijse.aurabloom_backend.service.custom.UserService;
import lk.ijse.aurabloom_backend.util.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse<List<UserResponseDTO>>> getAllUsers() {
        return new ResponseEntity<>(
                new APIResponse<>(200, "Users fetched successfully", userService.getAllUsers()),
                HttpStatus.OK
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<APIResponse<UserResponseDTO>> getProfile(Principal principal) {
        return new ResponseEntity<>(
                new APIResponse<>(200, "Profile fetched successfully", userService.getProfile(principal.getName())),
                HttpStatus.OK
        );
    }

    @PutMapping("/profile")
    public ResponseEntity<APIResponse<ProfileResponseDTO>> updateProfile(
            Principal principal,
            @RequestBody ProfileUpdateDTO dto) {

        return new ResponseEntity<>(
                new APIResponse<>(200, "Profile updated successfully", userService.updateProfile(principal.getName(), dto)),
                HttpStatus.OK
        );
    }

    @PutMapping("/profile/password")
    public ResponseEntity<APIResponse<ProfileResponseDTO>> changePassword(
            Principal principal,
            @Valid @RequestBody PasswordChangeDTO dto) {

        return new ResponseEntity<>(
                new APIResponse<>(200, "Password updated successfully", userService.changePassword(principal.getName(), dto)),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/profile")
    public ResponseEntity<APIResponse<String>> deleteProfile(Principal principal) {
        userService.deleteProfile(principal.getName());
        return new ResponseEntity<>(
                new APIResponse<>(200, "Account deleted successfully", null),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse<String>> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>(new APIResponse<>(200, "User deleted", null), HttpStatus.OK);
    }

}