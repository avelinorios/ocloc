package com.horario.features;

import com.intuit.karate.junit5.Karate;
import com.horario.BaseKarateTest;

public class SummaryKarateTest extends BaseKarateTest {
    @Karate.Test
    Karate testSummary() {
        return Karate.run("classpath:summary/summary.feature")
            .systemProperty("base.url", getBaseUrl());
    }
}
