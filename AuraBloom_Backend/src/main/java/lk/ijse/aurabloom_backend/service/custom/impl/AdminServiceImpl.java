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
import lk.ijse.aurabloom_backend.service.custom.AdminService;
import lk.ijse.aurabloom_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private UserResponseDTO toResponse(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole() != null ? user.getRole().name() : null
        );
    }

    @Override
    public UserResponseDTO getProfile(String email) {
        User user = getUserByEmail(email);

        if (user.getRole() != Role.ADMIN) {
            throw new CustomException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return toResponse(user);
    }

    @Override
    public ProfileResponseDTO updateProfile(String email, ProfileUpdateDTO dto) {
        User user = getUserByEmail(email);

        if (user.getRole() != Role.ADMIN) {
            throw new CustomException(HttpStatus.FORBIDDEN, "Access denied");
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            String newEmail = dto.getEmail().trim();

            if (!newEmail.equalsIgnoreCase(user.getEmail())
                    && userRepository.existsByEmailAndIdNot(newEmail, user.getId())) {
                throw new CustomException(HttpStatus.CONFLICT, "Email already exists");
            }

            user.setEmail(newEmail);
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
        User user = getUserByEmail(email);

        if (user.getRole() != Role.ADMIN) {
            throw new CustomException(HttpStatus.FORBIDDEN, "Access denied");
        }

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        User saved = userRepository.save(user);
        String newToken = jwtUtil.generateToken(saved);

        return new ProfileResponseDTO(toResponse(saved), newToken);
    }

    @Override
    public UserResponseDTO createAdmin(RegisterRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new CustomException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = User.builder()
                .email(dto.getEmail().trim())
                .username(dto.getUsername().trim())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.ADMIN)
                .build();

        return toResponse(userRepository.save(user));
    }
}