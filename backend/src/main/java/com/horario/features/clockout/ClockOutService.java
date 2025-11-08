package com.horario.features.clockout;

import org.springframework.stereotype.Service;

import static com.horario.features.users.UserConstants.DEFAULT_USER_ID;

import java.time.Instant;

@Service
public class ClockOutService {
    private final ClockOutRepository clockOutRepository;

    public ClockOutService(ClockOutRepository clockOutRepository) {
        this.clockOutRepository = clockOutRepository;
    }

    public TimeEntry clockOut() {
        TimeEntry entry = clockOutRepository.clockOut(DEFAULT_USER_ID, Instant.now());
        if (entry == null) {
            throw new RuntimeException("No hay fichaje abierto");
        }
        return entry;
    }
}
