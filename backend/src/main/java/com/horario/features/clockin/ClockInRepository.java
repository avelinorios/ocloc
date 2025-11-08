package com.horario.features.clockin;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Map;

@Repository
public class ClockInRepository {
    private final JdbcTemplate jdbcTemplate;

    public ClockInRepository(JdbcTemplate jdbcTemplate) {
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

    public TimeEntry create(TimeEntry entry) {
        String sql = """
            INSERT INTO time_entries(user_id, clock_in, note)
            VALUES(?, ?, ?)
        """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, entry.getUserId());
            ps.setTimestamp(2, Timestamp.from(entry.getClockIn()));
            ps.setString(3, entry.getNote());
            return ps;
        }, keyHolder);

        Number key = extractGeneratedId(keyHolder);

        return jdbcTemplate.queryForObject("""
            SELECT id, user_id, clock_in, clock_out, note
            FROM time_entries
            WHERE id = ?
        """, timeEntryRowMapper, key.longValue());
    }

    private Number extractGeneratedId(KeyHolder keyHolder) {
        if (keyHolder.getKeyList() != null && !keyHolder.getKeyList().isEmpty()) {
            Map<String, Object> keys = keyHolder.getKeyList().get(0);
            Object value = keys.get("id");
            if (value instanceof Number number) {
                return number;
            }
            // fallback: first numeric key
            for (Object candidate : keys.values()) {
                if (candidate instanceof Number number) {
                    return number;
                }
            }
        }
        throw new IllegalStateException("No se pudo obtener el ID generado para el fichaje");
    }
}
