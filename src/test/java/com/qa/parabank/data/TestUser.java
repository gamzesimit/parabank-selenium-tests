package com.qa.parabank.data;

/** A customer with a username unique to this run, so tests never collide. */
public record TestUser(String firstName, String lastName, String street, String city, String state,
                       String zipCode, String phone, String ssn, String username, String password) {

    public static TestUser unique() {
        String stamp = Long.toString(System.nanoTime()).substring(4, 13);
        return new TestUser("Qa", "Tester" + stamp, "100 Congress Ave", "Austin", "TX", "78701",
                "5125550100", "123-45-6789", "qa_" + stamp, "Passw0rd!23");
    }
}
