package com.horario.features.entries;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public class EntriesRepository {
    private final JdbcTemplate jdbcTemplate;

    public EntriesRepository(JdbcTemplate jdbcTemplate) {
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

    public List<TimeEntry> findByUserIdAndDateRange(Integer userId, Instant from, Instant to) {
        if (from != null && to != null) {
            String sql = """
                SELECT * FROM time_entries
                WHERE user_id = ?
                  AND clock_in >= ?
                  AND clock_in < ?
                ORDER BY clock_in DESC
            """;
            return jdbcTemplate.query(sql, timeEntryRowMapper, userId, 
                java.sql.Timestamp.from(from), java.sql.Timestamp.from(to));
        } else if (from != null) {
            String sql = """
                SELECT * FROM time_entries
                WHERE user_id = ?
                  AND clock_in >= ?
                ORDER BY clock_in DESC
            """;
            return jdbcTemplate.query(sql, timeEntryRowMapper, userId, 
                java.sql.Timestamp.from(from));
        } else if (to != null) {
            String sql = """
                SELECT * FROM time_entries
                WHERE user_id = ?
                  AND clock_in < ?
                ORDER BY clock_in DESC
            """;
            return jdbcTemplate.query(sql, timeEntryRowMapper, userId, 
                java.sql.Timestamp.from(to));
        } else {
            String sql = """
                SELECT * FROM time_entries
                WHERE user_id = ?
                ORDER BY clock_in DESC
            """;
            return jdbcTemplate.query(sql, timeEntryRowMapper, userId);
        }
    }
}

