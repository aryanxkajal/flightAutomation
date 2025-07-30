package runners;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources",
    glue = {"stepDefinitions", "hooks"},
    plugin = {"pretty", "html:target/cucumber-reports.html"},
    tags = "@UserLogin or @FlightBooking or @EnquiryPage or @FlightSearch",
    
    monochrome = true
    
)
public class TestRunner {
}
