package com.qa.parabank.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class TransferFundsPage extends BasePage {

    @FindBy(id = "amount")
    private WebElement amount;

    @FindBy(id = "fromAccountId")
    private WebElement fromAccount;

    @FindBy(id = "toAccountId")
    private WebElement toAccount;

    @FindBy(css = "input[value='Transfer']")
    private WebElement transferButton;

    @FindBy(id = "showResult")
    private WebElement result;

    @FindBy(id = "showError")
    private WebElement error;

    public TransferFundsPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        openStable("transfer.htm");
    }

    /** Submits a transfer and returns true when the application reports it complete. */
    public boolean transfer(String value, String from, String to) {
        type(amount, value);
        selectWhenLoaded(fromAccount, from);
        selectWhenLoaded(toAccount, to);
        click(transferButton);
        return waitForOutcome(() -> isShown(result), () -> isShown(error));
    }
}
