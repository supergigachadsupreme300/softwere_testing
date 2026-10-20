package vn.edu.softwaretesting.core;

import java.time.Duration;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public abstract class WebTestBase {
    private long testStartedAt;
    protected WebDriver driver;
    protected String baseUrl;

    @BeforeEach
    void openBrowser() {
        baseUrl = System.getProperty("baseUrl", "https://www.saucedemo.com");
        String browser = System.getProperty("browser", "chrome")
                .toLowerCase(Locale.ROOT);
        boolean headless = Boolean.parseBoolean(
                System.getProperty("headless", "true"));

        driver = switch (browser) {
            case "chrome" -> new ChromeDriver(chromeOptions(headless));
            case "brave" -> new ChromeDriver(braveOptions(headless));
            case "firefox" -> new FirefoxDriver(firefoxOptions(headless));
            case "edge" -> new EdgeDriver(edgeOptions(headless));
            default -> throw new IllegalArgumentException(
                    "Unsupported browser: " + browser);
        };

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
        driver.manage().window().setSize(
                new org.openqa.selenium.Dimension(1440, 900));
        testStartedAt = System.nanoTime();
    }

    @AfterEach
void closeBrowser(TestInfo testInfo) {
    try {
        if (driver != null) {
            driver.quit();
        }
    } finally {
        double elapsedSeconds =
                (System.nanoTime() - testStartedAt) / 1_000_000_000.0;

        System.out.printf(
                Locale.ROOT,
                "[TIME] %s: %.3f seconds%n",
                testInfo.getDisplayName(),
                elapsedSeconds
        );
    }
}

    private ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--disable-search-engine-choice-screen");
        return options;
    }

    private FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        return options;
    }

    private EdgeOptions edgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        return options;
    }
    private ChromeOptions braveOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        String bravePath = System.getProperty("bravePath");
        if (bravePath != null && !bravePath.isBlank()) { //calling isBlank on a null will cause exception
            options.setBinary(bravePath);
        }
        else{
            options.setBinary("C:\\Program Files\\BraveSoftware\\Brave-Browser\\Application\\brave.exe");
        }
        if (headless) {
            options.addArguments("--headless=new");
        }
        return options;
    }
}
