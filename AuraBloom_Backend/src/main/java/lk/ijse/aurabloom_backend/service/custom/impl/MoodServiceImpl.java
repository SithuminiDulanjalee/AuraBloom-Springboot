package lk.ijse.aurabloom_backend.service.custom.impl;

import lk.ijse.aurabloom_backend.dto.MoodEntryDTO;
import lk.ijse.aurabloom_backend.dto.MoodTrendDTO;
import lk.ijse.aurabloom_backend.entity.MoodEntry;
import lk.ijse.aurabloom_backend.entity.MoodType;
import lk.ijse.aurabloom_backend.entity.User;
import lk.ijse.aurabloom_backend.exception.CustomException;
import lk.ijse.aurabloom_backend.repository.MoodEntryRepository;
import lk.ijse.aurabloom_backend.repository.UserRepository;
import lk.ijse.aurabloom_backend.service.custom.MoodService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MoodServiceImpl implements MoodService {

    private final MoodEntryRepository moodEntryRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private MoodEntryDTO toDTO(MoodEntry entry) {
        MoodEntryDTO dto = modelMapper.map(entry, MoodEntryDTO.class);
        dto.setMoodType(entry.getMoodType().name());
        return dto;
    }

    private MoodType parseMoodType(String moodType) {
        try {
            return MoodType.valueOf(moodType.toUpperCase());
        } catch (Exception e) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Invalid mood type");
        }
    }

    private Map<String, Long> initMoodCounts() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (MoodType type : MoodType.values()) {
            counts.put(type.name(), 0L);
        }
        return counts;
    }

    @Override
    public MoodEntryDTO createMood(String email, MoodEntryDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

        MoodEntry entry = new MoodEntry();
        entry.setMoodType(parseMoodType(dto.getMoodType()));
        entry.setNote(dto.getNote());
        entry.setCreatedDate(dto.getCreatedDate() != null ? dto.getCreatedDate() : LocalDate.now());
        entry.setUser(user);

        MoodEntry saved = moodEntryRepository.save(entry);
        return toDTO(saved);
    }

    @Override
    public List<MoodEntryDTO> getAllMoods(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

        return moodEntryRepository.findByUserOrderByCreatedDateDesc(user)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public MoodEntryDTO updateMood(String email, Long id, MoodEntryDTO dto) {
        MoodEntry entry = moodEntryRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Mood entry not found"));

        if (!entry.getUser().getEmail().equals(email)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "Unauthorized");
        }

        entry.setMoodType(parseMoodType(dto.getMoodType()));
        entry.setNote(dto.getNote());
        if (dto.getCreatedDate() != null) {
            entry.setCreatedDate(dto.getCreatedDate());
        }

        MoodEntry updated = moodEntryRepository.save(entry);
        return toDTO(updated);
    }

    @Override
    public void deleteMood(String email, Long id) {
        MoodEntry entry = moodEntryRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Mood entry not found"));

        if (!entry.getUser().getEmail().equals(email)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "Unauthorized");
        }

        moodEntryRepository.delete(entry);
    }

    @Override
    public MoodTrendDTO getWeeklyTrend(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);

        List<MoodEntry> moods = moodEntryRepository.findByUserAndCreatedDateBetween(user, weekStart, today);

        Map<String, Long> counts = initMoodCounts();
        for (MoodEntry mood : moods) {
            String moodName = mood.getMoodType().name();
            counts.put(moodName, counts.get(moodName) + 1);
        }

        return new MoodTrendDTO(weekStart, today, counts);
    }

    @Override
    public MoodTrendDTO getMonthlyTrend(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        List<MoodEntry> moods = moodEntryRepository.findByUserAndCreatedDateBetween(user, monthStart, today);

        Map<String, Long> counts = initMoodCounts();
        for (MoodEntry mood : moods) {
            String moodName = mood.getMoodType().name();
            counts.put(moodName, counts.get(moodName) + 1);
        }

        return new MoodTrendDTO(monthStart, today, counts);
    }

    @Override
    public double getEmotionalRiskScore(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

        LocalDate last30Days = LocalDate.now().minusDays(30);
        List<MoodEntry> moods = moodEntryRepository.findByUserAndCreatedDateAfter(user, last30Days);

        if (moods.isEmpty()) {
            return 0.0;
        }

        long negativeCount = moods.stream()
                .filter(m -> m.getMoodType() == MoodType.SAD || m.getMoodType() == MoodType.STRESSED)
                .count();

        return (negativeCount * 100.0) / moods.size();
    }
}