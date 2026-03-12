package lk.ijse.aurabloom_backend.service.custom;


import lk.ijse.aurabloom_backend.dto.UserDTO;

import java.util.List;

public interface UserService {
    public UserDTO register(UserDTO dto) ;

    public UserDTO getProfile(String email);

    public UserDTO updateProfile(String email, UserDTO dto) ;

    public void deleteProfile(String email);

    public List<UserDTO> getAllUsers() ;
}
