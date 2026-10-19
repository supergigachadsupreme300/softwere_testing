
package vn.edu.softwaretesting.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class InventoryPage {

    private static final By INVENTORY_PAGE = By.id("inventory_container");
    private static final By CART_BADGE =By.cssSelector("[data-test='shopping-cart-badge']");
    private final WebDriver driver;
    private final WebDriverWait wait;

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(
                driver, Duration.ofSeconds(8));
    }

    public InventoryPage waitUntilLoaded() {    //you should call method .login inside class LoginPage before this
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                INVENTORY_PAGE));
        return this;
    }

    private By getProductButton(String productName) {
        String xpath =
                "//div[contains(@class,'inventory_item')]"  //find a div with the class name of inventory_item (there are 6 of it)
                + "[.//div[@data-test='inventory-item-name'"    //only get that div if it's descendant have another div type of data-test=inventory-item-name
                + " and normalize-space()='" + productName + "']]"  //  AND it's element text is product name
                + "//button";  //find a descendant of the div with class=inventory_name that have button element
        return By.xpath(xpath);
    }

    public void addProductToCart(String productName) {
        int expectedCount = getCartBadgeCount() + 1;

        WebElement button = wait.until(
                ExpectedConditions.elementToBeClickable(
                        getProductButton(productName)));

        if (!button.getText().equals("Add to cart")) {
            throw new IllegalStateException(
                    productName + " is already in the cart");
        }

        button.click();
        waitForCartBadgeCount(expectedCount);   //wait for change before proceed
    }

    public void removeProductFromCart(String productName) {
    int expectedCount = getCartBadgeCount() - 1;
    WebElement button = wait.until(
                ExpectedConditions.elementToBeClickable(
                        getProductButton(productName)));

    if (!button.getText().equals("Remove")) {
        throw new IllegalStateException(
                productName + " is not in the cart");
    }

    button.click();
    waitForCartBadgeCount(expectedCount);   //wait for change before proceed
}

    public void waitForCartBadgeCount(int expectedCount) {
        wait.withMessage(() ->
                "Expected cart badge count: " + expectedCount       //error reporting
                        + ", actual count: " + getCartBadgeCount()
        ).until(d -> getCartBadgeCount() == expectedCount);
    }
    public int getCartBadgeCount() { //return the number on the cart badge, 0 if empty
        //findElements will return a list of element (at most 1 element)
        List<WebElement> badges = driver.findElements(CART_BADGE);  //findElements will handle non-existent badge gracefully, findElement will throw expception.
        if (badges.isEmpty()) { //findElement is empty
            return 0;
        }
        return Integer.parseInt(badges.get(0).getText());   //get(0).getText() = the first element's string.
    }
    public String getProductButtonText(String productName){
        return wait.until(ExpectedConditions
                    .visibilityOfElementLocated(getProductButton(productName)))
                .getText();
    }
}
