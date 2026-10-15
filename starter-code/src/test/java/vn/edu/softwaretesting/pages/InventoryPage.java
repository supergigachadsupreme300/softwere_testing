package vn.edu.softwaretesting.pages;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class InventoryPage {
    private static final By Inventory_page = By.id("inventory_container");
    private static final By SL_backpack_btn = By.id("add-to-cart-sauce-labs-backpack");
    private static final By SL_backpack_name = By.cssSelector("[data-test]='inventory-item-name'");
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;
    public InventoryPage(WebDriver driver, String baseUrl) {
    this.driver = driver;
    this.baseUrl = baseUrl;
    this.wait = new WebDriverWait(
            driver, Duration.ofSeconds(8));
    }
    public InventoryPage open() {
    driver.get(baseUrl);
    wait.until(ExpectedConditions
            .visibilityOfElementLocated(Inventory_page));
    return this;
    }
}
