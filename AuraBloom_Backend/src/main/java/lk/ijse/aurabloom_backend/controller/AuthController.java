package lk.ijse.aurabloom_backend.controller;

import jakarta.validation.Valid;
import lk.ijse.aurabloom_backend.dto.AuthRequest;
import lk.ijse.aurabloom_backend.dto.AuthResponse;
import lk.ijse.aurabloom_backend.dto.UserDTO;
import lk.ijse.aurabloom_backend.service.custom.impl.AuthServiceImpl;
import lk.ijse.aurabloom_backend.service.custom.impl.UserServiceImpl;
import lk.ijse.aurabloom_backend.util.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserServiceImpl userService;
    private final AuthServiceImpl authService;

    @PostMapping("/register")
    public ResponseEntity<APIResponse<UserDTO>> register(@Valid @RequestBody UserDTO dto) {

        UserDTO savedUser = userService.register(dto);

        return new ResponseEntity<>(
                new APIResponse<>(
                        201,
                        "User registered successfully",
                        savedUser
                ),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {

        String token = authService.login(request);

        AuthResponse response = new AuthResponse(token);

        return new ResponseEntity<>(
                new APIResponse<>(
                        200,
                        "Login successful",
                        response
                ),
                HttpStatus.OK
        );
    }
}