package lk.ijse.aurabloom_backend.service.custom;

import lk.ijse.aurabloom_backend.dto.PasswordChangeDTO;
import lk.ijse.aurabloom_backend.dto.ProfileResponseDTO;
import lk.ijse.aurabloom_backend.dto.ProfileUpdateDTO;
import lk.ijse.aurabloom_backend.dto.RegisterRequestDTO;
import lk.ijse.aurabloom_backend.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    UserResponseDTO register(RegisterRequestDTO dto);
    UserResponseDTO getProfile(String email);
    ProfileResponseDTO updateProfile(String email, ProfileUpdateDTO dto);
    ProfileResponseDTO changePassword(String email, PasswordChangeDTO dto);
    void deleteProfile(String email);
    List<UserResponseDTO> getAllUsers();
}