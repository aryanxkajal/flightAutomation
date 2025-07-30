package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class EnquiryPage {
WebDriver driver;

@FindBy(id = "name") WebElement nameField;
@FindBy(id = "email") WebElement emailField;
@FindBy(id = "phone") WebElement phoneField;
@FindBy(id = "subject") WebElement subjectField;
@FindBy(id = "message") WebElement messageField;
@FindBy(id = "submit") WebElement submitButton;

@FindBy(id = "nameError") WebElement nameError;
@FindBy(id = "emailError") WebElement emailError;
@FindBy(id = "messageError") WebElement messageError;
@FindBy(id = "success-msg") WebElement successMsg;

public EnquiryPage(WebDriver driver) {
this.driver = driver;
PageFactory.initElements(driver, this);
}

public void fillForm(String name, String email, String phone, String subject, String message) {
nameField.clear(); nameField.sendKeys(name);
emailField.clear(); emailField.sendKeys(email);
phoneField.clear(); phoneField.sendKeys(phone);
subjectField.clear(); subjectField.sendKeys(subject);
messageField.clear(); messageField.sendKeys(message);
}

public void submitForm() {
submitButton.click();
}

public String getErrorMessage(String field) {
switch (field.toLowerCase()) {
case "name": return nameError.getText();
case "email": return emailError.getText();
case "message": return messageError.getText();
default: return "";
}
}

public String getSuccessMessage() {
    try {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOf(successMsg));  // wait until visible
        return successMsg.getText();
    } catch (Exception e) {
        // Timeout or no message appeared
        return "";
    }
}

}