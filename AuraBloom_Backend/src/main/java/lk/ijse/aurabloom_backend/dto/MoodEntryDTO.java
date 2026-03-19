package lk.ijse.aurabloom_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MoodEntryDTO {

    private Long id;

    @NotBlank(message = "Mood type is required")
    private String moodType;

    private String note;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;
}