package com.horario.features;

import com.intuit.karate.junit5.Karate;
import com.horario.BaseKarateTest;

public class ClockInKarateTest extends BaseKarateTest {
    @Karate.Test
    Karate testClockIn() {
        return Karate.run("classpath:clockin/clockin.feature")
            .systemProperty("base.url", getBaseUrl());
    }
}
