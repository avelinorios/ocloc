package com.horario.features;

import com.intuit.karate.junit5.Karate;
import com.horario.BaseKarateTest;

public class UsersKarateTest extends BaseKarateTest {
    @Karate.Test
    Karate testUsers() {
        return Karate.run("classpath:users/users.feature")
            .systemProperty("base.url", getBaseUrl());
    }
}
