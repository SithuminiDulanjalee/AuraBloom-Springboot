package lk.ijse.aurabloom_backend.service.custom;

import lk.ijse.aurabloom_backend.dto.PasswordChangeDTO;
import lk.ijse.aurabloom_backend.dto.ProfileResponseDTO;
import lk.ijse.aurabloom_backend.dto.ProfileUpdateDTO;
import lk.ijse.aurabloom_backend.dto.RegisterRequestDTO;
import lk.ijse.aurabloom_backend.dto.UserResponseDTO;

public interface AdminService {
    UserResponseDTO getProfile(String email);
    ProfileResponseDTO updateProfile(String email, ProfileUpdateDTO dto);
    ProfileResponseDTO changePassword(String email, PasswordChangeDTO dto);
    UserResponseDTO createAdmin(RegisterRequestDTO dto);
}