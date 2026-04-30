# Demo Web Shop — Selenium E2E (Java + TestNG)

End-to-end UI automation for [Tricentis Demo Web Shop](https://demowebshop.tricentis.com): login, search, cart, multi-step checkout, order confirmation, and logout.

## Submission checklist (mapping)

| Requirement | Where / how |
|-------------|-------------|
| Java + Selenium + TestNG | Maven project, `pom.xml`, `PurchaseFlowTest` |
| Page Object Model | `src/main/java/com/tricentis/demowebshop/pages/` |
| WebDriverManager | `WebDriverFactory` (Bonigarcia) |
| Config for URL & credentials | `src/test/resources/config.properties` |
| Assertions | Login, search results, add-to-cart, cart line vs product, order message, logout |
| XPath / CSS locators | Page classes |
| Logging | SLF4J + Log4j2 (`log4j2.xml`): console + `target/e2e-detailed.log` (DEBUG for `com.tricentis`) |
| Test reports | TestNG/Surefire under `target/surefire-reports/`; ExtentReports: `target/extent-report.html` (steps via `ExtentReporterListener.step`) |
| Gen AI documentation | [`al-docs/GEN_AI_AND_PROMPTS.md`](al-docs/GEN_AI_AND_PROMPTS.md) |

## Evaluation criteria (rubric mapping)

Use this table to see how common grading rubrics map to **concrete artifacts** in the repository.

| Criterion | What reviewers should look at |
|-----------|-------------------------------|
| **Code quality, readability, modularity** | Package layout (`pages/`, `utils/`, `tests/`, `listeners/`); `BasePage` + `PageFactory`; small methods on pages (e.g. `CheckoutPage` steps); `ConfigReader` and `WebDriverFactory` as single-purpose utilities; SLF4J logging with sensible levels. |
| **Correctness of the end-to-end flow** | `PurchaseFlowTest`: open site → log in → search → open in-stock product → add to cart → cart assertions → checkout wizard → order confirmation assertion → logout. Data-driven search term via `config.properties` / `@DataProvider`. |
| **Best practices (POM, locators, waits)** | **POM:** one class per area (`HomePage`, `LoginPage`, `CheckoutPage`, …). **Locators:** shared selectors in `pages/locators/DemoWebShopLocators` (nested `Header`, `Auth`, `SearchResults`, `Product`, `Cart`) so pages do not scatter duplicate strings. **Waits:** `WaitHelper` centralizes explicit `WebDriverWait` (visibility, clickability, URL). **Driver:** WebDriverManager in `WebDriverFactory`. **Optional:** `ScreenshotHelper` + failure attachment in `ExtentReporterListener`. |
| **Gen AI tools to enhance or accelerate development** | [`al-docs/GEN_AI_AND_PROMPTS.md`](al-docs/GEN_AI_AND_PROMPTS.md) — tools used, example prompts, and responsible-use notes. |
| **Assertions and test reports** | **Assertions:** `PurchaseFlowTest` uses named helpers (`assertLogoutLinkVisible`, `assertSearchHasResults`, `assertCartListsProduct`, …) so failures include **phase context** and **current URL**. **Reports:** TestNG/Surefire HTML and XML under `target/surefire-reports/`; Extent HTML at `target/extent-report.html` with steps from `ExtentReporterListener.step`. Failures: PNG under `target/screenshots/` when capture succeeds, embedded in Extent on failure. |
| **Documentation and setup instructions** | This file: JDK, `JAVA_HOME`, `config.properties` keys, `mvnw.cmd` / `mvn test`, report paths, troubleshooting. Root [`README.md`](../README.md) points here. |

## Prerequisites

- **JDK 17+** (project uses `maven.compiler.release` 17; JDK 21 works)
- **Chrome** installed (WebDriverManager resolves matching ChromeDriver)
- Network access to `https://demowebshop.tricentis.com`

## Setup

1. Clone or unzip this repository so this folder is your project root (or open `demowebshop-e2e` in your IDE).

2. Set **JAVA_HOME** to your JDK (Windows example):

   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
   ```

3. Edit **`src/test/resources/config.properties`**:

   - `base.url` — shop URL (default is correct for the assignment)
   - `user.email` / `user.password` — your demo account
   - `browser.headless` — `true` for CI/headless, `false` to watch the browser
   - Billing fields — used if checkout asks for a new address

## Run tests

From this directory (`demowebshop-e2e`):

```powershell
.\mvnw.cmd test
```

With Maven on `PATH`:

```powershell
mvn test
```

Tests are driven by `src/test/resources/testng.xml` (Surefire is configured in `pom.xml`).

## Reports (after a run)

| Output | Path |
|--------|------|
| TestNG HTML report | `target/surefire-reports/index.html` |
| Emailable summary | `target/surefire-reports/emailable-report.html` |
| ExtentReports (HTML, after suite) | `target/extent-report.html` — open in a browser; includes per-step entries from the test; failed tests may embed a screenshot |
| Failure screenshots (PNG) | `target/screenshots/` — written on failure when the WebDriver session is available |
| Detailed log file | `target/e2e-detailed.log` — same messages as console, plus DEBUG from `com.tricentis` |
| Surefire XML | `target/surefire-reports/testng-results.xml` |

Run Maven from the **`demowebshop-e2e`** directory so `target/` paths resolve correctly. The Extent report is **flushed when the TestNG suite finishes** (`onFinish`).

## Project layout (abbreviated)

```text
src/main/java/.../pages/     # Page Objects (PageFactory where used)
src/main/java/.../locators/  # DemoWebShopLocators — shared By / CSS / XPath strings
src/main/java/.../utils/    # ConfigReader, WebDriverFactory, WaitHelper, ScreenshotHelper
src/test/java/.../tests/    # TestNG tests
src/test/java/.../listeners/# ExtentReports listener
src/test/resources/         # config.properties, testng.xml, log4j2.xml
al-docs/                    # Gen AI prompts & tools (submission)
```

## Troubleshooting

- **Login fails**: Verify email/password in `config.properties` and that the account exists on the demo site.
- **CDP version warnings** in the console: harmless for basic WebDriver usage; optional `selenium-devtools-v*` dependency can align with your Chrome major version.
- **`mvn` not found**: Use `.\mvnw.cmd` from the project root (Maven Wrapper).

## License / demo use

This project targets a **public demo application** for learning and evaluation only.
