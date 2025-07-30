package stepDefinitions;

import hooks.Hooks;
import io.cucumber.java.en.*;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import pages.FlightSearchPage;
import utils.ExcelReader;

import java.util.Map;

public class FlightSearchSteps {

    WebDriver driver = Hooks.getDriver();
    FlightSearchPage searchPage = new FlightSearchPage(driver);
    ExcelReader reader = new ExcelReader();

    String filePath = "src/test/resources/Card.xlsx";
    String sheetName = "flight_search_details";
    Map<String, String> data;

    @Given("User is on Flight Search page")
    public void user_is_on_flight_search_page() {
        driver.get("https://webapps.tekstac.com/FlightBooking/search.html");
    }

    @When("User searches for flight using row {int}")
    public void user_searches_for_flight_using_row(Integer rowIndex) {
        data = reader.getCardDetails(filePath, sheetName, rowIndex);
        String type = data.get("searchType");
        String value = data.get("inputValue");

        System.out.println("🔍 Searching with: " + type + " = " + value);
        searchPage.enterSearchValue(type, value);
    }

   
    @Then("Validate search result for row {int}")
    public void validate_search_result_for_row(Integer rowIndex) {
        String expected = data.get("expectedMessage").toLowerCase().trim();

        System.out.println("🔎 Validating table result behavior for: " + expected);

        boolean actualBehavior;

        if (expected.contains("no flights found")) {
            // 🔍 Explicitly check for a visible message
            actualBehavior = searchPage.isNoFlightsMessageVisible();
        } else {
            actualBehavior = searchPage.isResultTablePopulated();
        }

        Assert.assertTrue("❌ Table/message behavior did not match expectation for: " + expected, actualBehavior);
    }

}

