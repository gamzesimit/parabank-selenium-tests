package com.qa.parabank.base;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Builds a Chrome session: a local browser by default, or a browser on a Selenium
 * Grid when SELENIUM_REMOTE_URL is set. Selenium Manager resolves the local driver.
 */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver create() {
        ChromeOptions options = new ChromeOptions();
        if (Config.HEADLESS) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1366,900", "--no-sandbox", "--disable-dev-shm-usage");

        WebDriver driver = Config.REMOTE_URL.isEmpty() ? new ChromeDriver(options) : remote(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        return driver;
    }

    private static WebDriver remote(ChromeOptions options) {
        try {
            return new RemoteWebDriver(URI.create(Config.REMOTE_URL).toURL(), options);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("SELENIUM_REMOTE_URL is not a valid URL: " + Config.REMOTE_URL, e);
        }
    }
}
