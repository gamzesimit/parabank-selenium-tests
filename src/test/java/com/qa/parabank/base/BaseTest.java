package com.qa.parabank.base;

import com.qa.parabank.data.TestUser;
import com.qa.parabank.pages.RegisterPage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/** One browser per test, closed afterwards, with a screenshot kept when a test fails. */
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void openBrowser() {
        driver = DriverFactory.create();
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser(ITestResult result) {
        if (driver == null) {
            return;
        }
        if (result.getStatus() == ITestResult.FAILURE) {
            saveScreenshot(result.getMethod().getMethodName());
        }
        driver.quit();
    }

    /** Registers a fresh customer, so no test depends on data another test left behind. */
    protected TestUser registerNewCustomer() {
        TestUser user = TestUser.unique();
        RegisterPage register = new RegisterPage(driver);
        register.open();
        register.register(user);
        return user;
    }

    private void saveScreenshot(String name) {
        try {
            File shot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path dir = Path.of("target", "screenshots");
            Files.createDirectories(dir);
            Files.copy(shot.toPath(), dir.resolve(name + ".png"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | WebDriverException e) {
            System.err.println("Screenshot not saved for " + name + ": " + e.getMessage());
        }
    }
}
