package lk.ijse.aurabloom_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MoodEntryDTO {

    @NotNull(message = "Mood type is required")
    private String moodType;

    private String note;
}