package com.horario.features.clockin;

import org.springframework.stereotype.Service;

import static com.horario.features.users.UserConstants.DEFAULT_USER_ID;

import java.time.Instant;

@Service
public class ClockInService {
    private final ClockInRepository clockInRepository;

    public ClockInService(ClockInRepository clockInRepository) {
        this.clockInRepository = clockInRepository;
    }

    public TimeEntry clockIn(String note) {
        TimeEntry entry = new TimeEntry();
        entry.setUserId(DEFAULT_USER_ID);
        entry.setClockIn(Instant.now());
        entry.setNote(note);
        return clockInRepository.create(entry);
    }
}
