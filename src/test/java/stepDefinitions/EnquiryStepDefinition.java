package stepDefinitions;

import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import io.cucumber.java.en.*;
import pages.EnquiryPage;
import utils.ExcelReader;
import hooks.Hooks;

public class EnquiryStepDefinition {
    WebDriver driver = hooks.Hooks.driver;
    EnquiryPage enquiryPage;
    ExcelReader reader = new ExcelReader();

    String filePath = "src/test/resources/Card.xlsx";
    String sheetName = "enquiry_module";

    @Given("User is on Enquiry page")
    public void user_is_on_enquiry_page() {
        driver.get("https://webapps.tekstac.com/FlightBooking/contactus.html");
        enquiryPage = new EnquiryPage(driver);
    }

    @When("User submits enquiry with data from row {int}")
    public void user_submits_enquiry_with_data_from_row(Integer rowIndex) {
        Map<String, String> data = reader.getCardDetails(filePath, sheetName, rowIndex);
        System.out.println("Excel Data Loaded: " + data);

        String name = data.getOrDefault("Name", "");
        String email = data.getOrDefault("Email", "");
        String phone = data.getOrDefault("Phone", "");
        String subject = data.getOrDefault("Subject", "");
        String message = data.getOrDefault("Message", "");

        
        enquiryPage.fillForm(name, email, phone, subject, message);
        enquiryPage.submitForm();
    }
    
    @Then("Validate all expected outcomes from row {int}")
    public void validate_all_expected_outcomes_from_row(Integer rowIndex) {
        Map<String, String> data = reader.getCardDetails(filePath, sheetName, rowIndex);
        
        String expectedErrorMsg = data.getOrDefault("ExpectedErrorText", "").trim();
        String expectedErrorField = data.getOrDefault("ExpectedErrorField", "").trim();
        String expectedSuccessText = data.getOrDefault("ExpectedSuccessText", "").trim();
        
        System.out.println("Row " + rowIndex + " - Expected Error Field: " + expectedErrorField);
        System.out.println("Row " + rowIndex + " - Expected Error Message: " + expectedErrorMsg);
        System.out.println("Row " + rowIndex + " - Expected Success Text: " + expectedSuccessText);
        
        // Check if we're expecting an error message
        if (!expectedErrorMsg.isEmpty() && !expectedErrorField.isEmpty()) {
            
            // First, check if there's an actual error message displayed
            String actualError = "";
            
            // Handle "All Fields" case - check name field for general validation
            if (expectedErrorField.equalsIgnoreCase("All Fields")) {
                actualError = enquiryPage.getErrorMessage("name");
            } else {
                actualError = enquiryPage.getErrorMessage(expectedErrorField);
            }
            
            System.out.println("Row " + rowIndex + " - Actual Error Message: '" + actualError + "'");
            
            if (actualError.isEmpty()) {
                // No error message found - this could be a bug
                String actualSuccessMsg = enquiryPage.getSuccessMessage();
                System.out.println("Row " + rowIndex + " - Actual Success Message: '" + actualSuccessMsg + "'");
                
                if (!actualSuccessMsg.isEmpty()) {
                    // SUCCESS MESSAGE APPEARED WHEN ERROR WAS EXPECTED - THIS IS A BUG
                    
                    // Special handling for phone validation bug
                    if (expectedErrorField.equalsIgnoreCase("phone")) {
                        System.out.println("🚨 PHONE VALIDATION BUG DETECTED! 🚨");
                        System.out.println("📞 Expected: Error message '" + expectedErrorMsg + "' for invalid phone number");
                        System.out.println("✅ Actual: Success message '" + actualSuccessMsg + "' (INCORRECT BEHAVIOR)");
                        System.out.println("🐛 The website incorrectly accepts invalid phone numbers!");
                        
                        // Take screenshot immediately when phone bug is detected
                        Hooks.takeScreenshotOnStepFailure("phone_validation_bug_row_" + rowIndex);
                        
                        Assert.fail("PHONE VALIDATION BUG DETECTED in Row " + rowIndex + ": " +
                                  "Expected error '" + expectedErrorMsg + "' for invalid phone number, " +
                                  "but got success message '" + actualSuccessMsg + "' instead. " +
                                  "The phone validation is not working correctly on the website.");
                    } else {
                        // General validation bug for other fields
                        System.out.println("🐛 VALIDATION BUG DETECTED in Row " + rowIndex + ":");
                        System.out.println("Expected: Error '" + expectedErrorMsg + "' in field '" + expectedErrorField + "'");
                        System.out.println("Actual: Success message '" + actualSuccessMsg + "'");
                        
                        // Take screenshot for general validation bugs too
                        Hooks.takeScreenshotOnStepFailure("validation_bug_" + expectedErrorField.toLowerCase() + "_row_" + rowIndex);
                        
                        Assert.fail("VALIDATION BUG DETECTED in Row " + rowIndex + ": " +
                                  "Expected error message '" + expectedErrorMsg + "' for field '" + expectedErrorField + "', " +
                                  "but got success message '" + actualSuccessMsg + "' instead. " +
                                  "This indicates validation is missing or not working properly.");
                    }
                } else {
                    // No error and no success message
                    System.out.println("❌ No message displayed when error was expected");
                    
                    // Take screenshot when no message appears but error was expected
                    Hooks.takeScreenshotOnStepFailure("no_message_displayed_" + expectedErrorField.toLowerCase() + "_row_" + rowIndex);
                    
                    Assert.fail("Row " + rowIndex + " - Expected error message '" + expectedErrorMsg + 
                              "' for field '" + expectedErrorField + "', but no message was displayed at all.");
                }
            } else {
                // Error message found - validate it matches expected
                System.out.println("✅ Error message found as expected");
                
                // Remove trailing periods and extra spaces for comparison
                String cleanActualError = actualError.trim().replaceAll("\\.$", "");
                String cleanExpectedError = expectedErrorMsg.trim().replaceAll("\\.$", "");
                
                // Handle the potential typo present in the Excel Sheet
                cleanExpectedError = cleanExpectedError.replace("charcters", "characters");
                
                Assert.assertEquals(cleanActualError, cleanExpectedError, 
                    "Error message mismatch for row " + rowIndex + " in field: " + expectedErrorField);
                System.out.println("✅ Error validation passed for row " + rowIndex);
            }
        } 
        // Check if we're expecting a success message
        else if (!expectedSuccessText.isEmpty()) {
            String actualSuccessMsg = enquiryPage.getSuccessMessage();
            System.out.println("Row " + rowIndex + " - Expected Success: '" + expectedSuccessText + "'");
            System.out.println("Row " + rowIndex + " - Actual Success: '" + actualSuccessMsg + "'");
            
            // Remove trailing periods and extra spaces for comparison
            String cleanActualSuccess = actualSuccessMsg.trim().replaceAll("\\.$", "");
            String cleanExpectedSuccess = expectedSuccessText.trim().replaceAll("\\.$", "");
            
            Assert.assertEquals(cleanActualSuccess, cleanExpectedSuccess, 
                "Success message mismatch for row " + rowIndex);
            System.out.println("✅ Success validation passed for row " + rowIndex);
        } else {
            System.out.println("⚠️ No expected outcome defined for row " + rowIndex);
        }
    }

    @Then("User should see error message {string} in field {string}")
    public void user_should_see_error_message_in_field(String expectedError, String field) {
        String actualError = enquiryPage.getErrorMessage(field);
        Assert.assertEquals(actualError.trim(), expectedError.trim(), "Mismatch in error message for field: " + field);
    }

    @Then("User should see success message {string}")
    public void user_should_see_success_message(String expectedMsg) {
        String actualMsg = enquiryPage.getSuccessMessage();
        Assert.assertEquals(actualMsg.trim(), expectedMsg.trim(), "Success message mismatch");
    }
}