package com.qa.parabank.pages;

import com.qa.parabank.data.TestUser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class RegisterPage extends BasePage {

    @FindBy(id = "customer.firstName")
    private WebElement firstName;

    @FindBy(id = "customer.lastName")
    private WebElement lastName;

    @FindBy(id = "customer.address.street")
    private WebElement street;

    @FindBy(id = "customer.address.city")
    private WebElement city;

    @FindBy(id = "customer.address.state")
    private WebElement state;

    @FindBy(id = "customer.address.zipCode")
    private WebElement zipCode;

    @FindBy(id = "customer.phoneNumber")
    private WebElement phone;

    @FindBy(id = "customer.ssn")
    private WebElement ssn;

    @FindBy(id = "customer.username")
    private WebElement username;

    @FindBy(id = "customer.password")
    private WebElement password;

    @FindBy(id = "repeatedPassword")
    private WebElement repeatedPassword;

    @FindBy(css = "input[value='Register']")
    private WebElement registerButton;

    @FindBy(linkText = "Log Out")
    private WebElement logOutLink;

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        openStable("register.htm");
    }

    public void register(TestUser user) {
        type(firstName, user.firstName());
        type(lastName, user.lastName());
        type(street, user.street());
        type(city, user.city());
        type(state, user.state());
        type(zipCode, user.zipCode());
        type(phone, user.phone());
        type(ssn, user.ssn());
        type(username, user.username());
        type(password, user.password());
        type(repeatedPassword, user.password());
        click(registerButton);
        wait.until(d -> isShown(logOutLink));
    }
}
