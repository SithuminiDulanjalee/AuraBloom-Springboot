package lk.ijse.aurabloom_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    private Long id;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotNull(message = "Duration is required")
    private Integer duration;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate sessionDate;

    private String notes;
}