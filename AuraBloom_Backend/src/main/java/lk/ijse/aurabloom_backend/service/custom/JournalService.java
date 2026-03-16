package lk.ijse.aurabloom_backend.service.custom;


import lk.ijse.aurabloom_backend.dto.JournalEntryDTO;

import java.util.List;
import java.util.Map;

public interface JournalService {
    public JournalEntryDTO createEntry(String email, JournalEntryDTO dto);

    public List<JournalEntryDTO> getAllEntries(String email);

    public JournalEntryDTO updateEntry(String email, Long id, JournalEntryDTO dto) ;

    public void deleteEntry(String email, Long id) ;

    public Map<String, Object> getWeeklySummary(String email) ;
}
