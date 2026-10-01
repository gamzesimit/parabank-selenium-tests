# ParaBank Selenium tests

[![tests](https://github.com/gamzesimit/parabank-selenium-tests/actions/workflows/tests.yml/badge.svg)](https://github.com/gamzesimit/parabank-selenium-tests/actions/workflows/tests.yml)

Selenium WebDriver checks for a retail online banking application, written in Java
with TestNG and Maven, on the Page Object Model with PageFactory. Test data for the
data-driven cases is read from an Excel sheet with Apache POI.

Four defects were found through the browser. Two of them are on the transfer screen:
a negative amount is accepted and moves the money backwards, and a transfer larger
than the balance drives the account to -1000.00.

Reports: [docs/defect-reports.md](docs/defect-reports.md)

## Running it

```bash
docker compose up -d      # ParaBank on http://localhost:8081
mvn test
```

Chrome runs headless by default. To watch it, or to point at another environment:

```bash
HEADLESS=false mvn test
BASE_URL=https://host/parabank/ mvn test
SELENIUM_REMOTE_URL=http://localhost:4444/wd/hub mvn test   # a Selenium Grid
```

## What is covered

| Class | Area |
|---|---|
| `RegistrationTest` | A new customer is welcomed by name, signs out and back in |
| `LoginTest` | Wrong password, unknown customer and empty fields, read from Excel |
| `TransferTest` | Amount moved exactly, cents read from Excel, the overview total, zero, negative and overdrawn transfers |
| `BillPayTest` | A payment leaves the account exactly, overdrawn and negative payments |

Twenty tests. A rule this build breaks is marked
`expectedExceptions = AssertionError.class` and carries the defect id, so the suite
stays green while the defect stays visible. Beside each one sits a test that pins
the present behaviour, so a fix turns into a failing test instead of passing
unnoticed.

## How it is built

```
src/test/java/com/qa/parabank/
  base/     Config, DriverFactory (local Chrome or Grid), BaseTest (browser per test, screenshot on failure)
  pages/    one page object per screen, elements declared with @FindBy, all extending BasePage
  data/     ExcelReader (Apache POI), TestUser (unique customer per run)
  tests/    test classes grouped by area
src/test/resources/
  testng.xml               suite definition
  testdata/test-data.xlsx  sign-in cases and amounts for the data-driven tests
docs/defect-reports.md     the four defects, with steps and impact
```

Every test registers its own customer, so no test depends on data another test
left behind. Money is compared as `BigDecimal` with `compareTo`, and every check
reads the balance before and after and asserts on the difference.

`BasePage.openStable` retries a page while the server answers with its internal
error page. The hosted demo does that intermittently; the retry is written down
rather than hidden, so nobody mistakes it for flakiness in the tests.

## Continuous integration

`.github/workflows/tests.yml` starts ParaBank as a service, waits for it, runs the
suite in headless Chrome on every push and pull request, and keeps the TestNG
reports and any failure screenshots for fourteen days.

## Open question

A transfer of 0.00 is reported complete and changes nothing. That reads as a
product decision rather than a defect, so it is pinned by
`aZeroTransferLeavesBothBalancesUnchanged` and raised here instead of filed.
