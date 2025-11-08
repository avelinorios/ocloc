package com.horario.features.summary;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/summary")
public class SummaryController {
    private final SummaryService summaryService;

    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @GetMapping
    public ResponseEntity<List<DailySummary>> getSummary(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        try {
            Instant fromInstant = from != null ? Instant.parse(from) : null;
            Instant toInstant = to != null ? Instant.parse(to) : null;
            return ResponseEntity.ok(summaryService.getSummary(fromInstant, toInstant));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
