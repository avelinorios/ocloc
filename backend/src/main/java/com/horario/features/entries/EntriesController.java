package com.horario.features.entries;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/entries")
public class EntriesController {
    private final EntriesService entriesService;

    public EntriesController(EntriesService entriesService) {
        this.entriesService = entriesService;
    }

    @GetMapping
    public ResponseEntity<List<TimeEntry>> getEntries(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        try {
            Instant fromInstant = from != null ? Instant.parse(from) : null;
            Instant toInstant = to != null ? Instant.parse(to) : null;
            List<TimeEntry> entries = entriesService.getEntries(fromInstant, toInstant);
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
