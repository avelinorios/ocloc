package com.horario.features.summary;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static com.horario.features.users.UserConstants.DEFAULT_USER_ID;

@Service
public class SummaryService {
    private final SummaryRepository summaryRepository;

    public SummaryService(SummaryRepository summaryRepository) {
        this.summaryRepository = summaryRepository;
    }

    public List<DailySummary> getSummary(Instant from, Instant to) {
        return summaryRepository.getSummary(DEFAULT_USER_ID, from, to);
    }
}
