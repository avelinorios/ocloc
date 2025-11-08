package com.horario.features.clockout;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/clock-out")
public class ClockOutController {
    private final ClockOutService clockOutService;

    public ClockOutController(ClockOutService clockOutService) {
        this.clockOutService = clockOutService;
    }

    @PostMapping
    public ResponseEntity<?> clockOut() {
        try {
            TimeEntry entry = clockOutService.clockOut();
            return ResponseEntity.ok(entry);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
