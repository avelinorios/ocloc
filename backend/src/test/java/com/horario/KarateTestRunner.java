package com.horario;

import com.intuit.karate.junit5.Karate;

public class KarateTestRunner {
    @Karate.Test
    Karate testUsers() {
        return Karate.run("users").relativeTo(getClass());
    }

    @Karate.Test
    Karate testClockIn() {
        return Karate.run("clockin").relativeTo(getClass());
    }

    @Karate.Test
    Karate testClockOut() {
        return Karate.run("clockout").relativeTo(getClass());
    }

    @Karate.Test
    Karate testEntries() {
        return Karate.run("entries").relativeTo(getClass());
    }

    @Karate.Test
    Karate testSummary() {
        return Karate.run("summary").relativeTo(getClass());
    }

    @Karate.Test
    Karate testAll() {
        return Karate.run().relativeTo(getClass());
    }
}

