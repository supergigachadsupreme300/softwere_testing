
package vn.edu.softwaretesting.pages;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class InventoryPage {
    //FR-02 variables
    private static final By INVENTORY_PAGE = By.id("inventory_container");
    private static final By CART_BADGE =By.cssSelector("[data-test='shopping-cart-badge']");
    private final WebDriver driver;
    private final WebDriverWait wait;
    //fr-04 variables
    private static final By CONTAINER = By.id("inventory_container");
    private static final By SORT = By.cssSelector("[data-test='product-sort-container']");
    private static final By ITEM = By.cssSelector(".inventory_item");
    private static final By ITEM_NAME = By.cssSelector(".inventory_item_name");
    private static final By ITEM_PRICE = By.cssSelector(".inventory_item_price");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(
                driver, Duration.ofSeconds(8));
    }
    //fr-02 methods
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

    waitForCartBadgeCount(expectedCount);

    wait.until(ExpectedConditions.textToBe(
            getProductButton(productName), "Remove"));
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
    wait.until(ExpectedConditions.textToBe(
        getProductButton(productName), "Add to cart"));
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
    // FR-04 methods
    /** Chọn kiểu sắp xếp theo value của option: az, za, lohi, hilo. */
    public InventoryPage sortBy(String optionValue) {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(SORT));
        new Select(dropdown).selectByValue(optionValue);
        try {
            wait.until(ExpectedConditions.attributeToBe(SORT, "value", optionValue));
        } catch (TimeoutException ignored) {
            // Nếu hệ thống không phản ánh lựa chọn, vẫn đọc danh sách để
            // assertion trong test báo lỗi rõ ràng thay vì lỗi timeout.
        }
        return this;
    }

    /** Chữ đang hiển thị trên dropdown, ví dụ "Name (A to Z)". */
    public String selectedSortLabel() {
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(SORT));
        return new Select(dropdown).getFirstSelectedOption().getText();
    }

    /** Tên sản phẩm theo thứ tự đang hiển thị. */
    public List<String> names() {
        return items().keySet().stream().toList();
    }

    /** Giá (dạng số) theo thứ tự đang hiển thị. */
    public List<Double> prices() {
        return items().values().stream().toList();
    }

    /** Tên -> giá, giữ nguyên thứ tự hiển thị. Dùng để kiểm tra tên/giá không bị đổi. */
    public Map<String, Double> items() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(ITEM));
        Map<String, Double> result = new LinkedHashMap<>();
        for (WebElement item : driver.findElements(ITEM)) {
            String name = item.findElement(ITEM_NAME).getText();
            String price = item.findElement(ITEM_PRICE).getText();
            result.put(name, Double.parseDouble(price.replace("$", "").trim()));
        }
        return result;
    }

    public int itemCount() {
        return driver.findElements(ITEM).size();
    }
}
