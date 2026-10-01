package com.qa.parabank.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.qa.parabank.base.BaseTest;
import com.qa.parabank.data.ExcelReader;
import com.qa.parabank.pages.AccountsOverviewPage;
import com.qa.parabank.pages.OpenAccountPage;
import com.qa.parabank.pages.TransferFundsPage;
import java.math.BigDecimal;
import java.util.List;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Transfers between a customer's own two accounts. Every check reads both balances
 * before and after and asserts on the difference, never on an absolute balance.
 */
public class TransferTest extends BaseTest {

    private String first;
    private String second;

    /** Registers a customer and opens a second account, so there is somewhere to send money. */
    private AccountsOverviewPage customerWithTwoAccounts() {
        registerNewCustomer();
        OpenAccountPage openAccount = new OpenAccountPage(driver);
        openAccount.open();
        openAccount.openAccount("SAVINGS");

        AccountsOverviewPage overview = new AccountsOverviewPage(driver);
        overview.open();
        List<String> ids = overview.accountIds();
        // setup failures throw IllegalStateException, never AssertionError, so a test
        // marked expectedExceptions = AssertionError.class cannot pass on broken setup
        if (ids.size() != 2) {
            throw new IllegalStateException("the customer must hold exactly two accounts, found " + ids);
        }
        first = ids.get(0);
        second = ids.get(1);
        return overview;
    }

    @Test(groups = "smoke",
          description = "A transfer moves exactly the stated amount and leaves the total unchanged")
    public void aTransferMovesExactlyTheStatedAmount() {
        AccountsOverviewPage overview = customerWithTwoAccounts();
        BigDecimal fromBefore = overview.balanceOf(first);
        BigDecimal toBefore = overview.balanceOf(second);

        TransferFundsPage transfer = new TransferFundsPage(driver);
        transfer.open();
        assertTrue(transfer.transfer("25.00", first, second), "the transfer must be reported complete");

        overview.open();
        BigDecimal fromAfter = overview.balanceOf(first);
        BigDecimal toAfter = overview.balanceOf(second);
        BigDecimal amount = new BigDecimal("25.00");

        assertEquals(fromBefore.subtract(fromAfter).compareTo(amount), 0, "the source must fall by 25.00");
        assertEquals(toAfter.subtract(toBefore).compareTo(amount), 0, "the destination must rise by 25.00");
        // double entry: a transfer creates and destroys nothing
        assertEquals(fromAfter.add(toAfter).compareTo(fromBefore.add(toBefore)), 0,
                "the two balances together must not change");
    }

    @DataProvider(name = "amountsToTheCent")
    public Object[][] amountsToTheCent() {
        return ExcelReader.rows("testdata/test-data.xlsx", "amounts-to-the-cent");
    }

    @Test(dataProvider = "amountsToTheCent",
          description = "An amount with cents arrives to the cent, not rounded")
    public void anAmountIsAppliedToTheCent(String amount, String note) {
        AccountsOverviewPage overview = customerWithTwoAccounts();
        BigDecimal before = overview.balanceOf(second);

        TransferFundsPage transfer = new TransferFundsPage(driver);
        transfer.open();
        assertTrue(transfer.transfer(amount, first, second), note + ": the transfer must be reported complete");

        overview.open();
        BigDecimal arrived = overview.balanceOf(second).subtract(before);
        assertEquals(arrived.compareTo(new BigDecimal(amount)), 0,
                note + ": expected " + amount + " to arrive, " + arrived + " arrived");
    }

    /** PB-006: a transfer above the balance is accepted and drives the source account negative. */
    @Test(description = "PB-006 A transfer above the available balance must be refused",
          expectedExceptions = AssertionError.class)
    public void aTransferAboveTheBalanceMustBeRefused() {
        AccountsOverviewPage overview = customerWithTwoAccounts();
        BigDecimal tooMuch = overview.balanceOf(first).add(new BigDecimal("1000.00"));

        TransferFundsPage transfer = new TransferFundsPage(driver);
        transfer.open();
        assertFalse(transfer.transfer(tooMuch.toPlainString(), first, second),
                "a transfer above the available balance must not be reported complete");
    }

    /** Pins what PB-006 does today, so a fix shows up as a change rather than passing silently. */
    @Test(description = "PB-006 today: a transfer above the balance leaves the source account at -1000.00")
    public void aTransferAboveTheBalanceDrivesTheAccountNegativeToday() {
        AccountsOverviewPage overview = customerWithTwoAccounts();
        BigDecimal tooMuch = overview.balanceOf(first).add(new BigDecimal("1000.00"));

        TransferFundsPage transfer = new TransferFundsPage(driver);
        transfer.open();
        assertTrue(transfer.transfer(tooMuch.toPlainString(), first, second),
                "with the defect present the transfer is reported complete");

        overview.open();
        assertEquals(overview.balanceOf(first).compareTo(new BigDecimal("-1000.00")), 0,
                "with the defect present the source lands on -1000.00, it is " + overview.balanceOf(first));
    }

    /** PB-005: a negative amount is accepted and moves the money in the opposite direction. */
    @Test(description = "PB-005 A transfer with a negative amount must not be reported complete",
          expectedExceptions = AssertionError.class)
    public void aNegativeTransferMustBeRefused() {
        customerWithTwoAccounts();
        TransferFundsPage transfer = new TransferFundsPage(driver);
        transfer.open();
        assertFalse(transfer.transfer("-25.00", first, second),
                "a negative transfer must not be reported complete");
    }

    /** Pins what PB-005 does today: the source gains and the destination loses. */
    @Test(description = "PB-005 today: a negative transfer reverses the direction of the money")
    public void aNegativeTransferReversesTheDirectionToday() {
        AccountsOverviewPage overview = customerWithTwoAccounts();
        BigDecimal fromBefore = overview.balanceOf(first);
        BigDecimal toBefore = overview.balanceOf(second);

        TransferFundsPage transfer = new TransferFundsPage(driver);
        transfer.open();
        assertTrue(transfer.transfer("-25.00", first, second),
                "with the defect present the transfer is reported complete");

        overview.open();
        BigDecimal amount = new BigDecimal("25.00");
        assertEquals(overview.balanceOf(first).subtract(fromBefore).compareTo(amount), 0,
                "with the defect present the source rises by 25.00");
        assertEquals(toBefore.subtract(overview.balanceOf(second)).compareTo(amount), 0,
                "with the defect present the destination falls by 25.00");
    }

    /**
     * A zero transfer is recorded rather than refused. That is a product decision more
     * than a defect, so it is pinned here and raised as a question in the README.
     */
    @Test(description = "A zero transfer is recorded and leaves both balances unchanged")
    public void aZeroTransferLeavesBothBalancesUnchanged() {
        AccountsOverviewPage overview = customerWithTwoAccounts();
        BigDecimal fromBefore = overview.balanceOf(first);
        BigDecimal toBefore = overview.balanceOf(second);

        TransferFundsPage transfer = new TransferFundsPage(driver);
        transfer.open();
        transfer.transfer("0.00", first, second);

        overview.open();
        assertEquals(overview.balanceOf(first).compareTo(fromBefore), 0, "the source must not change");
        assertEquals(overview.balanceOf(second).compareTo(toBefore), 0, "the destination must not change");
    }

    @Test(description = "The overview total equals the sum of the account balances")
    public void theOverviewTotalEqualsTheSumOfBalances() {
        AccountsOverviewPage overview = customerWithTwoAccounts();
        BigDecimal sum = overview.balanceOf(first).add(overview.balanceOf(second));
        assertEquals(overview.totalShown().compareTo(sum), 0,
                "the Total row must equal the sum of the rows, " + sum);
    }
}
