package vn.edu.softwaretesting.pages;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object của inventory.html.
 * Phần FR-04 (sắp xếp). FR-02 (thêm/xóa, badge giỏ hàng) do thành viên
 * khác bổ sung vào cùng lớp này: gộp code, không tạo lớp trùng tên.
 */
public final class InventoryPage {

    private static final By CONTAINER = By.id("inventory_container");
    private static final By SORT = By.cssSelector("[data-test='product-sort-container']");
    private static final By ITEM = By.cssSelector(".inventory_item");
    private static final By ITEM_NAME = By.cssSelector(".inventory_item_name");
    private static final By ITEM_PRICE = By.cssSelector(".inventory_item_price");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        wait.until(ExpectedConditions.visibilityOfElementLocated(CONTAINER));
    }

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
