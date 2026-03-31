package lk.ijse.aurabloom_backend.service.custom.impl;

import lk.ijse.aurabloom_backend.dto.TimeCapsuleDTO;
import lk.ijse.aurabloom_backend.entity.TimeCapsule;
import lk.ijse.aurabloom_backend.entity.User;
import lk.ijse.aurabloom_backend.repository.TimeCapsuleRepository;
import lk.ijse.aurabloom_backend.repository.UserRepository;
import lk.ijse.aurabloom_backend.service.custom.TimeCapsuleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeCapsuleServiceImpl implements TimeCapsuleService {

    private final TimeCapsuleRepository timeCapsuleRepository;
    private final UserRepository userRepository;
    @Autowired
    private final ModelMapper modelMapper;

    @Override
    public TimeCapsuleDTO createCapsule(String email, TimeCapsuleDTO dto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TimeCapsule capsule = modelMapper.map(dto, TimeCapsule.class);
        capsule.setCreatedDate(LocalDate.now());
        capsule.setUser(user);

        TimeCapsule saved = timeCapsuleRepository.save(capsule);

        return modelMapper.map(saved, TimeCapsuleDTO.class);
    }

    @Override
    public List<TimeCapsuleDTO> getAllCapsules(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<TimeCapsule> capsules = timeCapsuleRepository.findByUser(user);

        List<TimeCapsuleDTO> result = new ArrayList<>();

        for (TimeCapsule capsule : capsules) {
            result.add(modelMapper.map(capsule, TimeCapsuleDTO.class));
        }

        return result;
    }

    @Override
    public TimeCapsuleDTO updateCapsule(String email, Long id, TimeCapsuleDTO dto) {

        TimeCapsule capsule = timeCapsuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Capsule not found"));

        if (!capsule.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        capsule.setMessage(dto.getMessage());
        capsule.setUnlockDate(dto.getUnlockDate());

        TimeCapsule updated = timeCapsuleRepository.save(capsule);

        return modelMapper.map(updated, TimeCapsuleDTO.class);
    }

    @Override
    public void deleteCapsule(String email, Long id) {

        TimeCapsule capsule = timeCapsuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Capsule not found"));

        if (!capsule.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        timeCapsuleRepository.delete(capsule);
    }
}