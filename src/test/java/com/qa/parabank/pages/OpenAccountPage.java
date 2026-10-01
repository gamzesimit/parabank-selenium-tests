package com.qa.parabank.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class OpenAccountPage extends BasePage {

    @FindBy(id = "type")
    private WebElement accountType;

    @FindBy(id = "fromAccountId")
    private WebElement fundingAccount;

    @FindBy(css = "input[value='Open New Account']")
    private WebElement openButton;

    @FindBy(id = "newAccountId")
    private WebElement newAccountId;

    public OpenAccountPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        openStable("openaccount.htm");
    }

    /** Opens an account of the given type funded from the first account, and returns its number. */
    public String openAccount(String type) {
        new Select(wait.until(ExpectedConditions.visibilityOf(accountType))).selectByVisibleText(type);
        wait.until(d -> !new Select(fundingAccount).getOptions().isEmpty());
        new Select(fundingAccount).selectByIndex(0);
        click(openButton);
        return wait.until(ExpectedConditions.visibilityOf(newAccountId)).getText().trim();
    }
}
