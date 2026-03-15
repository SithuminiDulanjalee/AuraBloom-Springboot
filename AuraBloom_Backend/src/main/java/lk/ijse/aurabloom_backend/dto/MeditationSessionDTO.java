package lk.ijse.aurabloom_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeditationSessionDTO {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotNull(message = "Duration is required")
    private Integer duration;

    private LocalDate sessionDate;

    private String notes;
}