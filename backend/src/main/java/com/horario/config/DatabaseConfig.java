package com.horario.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
@Profile("!test")
public class DatabaseConfig {

    @Bean
    public CommandLineRunner initDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            waitForDatabase(jdbcTemplate);
            createSchema(jdbcTemplate);
            createDemoUser(jdbcTemplate);
        };
    }

    private void waitForDatabase(JdbcTemplate jdbcTemplate) {
        int maxRetries = 30;
        for (int i = 0; i < maxRetries; i++) {
            try {
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                System.out.println("✅ Base de datos conectada");
                return;
            } catch (Exception e) {
                if (i == maxRetries - 1) {
                    throw new RuntimeException("No se pudo conectar a la base de datos", e);
                }
                System.out.println("⏳ Esperando base de datos... (" + (i + 1) + "/" + maxRetries + ")");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ie);
                }
            }
        }
    }

    private void createSchema(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                name TEXT NOT NULL,
                email TEXT UNIQUE
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS time_entries (
                id SERIAL PRIMARY KEY,
                user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                clock_in TIMESTAMPTZ NOT NULL,
                clock_out TIMESTAMPTZ,
                note TEXT
            )
        """);

        jdbcTemplate.execute("""
            CREATE INDEX IF NOT EXISTS idx_time_entries_user ON time_entries(user_id)
        """);

        jdbcTemplate.execute("""
            CREATE INDEX IF NOT EXISTS idx_time_entries_clock_in ON time_entries(clock_in)
        """);

        System.out.println("✅ Tablas creadas correctamente");
    }

    private void createDemoUser(JdbcTemplate jdbcTemplate) {
        try {
            jdbcTemplate.update("""
                INSERT INTO users (name, email) 
                VALUES ('Demo', 'demo@acme.test') 
                ON CONFLICT (email) DO NOTHING
            """);
            System.out.println("✅ Usuario demo creado");
        } catch (Exception e) {
            System.out.println("ℹ️ Usuario demo: " + e.getMessage());
        }
    }
}

