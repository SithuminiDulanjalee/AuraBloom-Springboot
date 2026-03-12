package lk.ijse.aurabloom_backend.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class APIResponse<T> {
    private int code;
    private String message;
    private T data;
}