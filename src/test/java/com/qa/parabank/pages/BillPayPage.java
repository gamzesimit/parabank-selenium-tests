package com.qa.parabank.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class BillPayPage extends BasePage {

    private static final String PAYEE_ACCOUNT = "54321";

    @FindBy(name = "payee.name")
    private WebElement payeeName;

    @FindBy(name = "payee.address.street")
    private WebElement street;

    @FindBy(name = "payee.address.city")
    private WebElement city;

    @FindBy(name = "payee.address.state")
    private WebElement state;

    @FindBy(name = "payee.address.zipCode")
    private WebElement zipCode;

    @FindBy(name = "payee.phoneNumber")
    private WebElement phone;

    @FindBy(name = "payee.accountNumber")
    private WebElement payeeAccount;

    @FindBy(name = "verifyAccount")
    private WebElement verifyAccount;

    @FindBy(name = "amount")
    private WebElement amount;

    @FindBy(name = "fromAccountId")
    private WebElement fromAccount;

    @FindBy(css = "input[value='Send Payment']")
    private WebElement sendButton;

    @FindBy(id = "billpayResult")
    private WebElement result;

    @FindBy(id = "billpayError")
    private WebElement error;

    public BillPayPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        openStable("billpay.htm");
    }

    /** Pays a utility bill and returns true when the application reports it complete. */
    public boolean pay(String value, String from) {
        type(payeeName, "City Utilities");
        type(street, "1 Main St");
        type(city, "Austin");
        type(state, "TX");
        type(zipCode, "78701");
        type(phone, "5125550111");
        type(payeeAccount, PAYEE_ACCOUNT);
        type(verifyAccount, PAYEE_ACCOUNT);
        type(amount, value);
        selectWhenLoaded(fromAccount, from);
        click(sendButton);
        return waitForOutcome(() -> isShown(result), () -> isShown(error));
    }
}
