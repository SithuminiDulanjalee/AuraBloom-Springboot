
package lk.ijse.aurabloom_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDTO {
    private UserResponseDTO user;
    private String token;
}