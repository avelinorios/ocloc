package com.horario.features.clockin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeEntry {
    private Integer id;
    private Integer userId;
    private Instant clockIn;
    private Instant clockOut;
    private String note;
}

