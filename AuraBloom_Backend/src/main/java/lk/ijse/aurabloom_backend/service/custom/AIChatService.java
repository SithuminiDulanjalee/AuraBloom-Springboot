package lk.ijse.aurabloom_backend.service.custom;

import lk.ijse.aurabloom_backend.dto.AIChatRequest;
import lk.ijse.aurabloom_backend.dto.AIChatResponse;

public interface AIChatService {
    AIChatResponse chat(String email, AIChatRequest request);
}