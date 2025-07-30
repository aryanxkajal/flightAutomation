package stepDefinitions;

import hooks.Hooks;
import io.cucumber.java.en.*;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import pages.FlightBookingPage;
import utils.ExcelReader;

import java.util.Map;

public class FlightBookingSteps {

    WebDriver driver = Hooks.driver;
    FlightBookingPage bookingPage = new FlightBookingPage(driver);
    ExcelReader reader = new ExcelReader();

    String filePath = "src/test/resources/Card.xlsx"; 
    String sheetName = "flight_booking_details";

    @Given("User is on Ticket Booking page")
    public void user_is_on_ticket_booking_page() {
        driver.get("https://webapps.tekstac.com/FlightBooking/index.html");
    }

    @When("User enters booking details from row {int}")
    public void user_enters_booking_details_from_row(Integer rowIndex) {
        try {
            Map<String, String> data = reader.getCardDetails(filePath, sheetName, rowIndex);
            
            System.out.println("📝 Entering booking details from row " + rowIndex);
            
            // Enter all fields even if some are empty
            bookingPage.enterOrigin(data.get("origin"));
            bookingPage.enterDestination(data.get("destination"));
            bookingPage.enterDate(data.get("date"));
            bookingPage.selectClass(data.get("classType"));
            bookingPage.enterName(data.get("name"));
            bookingPage.enterEmail(data.get("email"));
            bookingPage.enterPhone(data.get("phone"));
            bookingPage.enterNumberOfPassengers(data.get("passengers"));
            
            System.out.println("✅ All booking details processing completed for row " + rowIndex);
            
        } catch (Exception e) {
            System.out.println("❌ Error processing row " + rowIndex + ": " + e.getMessage());
            throw new RuntimeException("Failed to enter booking details from row " + rowIndex, e);
        }
    }
    
    @Then("Validate expected message for row {int}")
    public void validate_expected_message_for_row(Integer rowIndex) {
        try {
            Map<String, String> data = reader.getCardDetails(filePath, sheetName, rowIndex);
            String expectedMessage = data.get("expectedMessage");
            
            if (expectedMessage != null && !expectedMessage.trim().isEmpty()) {
                System.out.println("🔍 Looking for expected message: " + expectedMessage);
                
                // Wait for potential validation messages
                Thread.sleep(1000);
                
                // Check if the expected validation message appears
                boolean messageFound = checkForValidationMessage(expectedMessage);
                
                if (messageFound) {
                    System.out.println("✅ Expected validation message found: " + expectedMessage);
                    System.out.println("✅ Frontend validation is working correctly!");
                } else {
                    System.out.println("❌ Expected validation message NOT found: " + expectedMessage);
                    System.out.println("❌ DEFECT: Frontend validation is missing for invalid input!");
                    System.out.println("❌ The application accepted invalid data: " + data.get("name"));
                    
                    // This should FAIL the test - validation is required but missing
                    Assert.fail("VALIDATION DEFECT: Expected validation message '" + expectedMessage + 
                               "' was not displayed. The application incorrectly accepted invalid input: '" + 
                               data.get("name") + "'. Frontend validation needs to be implemented.");
                }
                
            } else {
                System.out.println("ℹ️ No expected validation message specified for row " + rowIndex + " - test passes");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error validating expected message for row " + rowIndex + ": " + e.getMessage());
            Assert.fail("Exception occurred while validating expected message: " + e.getMessage());
        }
    }

    private boolean checkForValidationMessage(String expectedMessage) {
        try {
            // Method 1: Check the main error container (with <br> tags)
            try {
                WebElement errorDiv = driver.findElement(By.id("errfn"));
                
                // Get HTML content (to keep <br> intact)
                String actualHtml = errorDiv.getAttribute("innerHTML").trim();

                // Normalize expected message (replace \n with <br>)
                String expectedNormalized = expectedMessage.replace("\\n", "<br>").trim();

                // Normalize actual (optional cleanup of extra whitespace)
                String actualNormalized = actualHtml.replaceAll("\\s+", " ").replaceAll(" +", " ").trim();

                if (!actualNormalized.isEmpty() && actualNormalized.contains(expectedNormalized)) {
                    return true;
                }
            } catch (Exception e) {
                // div with id errfn not found or empty
            }

            // Method 2: Check any visible element containing exact message
            try {
                WebElement messageElement = driver.findElement(
                    By.xpath("//*[contains(text(),'" + expectedMessage + "')]"));
                return messageElement.isDisplayed();
            } catch (Exception e) {
                // Message not found
            }

            // Method 3: HTML5 validation messages
            try {
                WebElement nameField = driver.findElement(By.id("name"));
                String validationMessage = (String) ((JavascriptExecutor) driver)
                    .executeScript("return arguments[0].validationMessage;", nameField);

                if (validationMessage != null && !validationMessage.isEmpty()) {
                    return validationMessage.contains(expectedMessage) || 
                           expectedMessage.toLowerCase().contains(validationMessage.toLowerCase());
                }
            } catch (Exception e) {
                // No HTML5 validation
            }

            // Method 4: Check invalid-feedback or error-message classes
            try {
                WebElement nameField = driver.findElement(By.id("name"));
                String className = nameField.getAttribute("class");

                if (className != null && (className.contains("is-invalid") || className.contains("error"))) {
                    WebElement feedbackElement = driver.findElement(
                        By.xpath("//div[contains(@class,'invalid-feedback') or contains(@class,'error-message')]"));
                    return feedbackElement.getText().contains(expectedMessage);
                }
            } catch (Exception e) {
                // No CSS validation class-based error
            }

            return false;

        } catch (Exception e) {
            System.out.println("❌ Error checking for validation message: " + e.getMessage());
            return false;
        }
    }


    // Optional: Add a method to verify the form actually submitted despite invalid data
    @Then("Verify form incorrectly accepted invalid data for row {int}")
    public void verify_form_incorrectly_accepted_invalid_data(Integer rowIndex) {
        try {
            // Check if booking confirmation appeared (which shouldn't happen with invalid data)
            boolean confirmationShown = false;
            
            try {
                WebElement confirmation = driver.findElement(By.id("bookingconfirm"));
                confirmationShown = confirmation.isDisplayed();
            } catch (Exception e) {
                // Confirmation not shown
            }
            
            try {
                WebElement ticketTable = driver.findElement(By.id("ttab"));
                confirmationShown = confirmationShown || ticketTable.isDisplayed();
            } catch (Exception e) {
                // Ticket table not shown
            }
            
            if (confirmationShown) {
                System.out.println("❌ CRITICAL DEFECT: Form accepted invalid data and showed confirmation!");
                System.out.println("❌ Invalid data was processed instead of being rejected!");
                
                Map<String, String> data = reader.getCardDetails(filePath, sheetName, rowIndex);
                Assert.fail("CRITICAL VALIDATION DEFECT: The form accepted and processed invalid data ('" + 
                           data.get("name") + "') instead of showing validation error. This is a security risk!");
            } else {
                System.out.println("✅ Good: Form did not show confirmation for invalid data");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error checking form submission result: " + e.getMessage());
        }
    }
    
    @And("clicks on Book Now")
    public void clicks_on_book_now() {
        System.out.println("Click Book Now button using sir's approach");
        
        // Add a small delay
        try {
            Thread.sleep(1000); // Give time for all form processing
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        bookingPage.submitForm();
    }

    @Then("Validate email and date for row {int}")
    public void validate_email_and_date_for_row(Integer rowIndex) {
        try {
            Map<String, String> data = reader.getCardDetails(filePath, sheetName, rowIndex);

            String email = data.get("email");
            String date = data.get("date");

            // Log validation results but don't fail test for empty fields
            if (email == null || email.trim().isEmpty()) {
                System.out.println("⚠️ Email is empty for row " + rowIndex);
            } else if (!bookingPage.isEmailValid(email)) {
                System.out.println("⚠️ Invalid email format: " + email + " (Row " + rowIndex + ")");
                Assert.assertTrue(false);
            } else {
                System.out.println("✅ Valid email: " + email + " (Row " + rowIndex + ")");
            }

            if (date == null || date.trim().isEmpty()) {
                System.out.println("⚠️ Date is empty for row " + rowIndex);
            } else if (!bookingPage.isDateInFuture(date)) {
                System.out.println("⚠️ Date is not in future: " + date + " (Row " + rowIndex + ")");
                Assert.assertTrue(false);
            } else {
                System.out.println("✅ Valid future date: " + date + " (Row " + rowIndex + ")");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error validating email and date for row " + rowIndex + ": " + e.getMessage());
        }
    }



    @And("ticket details should be displayed")
    public void ticket_details_should_be_displayed() {
        if (!bookingPage.isTicketTableDisplayed()) {
//            throw new AssertionError("Ticket details table is not displayed.");
            Assert.assertTrue(false);
        }
    }
    
    @And("User clicks on Reset button")
    public void user_clicks_on_reset_button() {
        System.out.println("🔄 User clicking on Reset button");
        bookingPage.clickResetButton();
    }

    @Then("All form fields should be empty")
    public void all_form_fields_should_be_empty() {
        System.out.println("🔍 Validating that all fields are empty after reset");
        
        // Small wait to ensure reset operation is complete
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        if (!bookingPage.areAllFieldsEmpty()) {
            throw new AssertionError("Reset button did not clear all form fields properly");
        }
        
        System.out.println("✅ Reset validation successful - all fields are empty");
    }
}