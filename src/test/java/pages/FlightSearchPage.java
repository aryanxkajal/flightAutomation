package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

public class FlightSearchPage {
    WebDriver driver;
    WebDriverWait wait;

    public FlightSearchPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void enterSearchValue(String searchType, String value) {
        By inputLocator;
        switch (searchType.trim().toLowerCase()) {
            case "flight number":
                inputLocator = By.xpath("//*[@id='myInputnumber']");
                break;
            case "flight name":
                inputLocator = By.xpath("//*[@id='myInputname']");
                break;
            case "flight type":
                inputLocator = By.xpath("//*[@id='myInputtype']");
                break;
            default:
                throw new IllegalArgumentException("Unsupported search type: " + searchType);
        }

        WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(inputLocator));
        input.clear();
        input.sendKeys(value);
        input.sendKeys(Keys.RETURN);
    }

    public boolean isResultTablePopulated() {
        try {
            WebElement table = driver.findElement(By.id("myTable"));
            List<WebElement> visibleRows = table.findElements(By.xpath(".//tbody/tr[not(contains(@style,'display: none'))]"));
            System.out.println("✅ Found " + visibleRows.size() + " visible result rows");
            return !visibleRows.isEmpty();
        } catch (Exception e) {
            System.out.println("❌ Failed to find visible table rows: " + e.getMessage());
            return false;
        }
    }

    public boolean isResultTableEmpty() {
        try {
            WebElement table = driver.findElement(By.id("myTable"));
            List<WebElement> visibleRows = table.findElements(By.xpath(".//tbody/tr[not(contains(@style,'display: none'))]"));
            System.out.println("ℹ️ Visible rows count: " + visibleRows.size());
            return visibleRows.isEmpty();
        } catch (Exception e) {
            System.out.println("✅ Assuming empty table (could not locate any visible rows)");
            return true;
        }
    }
    public boolean isNoFlightsMessageVisible() {
        try {
            // Option 1: Check for a visible element with message
            WebElement message = driver.findElement(By.xpath("//*[contains(text(),'No flights found')]"));
            return message.isDisplayed();
        } catch (NoSuchElementException e) {
            System.out.println("ℹ️ No 'No flights found' message is visibly shown on the page.");
            return false;
        }
    }

}

