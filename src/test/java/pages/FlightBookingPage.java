package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FlightBookingPage {
    WebDriver driver;
    WebDriverWait wait;

    public FlightBookingPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Locators
    By nameField = By.id("name");
    By emailField = By.id("email");
    By phoneField = By.id("phone");
    By originField = By.id("travelFrom");
    By destinationField = By.id("travelTo");
    By dateField = By.id("departure");
    By submitButton = By.name("book-now");
    By classDropdown = By.id("selectclass");
    By passengerCountField = By.id("ticket-class-count");
    By confirmationMessage = By.id("bookingconfirm");
    By ticketTable = By.id("ttab");
    By resetButton = By.id("reset-now");
    By errorContainer = By.id("errfn");
    By generalErrorMessage = By.xpath("//*[contains(text(),'Name should only contain letters')]");
    By nameErrorMessage = By.xpath("//div[@id='errfn' or @class='error-message' or contains(@class,'error')]");
    By allErrorMessages = By.xpath("//*[contains(@style,'color:red') or contains(@class,'error') or contains(@class,'invalid')]");

    // Utility: Enter text and trigger full JS event sequence
    private void enterTextWithEvents(By locator, String value) {
        try {
            WebElement field = driver.findElement(locator);
            field.clear();
            
            // Only enter text if value is not null or empty
            if (value != null && !value.trim().isEmpty()) {
                field.sendKeys(value);
                System.out.println("✅ Entered value: '" + value + "' in field: " + locator.toString());
            } else {
                System.out.println("⚠️ Empty value provided for field: " + locator.toString() + " - leaving field empty");
            }

            // Trigger JS events regardless of whether we entered text
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                "arguments[0].dispatchEvent(new Event('focus', { bubbles: true }));" +
                "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));" +
                "arguments[0].dispatchEvent(new Event('blur', { bubbles: true }));",
                field
            );

            field.sendKeys(Keys.TAB);

            Thread.sleep(300);
            
        } catch (Exception e) {
            System.out.println("❌ Error entering text in field " + locator.toString() + ": " + e.getMessage());
            // Don't throw exception - continue with other fields
        }
    }

    // Input Actions
    public void enterName(String name) {
        enterTextWithEvents(nameField, name);
    }

    public void enterEmail(String email) {
        enterTextWithEvents(emailField, email);
    }

    public void enterPhone(String phone) {
        enterTextWithEvents(phoneField, phone);
    }

    public void enterOrigin(String origin) {
        enterTextWithEvents(originField, origin);
    }

    public void enterDestination(String destination) {
        enterTextWithEvents(destinationField, destination);
    }

    public void enterDate(String date) {
        enterTextWithEvents(dateField, date);
    }

    public void enterNumberOfPassengers(String count) {
        enterTextWithEvents(passengerCountField, count);
    }

    public void selectClass(String classType) {
        try {
            if (classType != null && !classType.trim().isEmpty()) {
                Select dropdown = new Select(driver.findElement(classDropdown));
                dropdown.selectByVisibleText(classType);
                System.out.println("✅ Selected class: '" + classType + "'");
            } else {
                System.out.println("⚠️ Empty class type provided - leaving dropdown as default");
            }
        } catch (Exception e) {
            System.out.println("❌ Error selecting class '" + classType + "': " + e.getMessage());
            // Don't throw exception - continue with other fields
        }
    }

    // Submit form by triggering JS function directly
    public void submitForm() {
        try {
            System.out.println("🔄 Using Sir's approach...");
            
            // Wait for the button to be present
            WebElement bookNowButton = wait.until(ExpectedConditions.presenceOfElementLocated(submitButton));
            
            // Scroll the button into view
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView(true);", bookNowButton);
            
            // Small pause to ensure scroll is complete
            Thread.sleep(500);
            
            // Click using JavaScript)
            js.executeScript("arguments[0].click();", bookNowButton);
            System.out.println("✅ JavaScript click executed");
            
            // Optional: Also try regular Selenium click as backup
            try {
                WebElement formClick = driver.findElement(By.xpath("//button[@id='book-now']"));
                formClick.click();
                System.out.println("✅ Regular Selenium click also executed");
            } catch (Exception e) {
                System.out.println("ℹ️ Regular click failed, but JS click should have worked: " + e.getMessage());
            }
            
            // Wait for confirmation or ticket table to appear
            try {
                wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(confirmationMessage),
                    ExpectedConditions.visibilityOfElementLocated(ticketTable)
                ));
                System.out.println("✅ Form submission successful - results appeared");
            } catch (Exception e) {
                System.out.println("⚠️ Results didn't appear immediately, but click was executed");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Form submission failed: " + e.getMessage());
            throw new RuntimeException("Unable to submit form", e);
        }
    }

    // Validation helpers
    public boolean isEmailValid(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        return email != null && email.matches(emailRegex);
    }


    public boolean isDateInFuture(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate inputDate = LocalDate.parse(dateStr, formatter);
            return inputDate.isAfter(LocalDate.now());
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format: " + dateStr);
            return false;
        }
    }

    public String getConfirmationMessage() {
        return driver.findElement(confirmationMessage).getText().trim();
    }

    public boolean isTicketTableDisplayed() {
        return driver.findElement(ticketTable).isDisplayed();
    }
    
    public void clickResetButton() {
        try {
            System.out.println("🔄 Clicking Reset button...");
            
            // Wait for the reset button to be present and clickable
            WebElement resetBtn = wait.until(ExpectedConditions.elementToBeClickable(resetButton));
            
            // Use sir's proven approach: scroll into view and click with JavaScript
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView(true);", resetBtn);
            Thread.sleep(300);
            js.executeScript("arguments[0].click();", resetBtn);
            
            System.out.println("✅ Reset button clicked successfully");
            
            // Small wait for reset to complete
            Thread.sleep(500);
            
        } catch (Exception e) {
            System.out.println("❌ Failed to click reset button: " + e.getMessage());
            throw new RuntimeException("Unable to click reset button", e);
        }
    }

    public boolean areAllFieldsEmpty() {
        try {
            
            // Check all input fields
            String name = driver.findElement(nameField).getAttribute("value");
            String email = driver.findElement(emailField).getAttribute("value");
            String phone = driver.findElement(phoneField).getAttribute("value");
            String origin = driver.findElement(originField).getAttribute("value");
            String destination = driver.findElement(destinationField).getAttribute("value");
            String date = driver.findElement(dateField).getAttribute("value");
            String passengers = driver.findElement(passengerCountField).getAttribute("value");
            
            // Check dropdown (class selection)
            Select classSelect = new Select(driver.findElement(classDropdown));
            String selectedClass = classSelect.getFirstSelectedOption().getText();
            
            
            
            // Check if all text fields are empty (or contain only whitespace)
            boolean allEmpty = (name == null || name.trim().isEmpty()) &&
                              (email == null || email.trim().isEmpty()) &&
                              (phone == null || phone.trim().isEmpty()) &&
                              (origin == null || origin.trim().isEmpty()) &&
                              (destination == null || destination.trim().isEmpty()) &&
                              (date == null || date.trim().isEmpty()) &&
                              (("0".equals(passengers.trim()) || passengers.trim().isEmpty()));
            
            // Check if dropdown is reset to default (adjust based on your default option)
            boolean dropdownReset = selectedClass.equals("") || 
                                   selectedClass.equals("Select Class") || 
                                   selectedClass.contains("Select");
            
            boolean result = allEmpty && dropdownReset;
            
            if (result) {
                System.out.println("✅ All fields are properly reset");
            } else {
                System.out.println("❌ Some fields are not reset properly");
            }
            
            return result;
            
        } catch (Exception e) {
            System.out.println("❌ Error checking field values: " + e.getMessage());
            return false;
        }
    }
    
    // Add this method to detect error messages:
    	public boolean isErrorMessageDisplayed(String expectedMessage) {
    	    try {
    	        System.out.println("🔍 Checking for error message: " + expectedMessage);
    	        
    	        // Wait a bit for validation to trigger
    	        Thread.sleep(1500);
    	        
    	        // Method 1: Check the specific error container
    	        try {
    	            WebElement errorDiv = driver.findElement(errorContainer);
    	            String errorText = errorDiv.getText().trim();
    	            if (!errorText.isEmpty()) {
    	                System.out.println("📋 Error container text: '" + errorText + "'");
    	                if (errorText.contains(expectedMessage)) {
    	                    System.out.println("✅ Found expected message in error container");
    	                    return true;
    	                }
    	            }
    	        } catch (Exception e) {
    	            System.out.println("ℹ️ Error container not found or empty");
    	        }
    	        
    	        // Method 2: Look for specific error message by text
    	        try {
    	            WebElement specificError = driver.findElement(By.xpath("//*[contains(text(),'" + expectedMessage + "')]"));
    	            if (specificError.isDisplayed()) {
    	                System.out.println("✅ Found specific error message element");
    	                return true;
    	            }
    	        } catch (Exception e) {
    	            System.out.println("ℹ️ Specific error message element not found");
    	        }
    	        
    	        // Method 3: Check all error-style elements
    	        try {
    	            java.util.List<WebElement> errorElements = driver.findElements(allErrorMessages);
    	            System.out.println("📋 Found " + errorElements.size() + " potential error elements");
    	            
    	            for (WebElement element : errorElements) {
    	                String text = element.getText().trim();
    	                if (!text.isEmpty()) {
    	                    System.out.println("📋 Error element text: '" + text + "'");
    	                    if (text.contains(expectedMessage)) {
    	                        System.out.println("✅ Found expected message in error element");
    	                        return true;
    	                    }
    	                }
    	            }
    	        } catch (Exception e) {
    	            System.out.println("ℹ️ No error-style elements found");
    	        }
    	        
    	        // Method 4: Check page source as last resort
    	        String pageSource = driver.getPageSource();
    	        if (pageSource.contains(expectedMessage)) {
    	            System.out.println("⚠️ Found message in page source but not in visible elements");
    	            return true;
    	        }
    	        
    	        // Method 5: Check for HTML5 validation messages
    	        try {
    	            WebElement nameField = driver.findElement(By.id("name"));
    	            String validationMessage = (String) ((JavascriptExecutor) driver)
    	                .executeScript("return arguments[0].validationMessage;", nameField);
    	            
    	            if (validationMessage != null && !validationMessage.isEmpty()) {
    	                System.out.println("📋 HTML5 validation message: '" + validationMessage + "'");
    	                if (validationMessage.contains(expectedMessage) || 
    	                    expectedMessage.contains(validationMessage)) {
    	                    System.out.println("✅ Found expected message in HTML5 validation");
    	                    return true;
    	                }
    	            }
    	        } catch (Exception e) {
    	            System.out.println("ℹ️ No HTML5 validation message");
    	        }
    	        
    	        System.out.println("❌ Expected message not found anywhere: " + expectedMessage);
    	        return false;
    	        
    	    } catch (Exception e) {
    	        System.out.println("❌ Error while checking for validation message: " + e.getMessage());
    	        return false;
    	    }
    	}

}
