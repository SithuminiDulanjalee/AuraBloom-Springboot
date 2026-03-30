package lk.ijse.aurabloom_backend.service.custom.impl;

import lk.ijse.aurabloom_backend.dto.JournalEntryDTO;
import lk.ijse.aurabloom_backend.entity.JournalEntry;
import lk.ijse.aurabloom_backend.entity.MoodType;
import lk.ijse.aurabloom_backend.entity.User;
import lk.ijse.aurabloom_backend.repository.JournalEntryRepository;
import lk.ijse.aurabloom_backend.repository.UserRepository;
import lk.ijse.aurabloom_backend.service.custom.BadgeService;
import lk.ijse.aurabloom_backend.service.custom.JournalService;
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
public class JournalServiceImpl implements JournalService {

    private final JournalEntryRepository journalEntryRepository;
    private final UserRepository userRepository;
    private final BadgeService badgeService;
    @Autowired
    private final ModelMapper modelMapper;

    @Override
    public JournalEntryDTO createEntry(String email, JournalEntryDTO dto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        JournalEntry entry = JournalEntry.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .mood(MoodType.valueOf(dto.getMood().toUpperCase()))
                .tags(dto.getTags())
                .createdDate(LocalDate.now())
                .user(user)
                .build();

        JournalEntry saved = journalEntryRepository.save(entry);
        badgeService.evaluateJournalBadges(email);

        return modelMapper.map(saved, JournalEntryDTO.class);
    }

    @Override
    public List<JournalEntryDTO> getAllEntries(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<JournalEntry> entries = journalEntryRepository.findByUser(user);

        List<JournalEntryDTO> result = new ArrayList<>();

        for (JournalEntry entry : entries) {
            result.add(modelMapper.map(entry, JournalEntryDTO.class));
        }

        return result;
    }

    @Override
    public JournalEntryDTO updateEntry(String email, Long id, JournalEntryDTO dto) {

        JournalEntry entry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Journal entry not found"));

        if (!entry.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        entry.setTitle(dto.getTitle());
        entry.setContent(dto.getContent());
        entry.setMood(MoodType.valueOf(dto.getMood().toUpperCase()));
        entry.setTags(dto.getTags());

        JournalEntry updated = journalEntryRepository.save(entry);

        return modelMapper.map(updated, JournalEntryDTO.class);
    }

    @Override
    public void deleteEntry(String email, Long id) {

        JournalEntry entry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Journal entry not found"));

        if (!entry.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        journalEntryRepository.delete(entry);
    }

    @Override
    public Map<String, Object> getWeeklySummary(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate oneWeekAgo = LocalDate.now().minusDays(7);

        List<JournalEntry> entries =
                journalEntryRepository.findByUserAndCreatedDateAfter(user, oneWeekAgo);

        Map<String, Integer> moodCounts = new HashMap<>();

        for (MoodType type : MoodType.values()) {
            moodCounts.put(type.name(), 0);
        }

        for (JournalEntry entry : entries) {
            String moodName = entry.getMood().name();
            moodCounts.put(moodName, moodCounts.getOrDefault(moodName, 0) + 1);
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalEntries", entries.size());
        summary.put("moodCounts", moodCounts);

        return summary;
    }
}