package vn.edu.softwaretesting.tests;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import vn.edu.softwaretesting.core.WebTestBase;
import vn.edu.softwaretesting.pages.LoginPage;
import vn.edu.softwaretesting.pages.InventoryPage;
public class CartTest extends WebTestBase {

    private InventoryPage inventoryPage;

    @BeforeEach
    void setUpCartTest() {
        LoginPage loginPage =
                new LoginPage(driver, baseUrl).open();

        LoginPage.LoginResult result =
                loginPage.login("standard_user", "secret_sauce");

        assertTrue(result.accepted(), "Login have failed!");    //check if login's accepted return a true boolean, if not then marked as failed.

        inventoryPage =
                new InventoryPage(driver).waitUntilLoaded();
    }
    @Test 
    @DisplayName("TC-CART-B1: Badge empty upon login")
    void  badgeEmptyUponLogin(){
        assertEquals(0, inventoryPage.getCartBadgeCount());
    }
    @ParameterizedTest(name = "{0}: Add {1}")
        @CsvFileSource(resources = "/cartTest-data.csv", numLinesToSkip = 1)
        void addOneProduct(
                String testCaseID,
                String productName,
                int expectedCount) {

        inventoryPage.addProductToCart(productName);

        assertEquals(expectedCount, inventoryPage.getCartBadgeCount());
        }
    @Test
        @DisplayName("TC-CART-B3: Add six products, remove one")
        void addSixProductsThenRemoveOne() {
        String[] products = {
                "Sauce Labs Backpack",
                "Sauce Labs Bike Light",
                "Sauce Labs Bolt T-Shirt",
                "Sauce Labs Fleece Jacket",
                "Sauce Labs Onesie",
                "Test.allTheThings() T-Shirt (Red)"
        };

        for (String product : products) {
                inventoryPage.addProductToCart(product);
        }

        assertEquals(6, inventoryPage.getCartBadgeCount());

        inventoryPage.removeProductFromCart("Sauce Labs Backpack");

        assertEquals(5, inventoryPage.getCartBadgeCount());
        }
        
        @Test
        @DisplayName("TC-CART-B4: Add six products")
        void addSixProducts() {
        String[] products = {
                "Sauce Labs Backpack",
                "Sauce Labs Bike Light",
                "Sauce Labs Bolt T-Shirt",
                "Sauce Labs Fleece Jacket",
                "Sauce Labs Onesie",
                "Test.allTheThings() T-Shirt (Red)"
        };

        for (String product : products) {
                inventoryPage.addProductToCart(product);
        }

        assertEquals(6, inventoryPage.getCartBadgeCount());
                }
        @Test
        @DisplayName("TC-CART-B5: Add one product then remove it")
        void addOneProductThenRemoveOne(){
                inventoryPage.addProductToCart("Sauce Labs Backpack");
                inventoryPage.removeProductFromCart("Sauce Labs Backpack");
                assertEquals(0, inventoryPage.getCartBadgeCount());
        }
        @Test
        @DisplayName("TC-CART-EP1: Button State change when adding a product")
        void buttonStateWhenAdd() {
        String product = "Sauce Labs Backpack";
        int count = inventoryPage.getCartBadgeCount();

        assertEquals("Add to cart",inventoryPage.getProductButtonText(product));
        assertEquals(count, inventoryPage.getCartBadgeCount());

        inventoryPage.addProductToCart(product);

        assertEquals("Remove",inventoryPage.getProductButtonText(product));
        assertEquals(count+1, inventoryPage.getCartBadgeCount());
        }
        @Test
        @DisplayName("TC-CART-EP2: Button State change when removing a product")
        void buttonStateWhenRemove() {
        String product = "Sauce Labs Backpack";
        
        inventoryPage.addProductToCart(product);
        int count = inventoryPage.getCartBadgeCount();  //observe the change from here

        assertEquals("Remove", inventoryPage.getProductButtonText(product));
        assertEquals(count, inventoryPage.getCartBadgeCount());

        inventoryPage.removeProductFromCart(product);

        assertEquals("Add to cart", inventoryPage.getProductButtonText(product));
        assertEquals(count-1, inventoryPage.getCartBadgeCount());
        }
}