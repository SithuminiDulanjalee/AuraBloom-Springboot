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
public class TimeCapsuleDTO {

    private Long id;

    @NotBlank(message = "Message cannot be blank")
    private String message;

    @NotNull(message = "Unlock date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate unlockDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;
}