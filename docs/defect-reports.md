# Defect reports

Application under test: ParaBank (Parasoft), `parasoft/parabank:latest`, run locally.
Browser: Chrome, driven by Selenium WebDriver 4. Tested September 2026.

Every report states what was done, what the application did, what a banking
application is required to do instead, and why it matters in money terms. Each one
is covered by a test marked as a known failure and a second test that pins what
the application does today.

---

## PB-002: A bill payment larger than the balance is accepted and drives the account negative

**Severity:** High
**Area:** Bill Pay
**Status:** Reproducible on every attempt

**Steps**

1. Register a new customer. The opening account holds 515.50.
2. Go to Bill Pay, fill in any payee, and enter the balance plus 1000.00.
3. Send the payment and go to Accounts Overview.

**Result** The payment is reported complete and the balance becomes -1000.00. No
warning, no overdraft fee, no approval step.

**Expected** The payment is refused with a message naming the available balance,
or it is accepted under a declared overdraft arrangement with a recorded fee.

**Impact** Any account can be driven negative by any amount a customer types.

**Covered by** `BillPayTest.aPaymentAboveTheBalanceMustBeRefused` and
`BillPayTest.aPaymentAboveTheBalanceDrivesTheAccountNegativeToday`.

---

## PB-003: A bill payment with a negative amount pays money into the account

**Severity:** High
**Area:** Bill Pay
**Status:** Reproducible on every attempt

**Steps**

1. Register a new customer.
2. Go to Bill Pay and enter -250.00 as the amount.
3. Send the payment and go to Accounts Overview.

**Result** The payment is reported complete and the balance rises by 250.00.

**Expected** The amount field refuses anything at or below zero, and the server
refuses the same value independently of the browser.

**Impact** Money is created out of nothing, and nothing on the statement looks wrong
to the customer.

**Covered by** `BillPayTest.aNegativePaymentMustBeRefused` and
`BillPayTest.aNegativePaymentRaisesTheBalanceToday`.

---

## PB-005: A transfer with a negative amount reverses the direction of the money

**Severity:** Critical
**Area:** Transfer Funds
**Status:** Reproducible on every attempt

**Steps**

1. Register a new customer and open a second account.
2. Go to Transfer Funds, enter -25.00, choose the first account as the source and
   the second as the destination, and submit.
3. Go to Accounts Overview.

**Result** The page says "Transfer Complete! -$25.00 has been transferred". The
source account rises by 25.00 and the destination falls by 25.00. The customer has
pulled money out of an account by typing a minus sign on the other one.

**Expected** A validation message next to the amount field, and no money moved.

**Impact** The same defect is visible at the API in
[banking-api-tests](https://github.com/gamzesimit/banking-api-tests). Finding it
in the browser as well shows the user interface does not stop it either.

**Covered by** `TransferTest.aNegativeTransferMustBeRefused` and
`TransferTest.aNegativeTransferReversesTheDirectionToday`.

---

## PB-006: A transfer larger than the balance is accepted and drives the account negative

**Severity:** High
**Area:** Transfer Funds
**Status:** Reproducible on every attempt

**Steps**

1. Register a new customer and open a second account.
2. Go to Transfer Funds and enter the balance of the first account plus 1000.00.
3. Submit and go to Accounts Overview.

**Result** The transfer is reported complete. The source account lands on
-1000.00 and the destination receives the full amount.

**Expected** The transfer is refused with a message naming the available balance.

**Impact** The same overdraft gap as PB-002, on the transfer screen. Between them a
customer can spend money the bank never lent.

**Covered by** `TransferTest.aTransferAboveTheBalanceMustBeRefused` and
`TransferTest.aTransferAboveTheBalanceDrivesTheAccountNegativeToday`.
