package lk.ijse.aurabloom_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoodTrendDTO {

    private LocalDate startDate;
    private LocalDate endDate;
    private Map<String, Long> moodCounts;

}