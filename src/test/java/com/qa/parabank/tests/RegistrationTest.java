package com.qa.parabank.tests;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.qa.parabank.base.BaseTest;
import com.qa.parabank.data.TestUser;
import com.qa.parabank.pages.LoginPage;
import org.testng.annotations.Test;

public class RegistrationTest extends BaseTest {

    @Test(groups = "smoke", description = "A new customer is registered and welcomed by name")
    public void aNewCustomerIsWelcomedByName() {
        TestUser user = registerNewCustomer();
        LoginPage page = new LoginPage(driver);

        assertTrue(page.rightPanelText().contains("Welcome " + user.username()),
                "the confirmation must name the new customer, the page said: " + page.rightPanelText());
        assertTrue(page.isSignedIn(), "a newly registered customer must be signed in");
    }

    @Test(description = "A registered customer can sign out and sign back in with the same password")
    public void aRegisteredCustomerCanSignOutAndBackIn() {
        TestUser user = registerNewCustomer();
        LoginPage page = new LoginPage(driver);
        page.logOut();
        assertFalse(page.isSignedIn(), "the customer must be signed out after Log Out");

        page.open();
        page.logIn(user.username(), user.password());
        assertTrue(page.isSignedIn(), "the customer must be able to sign back in");
    }
}
