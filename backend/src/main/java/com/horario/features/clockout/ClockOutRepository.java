package com.horario.features.clockout;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;

@Repository
public class ClockOutRepository {
    private final JdbcTemplate jdbcTemplate;

    public ClockOutRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<TimeEntry> timeEntryRowMapper = (rs, rowNum) -> {
        TimeEntry entry = new TimeEntry();
        entry.setId(rs.getInt("id"));
        entry.setUserId(rs.getInt("user_id"));
        entry.setClockIn(rs.getTimestamp("clock_in").toInstant());
        if (rs.getTimestamp("clock_out") != null) {
            entry.setClockOut(rs.getTimestamp("clock_out").toInstant());
        }
        entry.setNote(rs.getString("note"));
        return entry;
    };

    public TimeEntry clockOut(Integer userId, Instant clockOut) {
        String selectSql = """
            SELECT id, user_id, clock_in, clock_out, note
            FROM time_entries
            WHERE user_id = ? AND clock_out IS NULL
            ORDER BY clock_in DESC
            LIMIT 1
        """;
        var openEntries = jdbcTemplate.query(selectSql, timeEntryRowMapper, userId);
        if (openEntries.isEmpty()) {
            return null;
        }

        TimeEntry entry = openEntries.get(0);
        int updated = jdbcTemplate.update("""
            UPDATE time_entries
            SET clock_out = ?
            WHERE id = ?
        """, Timestamp.from(clockOut), entry.getId());

        if (updated == 0) {
            return null;
        }

        entry.setClockOut(clockOut);
        return entry;
    }
}
