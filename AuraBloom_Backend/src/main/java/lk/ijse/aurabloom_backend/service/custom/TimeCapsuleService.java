package lk.ijse.aurabloom_backend.service.custom;

import lk.ijse.aurabloom_backend.dto.TimeCapsuleDTO;

import java.util.List;

public interface TimeCapsuleService {

    TimeCapsuleDTO createCapsule(String email, TimeCapsuleDTO dto);

    List<TimeCapsuleDTO> getAllCapsules(String email);

    TimeCapsuleDTO updateCapsule(String email, Long id, TimeCapsuleDTO dto);

    void deleteCapsule(String email, Long id);
}