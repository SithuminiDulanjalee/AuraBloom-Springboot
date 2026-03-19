package lk.ijse.aurabloom_backend.service.custom;

import lk.ijse.aurabloom_backend.dto.AuthResponseDTO;
import lk.ijse.aurabloom_backend.dto.LoginRequestDTO;

public interface AuthService {
    AuthResponseDTO login(LoginRequestDTO request);
}
