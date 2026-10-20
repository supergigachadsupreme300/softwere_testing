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

    /*
    Khởi tạo CheckoutPage với driver đang dùng.
    Tạo WebDriverWait 8 giây để chờ các phần tử xuất hiện.
    */
    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(8));
    }

    public String getCurrentUrl()  {
    return driver.getCurrentUrl();
    }
    /*
    Chờ trang nhập thông tin (checkout-step-one.html) tải xong.
    Chờ URL chứa "checkout-step-one.html" và ô First Name hiển thị.
    */
    public CheckoutPage waitForInformationPage() {
        wait.until(ExpectedConditions.urlContains("checkout-step-one.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(FIRST));
        return this;
    }

    /*
    Nhập giá trị vào một ô input sau khi chờ ô có thể click.
    Xóa trắng trước khi nhập; null được coi như chuỗi rỗng.
    */
    private void enter(By locator, String value) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(locator));
        input.clear();
        input.sendKeys(value == null ? "" : value);
    }

    //Nhập họ tên(name)
    public CheckoutPage enterFirstName(String value) { enter(FIRST, value); return this; }
    public CheckoutPage enterLastName(String value) { enter(LAST, value); return this; }
     //Nhập Postal Code (mã bưu điện).
    public CheckoutPage enterPostalCode(String value) { enter(POSTAL, value); return this; }
    /*
      Nhập đầy đủ 3 trường thông tin giao hàng trong một lần gọi.
      @param first tên
      @param last họ
      @param postal mã bưu điện
      @return chính đối tượng này để viết chuỗi liên tục
    */
    public CheckoutPage fillInformation(String first, String last, String postal) {
        return enterFirstName(first).enterLastName(last).enterPostalCode(postal);
    }

    /*
    Nhấn nút Continue để sang bước tổng quan (step-two).
    Sau khi click, chờ một trong hai trạng thái: hiện lỗi hoặc
    Chuyển sang URL chứa "checkout-step-two.html".
    */
    public CheckoutPage clickContinue() {
        wait.until(ExpectedConditions.elementToBeClickable(CONTINUE)).click();
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(ERROR),
                ExpectedConditions.urlContains("checkout-step-two.html")));
        return this;
    }

    /*
    Nhấn nút Cancel để hủy checkout và quay về giỏ hàng.
    Chờ nút Cancel có thể click rồi đợi trang cart tải xong.
    Trang giỏ hàng (CartPage) sau khi hủy
    */
    public CartPage clickCancel() {
        wait.until(ExpectedConditions.elementToBeClickable(CANCEL)).click();
        return new CartPage(driver).waitForCart();
    }
    //Đọc chữ của một phần tử sau khi chờ nó hiển thị.
    private String text(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
    }


    //Lấy thông báo lỗi trên form (ví dụ: thiếu First/Last/Postal).
    public String getErrorMessage() { return text(ERROR); }


    //Lấy dòng "Item total" trên trang tổng quan.
    public String getItemTotal() { return text(SUBTOTAL); }
    //Lấy dòng tiền thuế trên trang tổng quan.
    public String getTax() { return text(TAX); }
    //Lấy dòng tổng tiền trên trang tổng quan.
    public String getTotal() { return text(TOTAL); }

    /*
    Lấy tiêu đề xác nhận trên trang hoàn tất.
    */
    public String getCompleteHeader() { return text(HEADER); }

    /*
    Chờ trang tổng quan đơn hàng (checkout-step-two.html) tải xong.
    Chờ URL chứa "checkout-step-two.html" và dòng Subtotal hiển thị.
    */
    public CheckoutPage waitForOverview() {
        wait.until(ExpectedConditions.urlContains("checkout-step-two.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(SUBTOTAL));
        return this;
    }

    /*
    Nhấn nút Finish để hoàn tất đơn hàng.
    Chờ chuyển sang "checkout-complete.html" và tiêu đề hoàn tất hiển thị.
    */
    public CheckoutPage clickFinish() {
        wait.until(ExpectedConditions.elementToBeClickable(FINISH)).click();
        wait.until(ExpectedConditions.urlContains("checkout-complete.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(HEADER));
        return this;
    }

    /*
    Kiểm tra huy hiệu giỏ hàng đã biến mất sau khi đặt hàng xong.
    Dùng điều kiện invisibility (ẩn hoặc không còn trong DOM).
    */
    public boolean isCartBadgeAbsent() {
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(BADGE));
        } catch (TimeoutException exception) {
            return false;
        }
    }

    // Tách các page object lồng nhau để giữ toàn bộ chi tiết UI nằm ngoài lớp test.
    /*
    Page Object cho trang danh sách sản phẩm (inventory).
    Chỉ giữ locator và thao tác thêm hàng + mở giỏ hàng.
    */
    public static final class InventoryPage {
        // Nút thêm ba-lô vào giỏ và link mở giỏ hàng.
        private static final By ADD = By.id("add-to-cart-sauce-labs-backpack");
        private static final By CART = By.cssSelector(".shopping_cart_link");
        private final WebDriver driver;
        private final WebDriverWait wait;

        /*
        Khởi tạo InventoryPage với driver đang dùng.
        */
        public InventoryPage(WebDriver driver) {
            this.driver = driver;
            wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        }

        /*
        Thêm ba-lô Sauce Labs vào giỏ hàng.
        Chờ trang inventory tải xong, click nút Add rồi chờ badge hiện "1".
        */
        public InventoryPage addBackpack() {
            wait.until(ExpectedConditions.urlContains("inventory.html"));
            wait.until(ExpectedConditions.elementToBeClickable(ADD)).click();
            wait.until(ExpectedConditions.textToBe(BADGE, "1"));
            return this;
        }

        /*
        Mở giỏ hàng bằng cách click icon giỏ hàng.
        Chờ icon có thể click rồi đợi trang cart tải xong.
        */
        public CartPage openCart() {
            wait.until(ExpectedConditions.elementToBeClickable(CART)).click();
            return new CartPage(driver).waitForCart();
        }
    }

    /*
    Page Object cho trang giỏ hàng (cart).
    Chỉ giữ locator và thao tác kiểm tra + bấm checkout.
    */
    public static final class CartPage {
        // Nút checkout và tên sản phẩm ba-lô trong giỏ.
        private static final By CHECKOUT = By.id("checkout");
        private static final By BACKPACK = By.xpath(
                "//div[contains(@class,'cart_item')][.//button[@id='remove-sauce-labs-backpack']]"
                + "//div[@class='inventory_item_name']");
        private final WebDriver driver;
        private final WebDriverWait wait;
        /*
        Khởi tạo CartPage với driver đang dùng.
        */
        public CartPage(WebDriver driver) {
            this.driver = driver;
            wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        }
         public String getCurrentUrl() {
        return driver.getCurrentUrl();
        }
        /*
        Chờ trang giỏ hàng (cart.html) tải xong.
        Chờ URL chứa "cart.html" và nút Checkout hiển thị.
        */
        public CartPage waitForCart() {
            wait.until(ExpectedConditions.urlContains("cart.html"));
            wait.until(ExpectedConditions.visibilityOfElementLocated(CHECKOUT));
            return this;
        }

        /*
        Nhấn nút Checkout để sang bước nhập thông tin.
        Chờ nút có thể click rồi đợi trang information tải xong.
        */
        public CheckoutPage clickCheckout() {
            wait.until(ExpectedConditions.elementToBeClickable(CHECKOUT)).click();
            return new CheckoutPage(driver).waitForInformationPage();
        }

        /*
        Lấy tên sản phẩm ba-lô trong giỏ hàng.
        Chờ dòng tên hiển thị rồi đọc chữ.
        */
        public String getBackpackName() {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(BACKPACK)).getText();
        }

        /* 
        Lấy số hiện trên huy hiệu giỏ hàng.
        Chờ badge hiển thị rồi đọc chữ.
        */
        public String getBadgeText() {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(BADGE)).getText();
        }
    }
}
