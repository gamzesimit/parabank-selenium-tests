package com.qa.parabank.data;

import java.util.concurrent.atomic.AtomicInteger;

/** A customer with a username unique to this run, so tests never collide. */
public record TestUser(String firstName, String lastName, String street, String city, String state,
                       String zipCode, String phone, String ssn, String username, String password) {

    private static final AtomicInteger SEQUENCE = new AtomicInteger();

    public static TestUser unique() {
        // wall clock plus a counter: unique across runs and within one, on any machine
        String stamp = (System.currentTimeMillis() % 1_000_000_000L) + "" + SEQUENCE.incrementAndGet();
        return new TestUser("Qa", "Tester" + stamp, "100 Congress Ave", "Austin", "TX", "78701",
                "5125550100", "123-45-6789", "qa_" + stamp, "Passw0rd!23");
    }
}
