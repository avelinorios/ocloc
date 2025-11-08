package com.horario.features;

import com.intuit.karate.junit5.Karate;
import com.horario.BaseKarateTest;

public class ClockOutKarateTest extends BaseKarateTest {
    @Karate.Test
    Karate testClockOut() {
        return Karate.run("classpath:clockout/clockout.feature")
            .systemProperty("base.url", getBaseUrl());
    }
}
