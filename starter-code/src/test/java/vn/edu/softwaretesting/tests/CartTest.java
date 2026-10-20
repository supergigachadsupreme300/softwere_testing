package vn.edu.softwaretesting.tests;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import vn.edu.softwaretesting.core.WebTestBase;
import vn.edu.softwaretesting.pages.CartPage;
import vn.edu.softwaretesting.pages.InventoryPage;
import vn.edu.softwaretesting.pages.LoginPage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("web")
public class CartTest extends WebTestBase {
    private static final String BACKPACK = "Sauce Labs Backpack";
    private static final List<String> PRODUCTS = List.of(
            BACKPACK,
            "Sauce Labs Bike Light",
            "Sauce Labs Bolt T-Shirt",
            "Sauce Labs Fleece Jacket",
            "Sauce Labs Onesie",
            "Test.allTheThings() T-Shirt (Red)");

    private InventoryPage inventoryPage;

    @BeforeEach
    void setUpCartTest() {
        LoginPage.LoginResult result = new LoginPage(driver, baseUrl)
                .open().login("standard_user", "secret_sauce");
        assertTrue(result.accepted(), result.message());
        inventoryPage = new InventoryPage(driver).waitUntilLoaded();
    }

    private CartPage openCart() {
        return new CartPage(driver).open();
    }

    private void assertCartContents(CartPage cart, List<String> expectedProducts) {
        assertAll(
                () -> assertEquals(expectedProducts.size(), cart.getItemCount(), "Cart rows"),
                () -> assertEquals(expectedProducts.size(), cart.getCartBadgeCount(), "Cart badge"),
                () -> assertEquals(Set.copyOf(expectedProducts), Set.copyOf(cart.getProductNames()),
                        "Products in the cart"));
    }

    @Test
    @DisplayName("TC-CART-B1: Badge empty upon login")
    void badgeEmptyUponLogin() {
        assertEquals(0, inventoryPage.getCartBadgeCount());
        CartPage cart = openCart();
        assertCartContents(cart, List.of());
        assertTrue(cart.isCartBadgeAbsent(), "Empty cart must not display a badge");
    }

    @ParameterizedTest(name = "{0}: Add {1}")
    @CsvFileSource(resources = "/cartTest-data.csv", numLinesToSkip = 1)
    void addOneProduct(String testCaseId, String productName, int expectedCount) {
        inventoryPage.addProductToCart(productName);
        assertEquals(expectedCount, inventoryPage.getCartBadgeCount(), testCaseId);
        assertCartContents(openCart(), List.of(productName));
    }

    @Test
    @DisplayName("TC-CART-B3: Add six products, remove one")
    void addSixProductsThenRemoveOne() {
        PRODUCTS.forEach(inventoryPage::addProductToCart);
        assertEquals(6, inventoryPage.getCartBadgeCount());

        inventoryPage.removeProductFromCart(BACKPACK);

        assertEquals(5, inventoryPage.getCartBadgeCount());
        assertCartContents(openCart(), PRODUCTS.stream()
                .filter(product -> !product.equals(BACKPACK)).toList());
    }

    @Test
    @DisplayName("TC-CART-B4: Add six products")
    void addSixProducts() {
        PRODUCTS.forEach(inventoryPage::addProductToCart);
        assertEquals(6, inventoryPage.getCartBadgeCount());
        assertCartContents(openCart(), PRODUCTS);
    }

    @Test
    @DisplayName("TC-CART-B5: Add one product then remove it")
    void addOneProductThenRemoveOne() {
        inventoryPage.addProductToCart(BACKPACK);
        CartPage cart = openCart();
        assertCartContents(cart, List.of(BACKPACK));

        cart.removeProduct(BACKPACK);

        assertCartContents(cart, List.of());
        assertTrue(cart.isCartBadgeAbsent(), "Removing the last item must remove the badge");
        inventoryPage = cart.continueShopping();
        assertAll(
                () -> assertEquals(0, inventoryPage.getCartBadgeCount()),
                () -> assertEquals("Add to cart", inventoryPage.getProductButtonText(BACKPACK)));
    }

    @Test
    @DisplayName("TC-CART-EP1: Button State change when adding a product")
    void buttonStateWhenAdd() {
        int count = inventoryPage.getCartBadgeCount();
        assertEquals("Add to cart", inventoryPage.getProductButtonText(BACKPACK));

        inventoryPage.addProductToCart(BACKPACK);

        assertAll(
                () -> assertEquals("Remove", inventoryPage.getProductButtonText(BACKPACK)),
                () -> assertEquals(count + 1, inventoryPage.getCartBadgeCount()));
        assertCartContents(openCart(), List.of(BACKPACK));
    }

    @Test
    @DisplayName("TC-CART-EP2: Button State change when removing a product")
    void buttonStateWhenRemove() {
        inventoryPage.addProductToCart(BACKPACK);
        int count = inventoryPage.getCartBadgeCount();
        assertEquals("Remove", inventoryPage.getProductButtonText(BACKPACK));

        inventoryPage.removeProductFromCart(BACKPACK);

        assertAll(
                () -> assertEquals("Add to cart", inventoryPage.getProductButtonText(BACKPACK)),
                () -> assertEquals(count - 1, inventoryPage.getCartBadgeCount()));
        assertCartContents(openCart(), List.of());
    }
}
