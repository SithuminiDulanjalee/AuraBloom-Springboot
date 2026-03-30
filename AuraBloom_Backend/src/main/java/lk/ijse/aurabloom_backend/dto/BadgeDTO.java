package lk.ijse.aurabloom_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadgeDTO {

    private Long id;
    private String badgeType;
    private String title;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate awardedDate;
}