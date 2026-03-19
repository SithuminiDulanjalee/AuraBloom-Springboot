package lk.ijse.aurabloom_backend.service.custom;


import lk.ijse.aurabloom_backend.dto.ProfileUpdateDTO;
import lk.ijse.aurabloom_backend.dto.RegisterRequestDTO;
import lk.ijse.aurabloom_backend.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    UserResponseDTO register(RegisterRequestDTO dto);
    UserResponseDTO getProfile(String email);
    UserResponseDTO updateProfile(String email, ProfileUpdateDTO dto);
    void deleteProfile(String email);
    List<UserResponseDTO> getAllUsers();
}
