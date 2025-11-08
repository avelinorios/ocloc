package com.horario.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Profile("test")
public class TestDatabaseConfig {

    @Bean
    @Primary
    public CommandLineRunner testDatabaseInit(JdbcTemplate jdbcTemplate) {
        return args -> {
            createSchema(jdbcTemplate);
            createDemoUser(jdbcTemplate);
        };
    }

    private void createSchema(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                email VARCHAR(255) UNIQUE
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS time_entries (
                id SERIAL PRIMARY KEY,
                user_id INTEGER NOT NULL,
                clock_in TIMESTAMP WITH TIME ZONE NOT NULL,
                clock_out TIMESTAMP WITH TIME ZONE,
                note VARCHAR(255),
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            )
        """);

        jdbcTemplate.execute("""
            CREATE INDEX IF NOT EXISTS idx_time_entries_user ON time_entries(user_id)
        """);

        jdbcTemplate.execute("""
            CREATE INDEX IF NOT EXISTS idx_time_entries_clock_in ON time_entries(clock_in)
        """);
    }

    private void createDemoUser(JdbcTemplate jdbcTemplate) {
        try {
            jdbcTemplate.update("""
                INSERT INTO users (id, name, email) 
                VALUES (1, 'Demo', 'demo@acme.test')
            """);
            jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 2");
        } catch (Exception e) {
            // User might already exist, ignore
        }
    }
}
