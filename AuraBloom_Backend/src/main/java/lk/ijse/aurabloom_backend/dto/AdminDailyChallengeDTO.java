package lk.ijse.aurabloom_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDailyChallengeDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must not exceed 150 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Min(value = 1, message = "Points must be at least 1")
    @Max(value = 1000, message = "Points must not exceed 1000")
    private int points;

    @NotBlank(message = "Challenge type is required")
    private String challengeType;

    @NotNull(message = "Challenge date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate challengeDate;

    private boolean active;
}