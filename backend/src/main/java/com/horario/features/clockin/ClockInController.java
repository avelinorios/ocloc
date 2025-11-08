package com.horario.features.clockin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/clock-in")
public class ClockInController {
    private final ClockInService clockInService;

    public ClockInController(ClockInService clockInService) {
        this.clockInService = clockInService;
    }

    @PostMapping
    public ResponseEntity<?> clockIn(@RequestBody(required = false) ClockInRequest request) {
        try {
            String note = request != null ? request.getNote() : null;
            TimeEntry entry = clockInService.clockIn(note);
            return ResponseEntity.ok(entry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
