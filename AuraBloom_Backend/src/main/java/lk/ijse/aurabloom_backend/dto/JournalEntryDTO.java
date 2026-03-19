package lk.ijse.aurabloom_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JournalEntryDTO {

    private Long id;
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @NotBlank(message = "Mood is required")
    private String mood;

    private List<String> tags;

    private LocalDate createdDate;
}