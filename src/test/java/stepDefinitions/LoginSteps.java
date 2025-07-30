package stepDefinitions;

import hooks.Hooks;
import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.LoginPage;

import java.time.Duration;

public class LoginSteps {

    WebDriver driver = Hooks.driver;
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    LoginPage loginPage = new LoginPage(driver);

    @Given("User is on Login page")
    public void user_is_on_login_page() {
        try {
            driver.get("https://webapps.tekstac.com/FlightBooking/login.html");
        } catch (Exception e) {
            System.err.println("❌ Failed to navigate to login page: " + e.getMessage());
            Hooks.takeScreenshotOnStepFailure("navigation_to_login_page");
            throw e;
        }
    }

    @When("User enters {string} and {string}")
    public void user_enters_credentials(String username, String password) {
        try {
            System.out.println("📝 Entering credentials - Username: '" + username + "', Password: '" + password + "'");
            
            loginPage.enterUsername(username);
            loginPage.enterPassword(password);

            // CAPTCHA Handling
            String captchaText = driver.findElement(By.id("code")).getText();
            loginPage.enterCaptcha(captchaText);
            loginPage.clickCaptchaButton();

            // Handle CAPTCHA alert
            try {
                Alert alert = driver.switchTo().alert();
                alert.accept();
                System.out.println("✅ CAPTCHA alert accepted");
            } catch (NoAlertPresentException ignored) {
                System.out.println("ℹ️ No CAPTCHA alert present");
            }

            loginPage.clickLogin();

            // Handle login alert
            try {
                Alert loginAlert = wait.until(ExpectedConditions.alertIsPresent());
                String alertText = loginAlert.getText();
                System.out.println("Alert text: " + alertText);
                loginAlert.accept();
            } catch (Exception e) {
                System.out.println("No alert present after login attempt");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Failed during credential entry: " + e.getMessage());
            Hooks.takeScreenshotOnStepFailure("credential_entry");
            throw e;
        }
    }
    
    @Then("User should see {string}")
    public void user_should_see(String expectedResult) {
        String actual = "Unknown";
        
        try {
            if (expectedResult.equalsIgnoreCase("Dashboard")) {
                // Wait for navigation
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                String currentUrl = driver.getCurrentUrl();
                System.out.println("🌐 Current URL: " + currentUrl);

                if (currentUrl.contains("index")) {
                    actual = "Dashboard";
                }

            } else {
                try {
                    String usernameError = "";
                    String passwordError = "";

                    // Try getting username error
                    try {
                        WebElement usernameErrEl = driver.findElement(By.id("usernameErr"));
                        usernameError = usernameErrEl.getText().trim();
                        System.out.println("🔴 Username error: " + usernameError);
                    } catch (Exception e) {
                        System.out.println("⚠️ Username error element not found.");
                    }

                    // Try getting password error
                    try {
                        WebElement passwordErrEl = driver.findElement(By.id("passwordErr"));
                        passwordError = passwordErrEl.getText().trim();
                        System.out.println("🔴 Password error: " + passwordError);
                    } catch (Exception e) {
                        System.out.println("⚠️ Password error element not found.");
                    }

                    // Match expectedResult to either error
                    if (expectedResult.equals(usernameError)) {
                        actual = usernameError;
                    } else if (expectedResult.equals(passwordError)) {
                        actual = passwordError;
                    }

                } catch (Exception e) {
                    System.out.println("❗ Unexpected error in error message capture: " + e.getMessage());
                }
            }

            System.out.println("🔍 Expected: " + expectedResult + " | Actual: " + actual);

            if (!actual.equalsIgnoreCase(expectedResult)) {
                System.err.println("❌ Validation failed - Expected: " + expectedResult + ", but got: " + actual);
                Hooks.takeScreenshotOnStepFailure("validation_failed");
                throw new AssertionError("❌ Expected: " + expectedResult + ", but got: " + actual);
            }

            System.out.println("✅ Validation successful - Expected: " + expectedResult + ", Actual: " + actual);
            
        } catch (AssertionError ae) {
            // AssertionError is already handled above with screenshot, just re-throw
            throw ae;
        } catch (Exception e) {
            System.err.println("❌ Unexpected error during validation: " + e.getMessage());
            Hooks.takeScreenshotOnStepFailure("validation_error");
            throw e;
        }
    }

    // New step definitions for forgot password functionality
    @When("User clicks on {string} link")
    public void user_clicks_on_link(String linkText) {
        try {
            System.out.println("🔄 User clicking on link: " + linkText);
            
            if (linkText.equals("Click here to reset it")) {
                loginPage.clickForgotPasswordLink();
            } else {
                throw new IllegalArgumentException("Unknown link: " + linkText);
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to click on link: " + linkText + " - " + e.getMessage());
            Hooks.takeScreenshotOnStepFailure("click_link_" + linkText.replaceAll("[^a-zA-Z0-9]", "_"));
            throw e;
        }
    }

    @Then("User should be redirected to password reset page")
    public void user_should_be_redirected_to_password_reset_page() {
        try {
            System.out.println("🔍 Validating redirection to password reset page");
            
            // Add a small wait for page navigation
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            if (!loginPage.isOnPasswordResetPage()) {
                // Log current page details for debugging
                System.out.println("Current URL: " + loginPage.getCurrentPageUrl());
                System.out.println("Current Title: " + loginPage.getCurrentPageTitle());
                
                System.err.println("❌ User was not redirected to password reset page");
                Hooks.takeScreenshotOnStepFailure("password_reset_redirection_failed");
                throw new AssertionError("User was not redirected to password reset page. " +
                                       "Current URL: " + loginPage.getCurrentPageUrl());
            }
            
            System.out.println("✅ Successfully validated redirection to password reset page");
            
        } catch (AssertionError ae) {
            // AssertionError is already handled above with screenshot, just re-throw
            throw ae;
        } catch (Exception e) {
            System.err.println("❌ Unexpected error during password reset page validation: " + e.getMessage());
            Hooks.takeScreenshotOnStepFailure("password_reset_validation_error");
            throw e;
        }
    }

    // Optional: Alternative step definition for more flexible link clicking
    @When("User clicks on forgot password link")
    public void user_clicks_on_forgot_password_link() {
        try {
            System.out.println("🔄 User clicking on forgot password link");
            loginPage.clickForgotPasswordLink();
        } catch (Exception e) {
            System.err.println("❌ Failed to click forgot password link: " + e.getMessage());
            Hooks.takeScreenshotOnStepFailure("forgot_password_link_click");
            throw e;
        }
    }
}