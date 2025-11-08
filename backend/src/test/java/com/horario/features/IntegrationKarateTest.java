package com.horario.features;

import com.intuit.karate.junit5.Karate;
import com.horario.BaseKarateTest;

public class IntegrationKarateTest extends BaseKarateTest {
    @Karate.Test
    Karate testIntegration() {
        return Karate.run("classpath:integration/integration.feature")
            .systemProperty("base.url", getBaseUrl());
    }
}
