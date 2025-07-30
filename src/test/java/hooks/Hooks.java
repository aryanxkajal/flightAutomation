package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Hooks {
    public static WebDriver driver;
    private static ThreadLocal<Scenario> currentScenario = new ThreadLocal<>();

    @Before
    public void setUp(Scenario scenario) {
    	System.setProperty("webdriver.gecko.driver", "/Users/cognizant/Desktop/selenium/geckodriver"); // ✅ Update path if needed
        driver = new FirefoxDriver();
        //System.setProperty("webdriver.chrome.driver", "E:/chromedriver-win64/chromedriver.exe");
        //driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        
        // Store current scenario for access from step definitions
        currentScenario.set(scenario);
        System.out.println("🚀 Starting scenario: " + scenario.getName());
    }

    @After
    public void tearDown(Scenario scenario) {
        // Take screenshot only if scenario failed (this is your existing logic)
        if (scenario.isFailed() && driver instanceof TakesScreenshot) {
            takeScreenshot(scenario, "scenario_failed");
        }

        // Clean up
        currentScenario.remove();
        
        // Quit driver
        if (driver != null) {
            driver.quit();
            System.out.println("🔚 Browser closed for scenario: " + scenario.getName());
        }
    }

    /**
     * Method to take screenshot immediately when a step fails
     * Call this method from your step definitions when you catch an exception
     */
    public static void takeScreenshotOnStepFailure(String stepDescription) {
        Scenario scenario = currentScenario.get();
        if (scenario != null && driver instanceof TakesScreenshot) {
            takeScreenshot(scenario, "step_failed_" + stepDescription.replaceAll("[^a-zA-Z0-9]", "_"));
        }
    }

    /**
     * Generic method to take screenshot
     */
    private static void takeScreenshot(Scenario scenario, String prefix) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);

            // Attach screenshot to Cucumber report
            scenario.attach(screenshot, "image/png", scenario.getName() + "_" + prefix);

            // Save screenshot locally
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = prefix + "_" + scenario.getName().replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";
            Path path = Paths.get("target", "screenshots", filename);
            Files.createDirectories(path.getParent());
            Files.write(path, screenshot);
            
            System.out.println("📸 Screenshot saved: " + filename);
        } catch (IOException e) {
            System.err.println("❌ Failed to save screenshot: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static WebDriver getDriver() {
        return driver;
    }

    public static Scenario getCurrentScenario() {
        return currentScenario.get();
    }
}