package vn.edu.softwaretesting.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import vn.edu.softwaretesting.core.WebTestBase;
import vn.edu.softwaretesting.pages.CheckoutPage;
import vn.edu.softwaretesting.pages.CheckoutPage.CartPage;
import vn.edu.softwaretesting.pages.CheckoutPage.InventoryPage;
import vn.edu.softwaretesting.pages.LoginPage;
import static org.junit.jupiter.api.Assertions.*;

public class CheckoutTest extends WebTestBase {
    private CheckoutPage checkout;

    @BeforeEach
    void prepareCheckout() {
        LoginPage.LoginResult result = new LoginPage(driver, baseUrl)
                .open().login("standard_user", "secret_sauce");
        assertTrue(result.accepted(), result.message());
        checkout = new InventoryPage(driver).addBackpack().openCart().clickCheckout();
        assertTrue(driver.getCurrentUrl().contains("checkout-step-one.html"));
    }

    @Test
    @DisplayName("TC-CHK-01")
    void completeCheckout() {
        checkout.fillInformation("An", "Nguyen", "700000").clickContinue().waitForOverview();
        assertAll(
                () -> assertEquals("Item total: $29.99", checkout.getItemTotal()),
                () -> assertEquals("Tax: $2.40", checkout.getTax()),
                () -> assertEquals("Total: $32.39", checkout.getTotal()));
        checkout.clickFinish();
        assertAll(
                () -> assertTrue(driver.getCurrentUrl().contains("checkout-complete.html")),
                () -> assertEquals("Thank you for your order!", checkout.getCompleteHeader()),
                () -> assertTrue(checkout.isCartBadgeAbsent(), "Cart badge must disappear"));
    }

    @DisplayName("TC-CHK-02 / TC-CHK-03 / TC-CHK-04")
    @ParameterizedTest(name = "{0}: missing field -> {4}")
    @CsvSource({
            "TC-CHK-02, '', Nguyen, 700000, Error: First Name is required",
            "TC-CHK-03, An, '', 700000, Error: Last Name is required",
            "TC-CHK-04, An, Nguyen, '', Error: Postal Code is required"
    })
    void rejectMissingField(String caseId, String first, String last,
                            String postal, String expectedError) {
        checkout.fillInformation(first, last, postal).clickContinue();
        assertAll(
                () -> assertEquals(expectedError, checkout.getErrorMessage(), caseId),
                () -> assertTrue(driver.getCurrentUrl().contains("checkout-step-one.html"), caseId));
    }

    @Test
    @DisplayName("TC-CHK-05")
    void cancelCheckoutKeepsCart() {
        CartPage cart = checkout.clickCancel();
        assertAll(
                () -> assertTrue(driver.getCurrentUrl().contains("cart.html")),
                () -> assertEquals("Sauce Labs Backpack", cart.getBackpackName()),
                () -> assertEquals("1", cart.getBadgeText()));
    }
}
