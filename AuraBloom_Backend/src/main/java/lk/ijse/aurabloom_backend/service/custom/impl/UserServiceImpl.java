package lk.ijse.aurabloom_backend.service.custom.impl;

import lk.ijse.aurabloom_backend.dto.PasswordChangeDTO;
import lk.ijse.aurabloom_backend.dto.ProfileResponseDTO;
import lk.ijse.aurabloom_backend.dto.ProfileUpdateDTO;
import lk.ijse.aurabloom_backend.dto.RegisterRequestDTO;
import lk.ijse.aurabloom_backend.dto.UserResponseDTO;
import lk.ijse.aurabloom_backend.entity.Role;
import lk.ijse.aurabloom_backend.entity.User;
import lk.ijse.aurabloom_backend.exception.CustomException;
import lk.ijse.aurabloom_backend.repository.UserRepository;
import lk.ijse.aurabloom_backend.service.custom.UserService;
import lk.ijse.aurabloom_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private UserResponseDTO toResponse(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole() != null ? user.getRole().name() : null
        );
    }

    @Override
    public UserResponseDTO register(RegisterRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new CustomException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = User.builder()
                .email(dto.getEmail())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.USER)
                .build();

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Override
    public UserResponseDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

        return toResponse(user);
    }

    @Override
    public ProfileResponseDTO updateProfile(String email, ProfileUpdateDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (!dto.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
                throw new CustomException(HttpStatus.CONFLICT, "Email already exists");
            }
            user.setEmail(dto.getEmail().trim());
        }

        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            user.setUsername(dto.getUsername().trim());
        }

        User saved = userRepository.save(user);
        String newToken = jwtUtil.generateToken(saved);

        return new ProfileResponseDTO(toResponse(saved), newToken);
    }

    @Override
    public ProfileResponseDTO changePassword(String email, PasswordChangeDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        User saved = userRepository.save(user);
        String newToken = jwtUtil.generateToken(saved);

        return new ProfileResponseDTO(toResponse(saved), newToken);
    }

    @Override
    public void deleteProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));
        userRepository.delete(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}