package com.horario.features.entries;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static com.horario.features.users.UserConstants.DEFAULT_USER_ID;

@Service
public class EntriesService {
    private final EntriesRepository entriesRepository;

    public EntriesService(EntriesRepository entriesRepository) {
        this.entriesRepository = entriesRepository;
    }

    public List<TimeEntry> getEntries(Instant from, Instant to) {
        return entriesRepository.findByUserIdAndDateRange(DEFAULT_USER_ID, from, to);
    }
}
