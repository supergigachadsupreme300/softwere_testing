
package vn.edu.softwaretesting.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CartPage {

    private final WebDriver driver;

    // Page locators
    private final By pageTitle = By.className("title");
    private final By cartItems = By.className("cart_item");
    private final By productNames = By.className("inventory_item_name");
    private final By productPrices = By.className("inventory_item_price");
    private final By cartBadge = By.className("shopping_cart_badge");
    private final By continueShoppingButton = By.id("continue-shopping");
    private final By checkoutButton = By.id("checkout");

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }
    //cartPage Sanity check
    public boolean isCartPageDisplayed() {
        return driver.getCurrentUrl().contains("cart.html")
                && driver.findElement(pageTitle)
                        .getText().equals("Your Cart");
    }
    //get all products in the cart, the returned result can be used in a list loop
    public List<WebElement> getCartItems() {
        return driver.findElements(cartItems);
    }

    public boolean isProductInCart(String productName) {
        for (WebElement item : getCartItems()) {
            String name = item.findElement(productNames).getText();

            if (name.equals(productName)) {
                return true;
            }
        }

        return false;
    }

    // Get the number shown on the cart badge, return 0 if empty
    public int getCartBadgeCount() {
        List<WebElement> badges = driver.findElements(cartBadge);

        if (badges.isEmpty()) {
            return 0;
        }

        return Integer.parseInt(badges.get(0).getText());
    }

    // Remove a specific product from the cart
    public void removeProduct(String productName) {
        for (WebElement item : getCartItems()) {
            String name = item.findElement(productNames).getText();

            if (name.equals(productName)) {
                item.findElement(By.tagName("button")).click();
                return;
            }
        }

        throw new IllegalArgumentException(
                "Product not found in cart: " + productName);
    }

    // Get all product names, use this function if you dont need detailed info of getCartItems
    public List<WebElement> getProductNames() {
        return driver.findElements(productNames);
    }

    // Get all product price, use this function if you dont need detailed info of getCartItems
    //this method probably don't get used much
    public List<WebElement> getProductPrices() {
        return driver.findElements(productPrices);
    }

    public void clickContinueShopping() {
        driver.findElement(continueShoppingButton).click();
    }

    public void clickCheckout() {
        driver.findElement(checkoutButton).click();
    }
}
