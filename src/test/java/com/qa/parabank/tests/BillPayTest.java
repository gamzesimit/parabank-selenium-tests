package com.qa.parabank.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.qa.parabank.base.BaseTest;
import com.qa.parabank.pages.AccountsOverviewPage;
import com.qa.parabank.pages.BillPayPage;
import java.math.BigDecimal;
import org.testng.annotations.Test;

/**
 * Bill payments from a new customer's opening account. Two rules this build breaks
 * are marked {@code expectedExceptions = AssertionError.class} and carry the defect
 * id, so the suite stays green while the defect stays visible. When a defect is
 * fixed, the mark comes off and the test becomes a regression guard.
 */
public class BillPayTest extends BaseTest {

    private String account;

    private AccountsOverviewPage customerWithOneAccount() {
        registerNewCustomer();
        AccountsOverviewPage overview = new AccountsOverviewPage(driver);
        overview.open();
        account = overview.accountIds().get(0);
        return overview;
    }

    private BigDecimal payAndReadBalance(AccountsOverviewPage overview, String amount, boolean mustComplete) {
        BillPayPage billPay = new BillPayPage(driver);
        billPay.open();
        boolean complete = billPay.pay(amount, account);
        if (mustComplete) {
            assertTrue(complete, "the payment of " + amount + " must be reported complete");
        } else {
            assertFalse(complete, "a payment of " + amount + " must not be reported complete");
        }
        overview.open();
        return overview.balanceOf(account);
    }

    @Test(groups = "smoke", description = "A payment inside the balance leaves the account by exactly that amount")
    public void aPaymentInsideTheBalanceLeavesTheAccountExactly() {
        AccountsOverviewPage overview = customerWithOneAccount();
        BigDecimal before = overview.balanceOf(account);

        BigDecimal after = payAndReadBalance(overview, "40.25", true);
        assertEquals(before.subtract(after).compareTo(new BigDecimal("40.25")), 0,
                "the account must fall by exactly 40.25");
    }

    /** PB-002: a payment above the balance is accepted and drives the account negative. */
    @Test(description = "PB-002 A payment above the available balance must be refused",
          expectedExceptions = AssertionError.class)
    public void aPaymentAboveTheBalanceMustBeRefused() {
        AccountsOverviewPage overview = customerWithOneAccount();
        BigDecimal tooMuch = overview.balanceOf(account).add(new BigDecimal("1000.00"));

        BigDecimal after = payAndReadBalance(overview, tooMuch.toPlainString(), false);
        assertTrue(after.signum() >= 0, "the balance must never go below zero, it is " + after);
    }

    /** Pins what PB-002 does today, so a fix shows up as a change rather than passing silently. */
    @Test(description = "PB-002 today: a payment above the balance drives the account below zero")
    public void aPaymentAboveTheBalanceDrivesTheAccountNegativeToday() {
        AccountsOverviewPage overview = customerWithOneAccount();
        BigDecimal before = overview.balanceOf(account);
        BigDecimal tooMuch = before.add(new BigDecimal("1000.00"));

        BigDecimal after = payAndReadBalance(overview, tooMuch.toPlainString(), true);
        assertEquals(after.compareTo(new BigDecimal("-1000.00")), 0,
                "with the defect present the balance lands on -1000.00, it is " + after);
    }

    /** PB-003: a negative payment is accepted and pays money into the account. */
    @Test(description = "PB-003 A payment with a negative amount must be refused",
          expectedExceptions = AssertionError.class)
    public void aNegativePaymentMustBeRefused() {
        AccountsOverviewPage overview = customerWithOneAccount();
        BigDecimal before = overview.balanceOf(account);

        BigDecimal after = payAndReadBalance(overview, "-250.00", false);
        assertEquals(after.compareTo(before), 0, "a refused payment must leave the balance alone");
    }

    /** Pins what PB-003 does today. */
    @Test(description = "PB-003 today: a negative payment raises the balance by that amount")
    public void aNegativePaymentRaisesTheBalanceToday() {
        AccountsOverviewPage overview = customerWithOneAccount();
        BigDecimal before = overview.balanceOf(account);

        BigDecimal after = payAndReadBalance(overview, "-250.00", true);
        assertEquals(after.subtract(before).compareTo(new BigDecimal("250.00")), 0,
                "with the defect present the balance rises by 250.00");
    }
}
