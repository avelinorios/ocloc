package com.horario.features.users;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<User> userRowMapper = (rs, rowNum) ->
        new User(rs.getInt("id"), rs.getString("name"), rs.getString("email"));

    public User create(User user) {
        String sql = "INSERT INTO users(name, email) VALUES(?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            return ps;
        }, keyHolder);

        Number key = extractGeneratedId(keyHolder);

        return jdbcTemplate.queryForObject(
            "SELECT id, name, email FROM users WHERE id = ?",
            userRowMapper,
            key.longValue()
        );
    }

    private Number extractGeneratedId(KeyHolder keyHolder) {
        if (keyHolder.getKeyList() != null && !keyHolder.getKeyList().isEmpty()) {
            Map<String, Object> keys = keyHolder.getKeyList().get(0);
            Object value = keys.get("id");
            if (value instanceof Number number) {
                return number;
            }
            for (Object candidate : keys.values()) {
                if (candidate instanceof Number number) {
                    return number;
                }
            }
        }
        throw new IllegalStateException("No se pudo crear el usuario");
    }

    public Optional<User> findById(Integer id) {
        String sql = "SELECT id, name, email FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
