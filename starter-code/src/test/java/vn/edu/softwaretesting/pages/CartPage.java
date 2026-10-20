package vn.edu.softwaretesting.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class CartPage {
    private static final By CART_LINK = By.cssSelector(".shopping_cart_link");
    private static final By CART_LIST = By.cssSelector(".cart_list");
    private static final By ITEMS = By.cssSelector(".cart_item");
    private static final By ITEM_NAME = By.cssSelector(".inventory_item_name");
    private static final By REMOVE = By.cssSelector("button");
    private static final By BADGE = By.cssSelector(".shopping_cart_badge");
    private static final By CHECKOUT = By.id("checkout");
    private static final By CONTINUE_SHOPPING = By.id("continue-shopping");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public CartPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(8));
    }

    public CartPage open() {
        wait.until(ExpectedConditions.elementToBeClickable(CART_LINK)).click();
        return waitForCart();
    }

    public CartPage waitForCart() {
        wait.until(ExpectedConditions.urlContains("cart.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(CART_LIST));
        return this;
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public List<String> getProductNames() {
        return driver.findElements(ITEMS).stream()
                .map(item -> item.findElement(ITEM_NAME).getText())
                .toList();
    }

    public int getItemCount() {
        return driver.findElements(ITEMS).size();
    }

    public int getCartBadgeCount() {
        List<WebElement> badges = driver.findElements(BADGE);
        return badges.isEmpty() ? 0 : Integer.parseInt(badges.get(0).getText());
    }

    public boolean isCartBadgeAbsent() {
        return driver.findElements(BADGE).isEmpty();
    }

    public CartPage removeProduct(String productName) {
        WebElement item = driver.findElements(ITEMS).stream()
                .filter(row -> row.findElement(ITEM_NAME).getText().equals(productName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "Product is not in the cart: " + productName));
        int expectedCount = getCartBadgeCount() - 1;

        wait.until(ExpectedConditions.elementToBeClickable(item.findElement(REMOVE))).click();
        wait.until(d -> !getProductNames().contains(productName));
        wait.withMessage("Cart badge must update after removing " + productName)
                .until(d -> getCartBadgeCount() == expectedCount);
        return this;
    }

    public CheckoutPage clickCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(CHECKOUT)).click();
        return new CheckoutPage(driver).waitForInformationPage();
    }

    public InventoryPage continueShopping() {
        wait.until(ExpectedConditions.elementToBeClickable(CONTINUE_SHOPPING)).click();
        return new InventoryPage(driver).waitUntilLoaded();
    }
}
