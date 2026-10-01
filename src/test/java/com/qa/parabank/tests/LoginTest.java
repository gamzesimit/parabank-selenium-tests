package com.qa.parabank.tests;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.qa.parabank.base.BaseTest;
import com.qa.parabank.data.ExcelReader;
import com.qa.parabank.pages.LoginPage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/** Sign-in cases read from src/test/resources/testdata/test-data.xlsx. */
public class LoginTest extends BaseTest {

    @DataProvider(name = "invalidLogins")
    public Object[][] invalidLogins() {
        return ExcelReader.rows("testdata/test-data.xlsx", "invalid-logins");
    }

    @Test(dataProvider = "invalidLogins",
          description = "Sign-in is refused with a message for credentials that do not match")
    public void invalidCredentialsAreRefused(String caseName, String username, String password,
                                            String expectedMessage) {
        LoginPage page = new LoginPage(driver);
        page.open();
        page.logIn(username, password);

        assertFalse(page.isSignedIn(), caseName + ": the customer must not be signed in");
        assertTrue(page.rightPanelText().contains(expectedMessage),
                caseName + ": expected \"" + expectedMessage + "\", the page said: " + page.rightPanelText());
    }
}
