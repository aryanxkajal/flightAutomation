

package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class LoginPage {
    WebDriver driver;
    WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Locators
    By usernameField = By.id("username");
    By passwordField = By.id("password");
    By captchaField = By.id("captcha");
    By captchaButton = By.id("captchaBtn");
    By loginButton = By.id("login-submit");
    
    // Different possible locators for the forgot password link
    By forgotPasswordLink = By.linkText("Click here to reset it");
    By forgotPasswordLinkPartial = By.partialLinkText("Click here to reset");
    By forgotPasswordLinkXPath = By.xpath("//a[contains(text(), 'Click here to reset it')]");
    By forgotPasswordLinkCSS = By.cssSelector("a[href*='reset'], a[href*='forgot']");

    public void enterUsername(String username) {
        WebElement usernameElement = driver.findElement(usernameField);
        usernameElement.clear();
        if (username != null && !username.isEmpty()) {
            usernameElement.sendKeys(username);
        }
        System.out.println("✅ Username entered: '" + username + "'");
    }

    public void enterPassword(String password) {
        WebElement passwordElement = driver.findElement(passwordField);
        passwordElement.clear();
        if (password != null && !password.isEmpty()) {
            passwordElement.sendKeys(password);
        }
        System.out.println("✅ Password entered: '" + password + "'");
    }

    public void enterCaptcha(String captchaText) {
        driver.findElement(captchaField).sendKeys(captchaText);
        System.out.println("✅ CAPTCHA entered: " + captchaText);
    }

    public void clickCaptchaButton() {
        driver.findElement(captchaButton).click();
        System.out.println("✅ CAPTCHA button clicked");
    }

    public void clickLogin() {
        driver.findElement(loginButton).click();
        System.out.println("✅ Login button clicked");
    }

    // Method to click forgot password link
    public void clickForgotPasswordLink() {
        try {
            //All potential forgot pass links tested here
            
            WebElement forgotLink = null;
            
            // Try multiple locator strategies
            By[] locators = {
                forgotPasswordLink,
                forgotPasswordLinkPartial,
                forgotPasswordLinkXPath,
                forgotPasswordLinkCSS
            };
            
            for (By locator : locators) {
                try {
                    forgotLink = wait.until(ExpectedConditions.elementToBeClickable(locator));
                    System.out.println("✅ Found forgot password link with: " + locator.toString());
                    break;
                } catch (Exception e) {
                    System.out.println("❌ Link not found with: " + locator.toString());
                }
            }
            
            if (forgotLink != null) {
                // Log link details for debugging
                System.out.println("Link text: '" + forgotLink.getText() + "'");
                System.out.println("Link href: '" + forgotLink.getAttribute("href") + "'");
                
                // Use JavaScript click
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].scrollIntoView(true);", forgotLink);
                Thread.sleep(300);
                js.executeScript("arguments[0].click();", forgotLink);
                
                System.out.println("✅ Forgot password link clicked successfully");
                
            } else {
                throw new RuntimeException("Forgot password link not found with any locator strategy");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Failed to click forgot password link: " + e.getMessage());
            throw new RuntimeException("Unable to click forgot password link", e);
        }
    }

    // Method to check if we're on password reset page
    public boolean isOnPasswordResetPage() {
        try {
            // Wait a bit for page to load
            Thread.sleep(2000);
            
            String currentUrl = driver.getCurrentUrl();
            String pageTitle = driver.getTitle();
            String pageSource = driver.getPageSource();
            
            System.out.println("Current URL: " + currentUrl);
            System.out.println("Page Title: " + pageTitle);
            
            // Check various indicators that we're on password reset page
            boolean urlContainsReset = currentUrl.toLowerCase().contains("reset") || 
                                     currentUrl.toLowerCase().contains("forgot") ||
                                     currentUrl.toLowerCase().contains("password");
            
            boolean titleContainsReset = pageTitle.toLowerCase().contains("reset") || 
                                       pageTitle.toLowerCase().contains("forgot") ||
                                       pageTitle.toLowerCase().contains("password");
            
            boolean pageContainsResetElements = pageSource.toLowerCase().contains("reset password") ||
                                              pageSource.toLowerCase().contains("forgot password") ||
                                              pageSource.toLowerCase().contains("enter your email");
            
            boolean result = urlContainsReset || titleContainsReset || pageContainsResetElements;
            
            if (result) {
                System.out.println("✅ Successfully navigated to password reset page");
            } else {
                System.out.println("❌ Not on password reset page");
            }
            
            return result;
            
        } catch (Exception e) {
            System.out.println("❌ Error checking password reset page: " + e.getMessage());
            return false;
        }
    }

    // Method to get current page URL (useful for debugging)
    public String getCurrentPageUrl() {
        return driver.getCurrentUrl();
    }

    // Method to get current page title (useful for debugging)
    public String getCurrentPageTitle() {
        return driver.getTitle();
    }
}
