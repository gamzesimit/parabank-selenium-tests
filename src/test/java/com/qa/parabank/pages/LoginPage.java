package com.qa.parabank.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

    @FindBy(name = "username")
    private WebElement username;

    @FindBy(name = "password")
    private WebElement password;

    @FindBy(css = "input[value='Log In']")
    private WebElement logInButton;

    @FindBy(linkText = "Log Out")
    private WebElement logOutLink;

    @FindBy(css = "#rightPanel .error")
    private WebElement errorMessage;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        openStable("index.htm");
    }

    public void logIn(String user, String pass) {
        type(username, user);
        type(password, pass);
        click(logInButton);
        waitForOutcome(() -> isShown(logOutLink), () -> isShown(errorMessage));
    }

    public boolean isSignedIn() {
        return isShown(logOutLink);
    }

    public void logOut() {
        click(logOutLink);
        wait.until(d -> isShown(username));
    }
}
