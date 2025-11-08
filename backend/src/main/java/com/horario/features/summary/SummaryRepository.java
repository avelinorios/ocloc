package com.horario.features.summary;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SummaryRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Madrid");

    public SummaryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<DailySummary> getSummary(Integer userId, Instant from, Instant to) {
        String sql = """
            SELECT clock_in, clock_out
            FROM time_entries
            WHERE user_id = ?
              AND (? IS NULL OR clock_in >= ?)
              AND (? IS NULL OR clock_in < ?)
            ORDER BY clock_in DESC
        """;

        var entries = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Timestamp clockIn = rs.getTimestamp("clock_in");
            Timestamp clockOut = rs.getTimestamp("clock_out");
            return new TimeRange(
                clockIn.toInstant(),
                clockOut != null ? clockOut.toInstant() : null
            );
        }, userId, from, from, to, to);

        Map<LocalDate, Double> totals = new LinkedHashMap<>();
        for (TimeRange entry : entries) {
            Instant end = entry.clockOut() != null ? entry.clockOut() : Instant.now();
            double hours = Duration.between(entry.clockIn(), end).toMillis() / 3_600_000d;
            LocalDate day = entry.clockIn().atZone(ZONE_ID).toLocalDate();
            totals.merge(day, hours, Double::sum);
        }

        return totals.entrySet().stream()
            .sorted(Map.Entry.<LocalDate, Double>comparingByKey(Comparator.reverseOrder()))
            .map(e -> new DailySummary(
                e.getKey(),
                BigDecimal.valueOf(e.getValue()).setScale(2, RoundingMode.HALF_UP).doubleValue()
            ))
            .toList();
    }

    private record TimeRange(Instant clockIn, Instant clockOut) {
    }
}
