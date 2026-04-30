# Generative AI usage — Demo Web Shop E2E

This document satisfies the submission requirement to record **example prompts** and **tools** used while building and maintaining the Selenium automation suite.

## Tools leveraged

| Tool | How it was used |
|------|-----------------|
| **Cursor (Composer / agent)** | End-to-end implementation of the Maven project: Page Object Model, TestNG suite, WebDriverManager, `config.properties`, Log4j2, ExtentReports listener, checkout flow fixes, and compilation/test runs. |
| **ChatGPT / similar LLMs** *(optional)* | Useful for quick lookups on Selenium APIs, TestNG annotations, or nopCommerce/Demo Web Shop locator patterns when not coding in the IDE. |
| **GitHub Copilot** *(optional)* | Inline completions in the IDE for boilerplate (e.g., imports, small refactorings). |

> Replace or extend the “optional” rows with whatever you actually used (e.g., only Cursor).

## Example prompts (representative)

Below are **paraphrased examples** of prompts that match the kind of work done on this project. Your exact wording may differ.

1. **Scaffold the project**  
   *“Design a Java Maven project with Selenium 4, TestNG, WebDriverManager, Page Object Model, `config.properties` for URL and credentials, and a TestNG XML suite.”*

2. **Implement the user journey**  
   *“Automate login, search for ‘computer’, add a product to cart, open cart, checkout with dummy billing/shipping, choose shipping and payment (COD), confirm order, assert success message, then logout.”*

3. **Stabilize locators and waits**  
   *“Use explicit waits in a helper class; use CSS and XPath for Demo Web Shop; skip or configure ‘Build your own’ / configurable products so Add to cart succeeds.”*

4. **Fix failures**  
   *“The test times out on `#bar-notification.success` after Add to cart — handle product attributes (dropdowns/radios) before clicking Add to cart.”*  
   *“Order completion assertion fails — broaden selectors for the completed page and confirm button.”*

5. **Reporting**  
   *“Add TestNG default reports via Surefire and an ExtentReports HTML report under `target/`.”*

6. **Run configuration**  
   *“Support headless and non-headless Chrome via `browser.headless` in properties; document JAVA_HOME and `mvnw` usage on Windows.”*

## Responsible use

- Generated code was **reviewed, compiled, and executed** against the live demo site.  
- **Credentials** are externalized in `config.properties` and should not be committed with real secrets for production systems.  
- Assertions and waits were adjusted based on **actual test runs**, not assumed HTML alone.

## Alignment with grading rubrics

Reviewers evaluating **code quality**, **POM/waits**, **assertions/reports**, or **documentation** can cross-check the repository using the **Evaluation criteria (rubric mapping)** table in the project [`README.md`](../README.md) at the `demowebshop-e2e` root.
