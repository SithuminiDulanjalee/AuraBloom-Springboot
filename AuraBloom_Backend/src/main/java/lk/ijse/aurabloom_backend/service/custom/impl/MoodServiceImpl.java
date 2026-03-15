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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MoodServiceImpl implements MoodService {

    private final MoodEntryRepository moodEntryRepository;
    private final UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public MoodEntryDTO createMood(String email, MoodEntryDTO dto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        MoodEntry entry = modelMapper.map(dto, MoodEntry.class);

        entry.setMoodType(MoodType.valueOf(dto.getMoodType().toUpperCase()));
        entry.setCreatedDate(LocalDate.now());
        entry.setUser(user);

        MoodEntry saved = moodEntryRepository.save(entry);

        return modelMapper.map(saved, MoodEntryDTO.class);
    }

    @Override
    public List<MoodEntryDTO> getAllMoods(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        List<MoodEntry> moods = moodEntryRepository.findByUser(user);

        List<MoodEntryDTO> result = new ArrayList<>();

        for (MoodEntry mood : moods) {
            result.add(modelMapper.map(mood, MoodEntryDTO.class));
        }

        return result;
    }

    @Override
    public MoodEntryDTO updateMood(String email, Long id, MoodEntryDTO dto) {

        MoodEntry entry = moodEntryRepository.findById(id)
                .orElseThrow(() -> new CustomException("Mood entry not found"));

        if (!entry.getUser().getEmail().equals(email)) {
            throw new CustomException("Unauthorized");
        }

        entry.setMoodType(MoodType.valueOf(dto.getMoodType().toUpperCase()));
        entry.setNote(dto.getNote());

        MoodEntry updated = moodEntryRepository.save(entry);

        return modelMapper.map(updated, MoodEntryDTO.class);
    }

    @Override
    public void deleteMood(String email, Long id) {

        MoodEntry entry = moodEntryRepository.findById(id)
                .orElseThrow(() -> new CustomException("Mood entry not found"));

        if (!entry.getUser().getEmail().equals(email)) {
            throw new CustomException("Unauthorized");
        }

        moodEntryRepository.delete(entry);
    }


    @Override
    public List<MoodTrendDTO> getWeeklyTrend(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        LocalDate today = LocalDate.now();

        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);

        LocalDate weekEnd = weekStart.plusDays(6);

        List<MoodEntry> moods =
                moodEntryRepository.findByUserAndCreatedDateBetween(user, weekStart, weekEnd);

        Map<String, Long> counts = new HashMap<>();

        for (MoodEntry mood : moods) {

            String moodName = mood.getMoodType().name();

            counts.put(moodName, counts.getOrDefault(moodName, 0L) + 1);
        }

        List<MoodTrendDTO> result = new ArrayList<>();

        result.add(new MoodTrendDTO(weekStart, weekEnd, counts));

        return result;
    }

    @Override
    public List<MoodTrendDTO> getMonthlyTrend(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        LocalDate today = LocalDate.now();

        LocalDate startOfYear = LocalDate.of(today.getYear(), 1, 1);

        List<MoodEntry> moods =
                moodEntryRepository.findByUserAndCreatedDateBetween(user, startOfYear, today);

        List<MoodTrendDTO> result = new ArrayList<>();

        for (int month = 1; month <= today.getMonthValue(); month++) {

            LocalDate monthStart = LocalDate.of(today.getYear(), month, 1);
            LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

            Map<String, Long> counts = new HashMap<>();

            for (MoodEntry mood : moods) {

                if (!mood.getCreatedDate().isBefore(monthStart) &&
                        !mood.getCreatedDate().isAfter(monthEnd)) {

                    String moodName = mood.getMoodType().name();

                    counts.put(moodName, counts.getOrDefault(moodName, 0L) + 1);
                }
            }

            result.add(new MoodTrendDTO(monthStart, monthEnd, counts));
        }

        return result;
    }

    @Override
    public double getEmotionalRiskScore(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        LocalDate last30Days = LocalDate.now().minusDays(30);

        List<MoodEntry> moods = moodEntryRepository.findByUserAndCreatedDateAfter(user, last30Days);

        long negativeCount = 0;

        for (MoodEntry mood : moods) {

            if (mood.getMoodType() == MoodType.SAD ||
                    mood.getMoodType() == MoodType.STRESSED) {

                negativeCount++;
            }
        }

        if (moods.size() == 0) {
            return 0;
        }

        return (negativeCount * 100.0) / moods.size();
    }
}