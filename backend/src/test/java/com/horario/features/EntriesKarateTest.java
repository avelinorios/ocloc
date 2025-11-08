package com.horario.features;

import com.intuit.karate.junit5.Karate;
import com.horario.BaseKarateTest;

public class EntriesKarateTest extends BaseKarateTest {
    @Karate.Test
    Karate testEntries() {
        return Karate.run("classpath:entries/entries.feature")
            .systemProperty("base.url", getBaseUrl());
    }
}
