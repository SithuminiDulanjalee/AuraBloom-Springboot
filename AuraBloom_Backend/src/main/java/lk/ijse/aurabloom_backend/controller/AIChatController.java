package lk.ijse.aurabloom_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lk.ijse.aurabloom_backend.dto.AIChatRequest;
import lk.ijse.aurabloom_backend.dto.AIChatResponse;
import lk.ijse.aurabloom_backend.service.custom.AIChatService;
import lk.ijse.aurabloom_backend.util.APIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class AIChatController {

    private final AIChatService aiChatService;

    @PostMapping("/chat")
    public ResponseEntity<APIResponse<AIChatResponse>> chat(
            Principal principal,
            @RequestBody @Valid AIChatRequest request) {

        AIChatResponse response = aiChatService.chat(principal.getName(), request);
        return ResponseEntity.ok(new APIResponse<>(200, "AI response", response));
    }
}