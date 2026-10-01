package com.qa.parabank.pages;

import com.qa.parabank.base.Config;
import java.time.Duration;
import java.util.function.Supplier;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Shared behaviour for every page object. Elements are declared with {@code @FindBy}
 * and initialised by PageFactory, so a page class lists what is on the screen and
 * the methods say what a customer does with it.
 */
public abstract class BasePage {

    /** Text the application shows when a request fails on the server. */
    private static final String INTERNAL_ERROR = "An internal error has occurred";
    private static final int OPEN_ATTEMPTS = 5;

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    @FindBy(id = "rightPanel")
    private WebElement rightPanel;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }

    /**
     * Opens a page and retries while the server answers with its internal error page.
     * The environment does that intermittently; it is a property of the application,
     * not of the tests, and is written up rather than hidden.
     */
    protected void openStable(String path) {
        for (int i = 0; i < OPEN_ATTEMPTS; i++) {
            driver.get(Config.BASE_URL + path);
            if (!rightPanelText().contains(INTERNAL_ERROR)) {
                return;
            }
        }
        throw new IllegalStateException(path + " returned the internal error page "
                + OPEN_ATTEMPTS + " times in a row");
    }

    public String rightPanelText() {
        return wait.until(ExpectedConditions.visibilityOf(rightPanel)).getText().trim();
    }

    protected void type(WebElement field, String text) {
        wait.until(ExpectedConditions.visibilityOf(field));
        field.clear();
        field.sendKeys(text);
    }

    protected void click(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    /** Selects an option once the list has been filled, which happens after the page renders. */
    protected void selectWhenLoaded(WebElement list, String visibleText) {
        wait.until(d -> new Select(list).getOptions().stream()
                .anyMatch(o -> o.getText().trim().equals(visibleText)));
        new Select(list).selectByVisibleText(visibleText);
    }

    /** True when the element is on the page and shown, without waiting for it. */
    protected static boolean isShown(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /** Waits until one of two outcomes is on screen and reports which. */
    protected boolean waitForOutcome(Supplier<Boolean> success, Supplier<Boolean> failure) {
        wait.until(d -> success.get() || failure.get());
        return success.get();
    }
}
