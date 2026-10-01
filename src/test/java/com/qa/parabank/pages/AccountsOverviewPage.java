package com.qa.parabank.pages;

import java.math.BigDecimal;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class AccountsOverviewPage extends BasePage {

    @FindBy(css = "#accountTable tbody tr")
    private List<WebElement> rows;

    public AccountsOverviewPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        openStable("overview.htm");
        // the table is filled by an ajax call after the page renders
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#accountTable tbody tr a")));
    }

    public List<String> accountIds() {
        return rows.stream()
                .flatMap(r -> r.findElements(By.tagName("a")).stream())
                .map(a -> a.getText().trim())
                .filter(t -> !t.isEmpty())
                .toList();
    }

    /** Balance of one account, read from its row as an exact decimal. */
    public BigDecimal balanceOf(String accountId) {
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (!cells.isEmpty() && cells.get(0).getText().trim().equals(accountId)) {
                return parseMoney(cells.get(1).getText());
            }
        }
        throw new IllegalStateException("Account " + accountId + " is not on the overview");
    }

    /** The figure the page prints on its own Total row. */
    public BigDecimal totalShown() {
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (!cells.isEmpty() && cells.get(0).getText().trim().equals("Total")) {
                return parseMoney(cells.get(1).getText());
            }
        }
        throw new IllegalStateException("The overview has no Total row");
    }

    public static BigDecimal parseMoney(String text) {
        return new BigDecimal(text.replaceAll("[^0-9.\\-]", ""));
    }
}
