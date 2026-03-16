package lk.ijse.aurabloom_backend.controller;

import jakarta.validation.Valid;
import lk.ijse.aurabloom_backend.dto.JournalEntryDTO;
import lk.ijse.aurabloom_backend.service.custom.impl.JournalServiceImpl;
import lk.ijse.aurabloom_backend.util.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/journals")
@CrossOrigin(origins = "*")
@Validated
@PreAuthorize("hasRole('USER')")
public class JournalController {

    private final JournalServiceImpl journalService;

    @PostMapping
    public ResponseEntity<APIResponse<JournalEntryDTO>> createEntry(
            Principal principal,
            @Valid @RequestBody JournalEntryDTO dto) {

        JournalEntryDTO entry = journalService.createEntry(principal.getName(), dto);

        return new ResponseEntity<>(
                new APIResponse<>(201,"Journal entry created", entry),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<JournalEntryDTO>>> getAllEntries(
            Principal principal) {

        List<JournalEntryDTO> entries = journalService.getAllEntries(principal.getName());

        return new ResponseEntity<>(
                new APIResponse<>(200,"Journal entries fetched", entries),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<JournalEntryDTO>> updateEntry(
            Principal principal,
            @PathVariable Long id,
            @Valid @RequestBody JournalEntryDTO dto) {

        JournalEntryDTO entry = journalService.updateEntry(principal.getName(), id, dto);

        return new ResponseEntity<>(
                new APIResponse<>(200,"Journal entry updated", entry),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteEntry(
            Principal principal,
            @PathVariable Long id) {

        journalService.deleteEntry(principal.getName(), id);

        return new ResponseEntity<>(
                new APIResponse<>(200,"Journal entry deleted", null),
                HttpStatus.OK
        );
    }

    @GetMapping("/weekly-summary")
    public ResponseEntity<APIResponse<Map<String,Object>>> getWeeklySummary(
            Principal principal) {

        Map<String,Object> summary = journalService.getWeeklySummary(principal.getName());

        return new ResponseEntity<>(
                new APIResponse<>(200,"Weekly journal summary", summary),
                HttpStatus.OK
        );
    }
}