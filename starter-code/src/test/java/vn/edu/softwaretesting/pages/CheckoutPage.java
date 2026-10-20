package vn.edu.softwaretesting.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class CheckoutPage {
    private static final By FIRST = By.id("first-name");
    private static final By LAST = By.id("last-name");
    private static final By POSTAL = By.id("postal-code");
    private static final By CONTINUE = By.id("continue");
    private static final By CANCEL = By.id("cancel");
    private static final By ERROR = By.cssSelector("[data-test='error']");
    private static final By SUBTOTAL = By.cssSelector(".summary_subtotal_label");
    private static final By TAX = By.cssSelector(".summary_tax_label");
    private static final By TOTAL = By.cssSelector(".summary_total_label");
    private static final By FINISH = By.id("finish");
    private static final By HEADER = By.cssSelector(".complete-header");
    private static final By BADGE = By.cssSelector(".shopping_cart_badge");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(8));
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public CheckoutPage waitForInformationPage() {
        wait.until(ExpectedConditions.urlContains("checkout-step-one.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(FIRST));
        wait.until(ExpectedConditions.visibilityOfElementLocated(LAST));
        wait.until(ExpectedConditions.visibilityOfElementLocated(POSTAL));
        return this;
    }

    private void enter(By locator, String value) {
        String expectedValue = value == null ? "" : value;
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(locator));
        input.clear();
        if (!expectedValue.isEmpty()) {
            input.sendKeys(expectedValue);
        }
        wait.withMessage("Input value must update before continuing checkout")
                .until(d -> expectedValue.equals(d.findElement(locator).getDomProperty("value")));
    }

    public CheckoutPage enterFirstName(String value) {
        enter(FIRST, value);
        return this;
    }

    public CheckoutPage enterLastName(String value) {
        enter(LAST, value);
        return this;
    }

    public CheckoutPage enterPostalCode(String value) {
        enter(POSTAL, value);
        return this;
    }

    public CheckoutPage fillInformation(String first, String last, String postal) {
        return enterFirstName(first).enterLastName(last).enterPostalCode(postal);
    }

    public CheckoutPage clickContinue() {
        wait.until(ExpectedConditions.elementToBeClickable(CONTINUE)).click();
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(ERROR),
                ExpectedConditions.urlContains("checkout-step-two.html")));
        return this;
    }

    public CartPage clickCancel() {
        wait.until(ExpectedConditions.elementToBeClickable(CANCEL)).click();
        return new CartPage(driver).waitForCart();
    }

    private String text(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
    }

    public String getErrorMessage() {
        return text(ERROR);
    }

    public String getItemTotal() {
        return text(SUBTOTAL);
    }

    public String getTax() {
        return text(TAX);
    }

    public String getTotal() {
        return text(TOTAL);
    }

    public String getCompleteHeader() {
        return text(HEADER);
    }

    public CheckoutPage waitForOverview() {
        wait.until(ExpectedConditions.urlContains("checkout-step-two.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(SUBTOTAL));
        return this;
    }

    public CheckoutPage clickFinish() {
        wait.until(ExpectedConditions.elementToBeClickable(FINISH)).click();
        wait.until(ExpectedConditions.urlContains("checkout-complete.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(HEADER));
        return this;
    }

    public boolean isCartBadgeAbsent() {
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(BADGE));
        } catch (TimeoutException exception) {
            return false;
        }
    }
}
