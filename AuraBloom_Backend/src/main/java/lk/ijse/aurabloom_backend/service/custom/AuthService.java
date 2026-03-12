package lk.ijse.aurabloom_backend.service.custom;

import lk.ijse.aurabloom_backend.dto.AuthRequest;
import lk.ijse.aurabloom_backend.entity.User;

public interface AuthService {
    public String login(AuthRequest request);
}
