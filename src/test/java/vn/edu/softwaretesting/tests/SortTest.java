package vn.edu.softwaretesting.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import vn.edu.softwaretesting.core.WebTestBase;
import vn.edu.softwaretesting.pages.InventoryPage;
import vn.edu.softwaretesting.pages.LoginPage;

@Tag("web")
@DisplayName("FR-04: Sorting")
class SortTest extends WebTestBase {

    // Đổi cho khớp mã lỗi nhóm đã đánh trong báo cáo / Excel.
    private static final String BUG_PROBLEM_USER = "BUG-xx";
    private static final String BUG_VISUAL_USER = "BUG-yy";

    private InventoryPage login(String username) {
        new LoginPage(driver, baseUrl).open().login(username, "secret_sauce");
        return new InventoryPage(driver);
    }

    // TC-SORT-01, TC-SORT-02: EC1, EC2 (standard_user)
    @ParameterizedTest(name = "{0}: sort by name, option={1}")
    @CsvSource({
            "TC-SORT-01, az, true",
            "TC-SORT-02, za, false"
    })
    void sortByName(String id, String option, boolean ascending) {
        InventoryPage inventory = login("standard_user");
        Map<String, Double> baseline = inventory.items();

        inventory.sortBy(option);

        List<String> actual = inventory.names();
        List<String> expected = new ArrayList<>(actual);
        expected.sort(ascending
                ? String.CASE_INSENSITIVE_ORDER
                : String.CASE_INSENSITIVE_ORDER.reversed());
        assertEquals(expected, actual, () -> id + ": names not sorted, got " + actual);
        assertIntact(inventory, baseline, id);
    }

    // TC-SORT-03, TC-SORT-04: EC3, EC4 (standard_user)
    @ParameterizedTest(name = "{0}: sort by price, option={1}")
    @CsvSource({
            "TC-SORT-03, lohi, true",
            "TC-SORT-04, hilo, false"
    })
    void sortByPrice(String id, String option, boolean ascending) {
        InventoryPage inventory = login("standard_user");
        Map<String, Double> baseline = inventory.items();

        inventory.sortBy(option);

        List<Double> actual = inventory.prices();
        List<Double> expected = new ArrayList<>(actual);
        expected.sort(ascending ? Comparator.naturalOrder() : Comparator.reverseOrder());
        assertEquals(expected, actual, () -> id + ": prices not sorted, got " + actual);
        assertIntact(inventory, baseline, id);
    }

    // TC-SORT-05 (problem_user), TC-SORT-06 (visual_user): lỗi hệ thống đã biết.
    // Test PHẢI fail; không sửa assertion để pass.
    @Tag("known-bug")
    @ParameterizedTest(name = "{0} [{1}]: user={2}, option={3}")
    @MethodSource("knownBugCases")
    void sortWithBuggyAccounts(String id, String bug, String username, String option) {
        InventoryPage inventory = login(username);
        Map<String, Double> baseline = inventory.items();

        inventory.sortBy(option);

        boolean byName = option.equals("az") || option.equals("za");
        if (byName) {
            List<String> actual = inventory.names();
            List<String> expected = new ArrayList<>(actual);
            expected.sort(option.equals("az")
                    ? String.CASE_INSENSITIVE_ORDER
                    : String.CASE_INSENSITIVE_ORDER.reversed());
            assertEquals(expected, actual, () -> id + " [" + bug + "]: names not sorted, got " + actual);
        } else {
            List<Double> actual = inventory.prices();
            List<Double> expected = new ArrayList<>(actual);
            expected.sort(option.equals("lohi") ? Comparator.naturalOrder() : Comparator.reverseOrder());
            assertEquals(expected, actual, () -> id + " [" + bug + "]: prices not sorted, got " + actual);
        }
        assertIntact(inventory, baseline, id + " [" + bug + "]");
    }

    static Stream<Arguments> knownBugCases() {
        return Stream.of("za", "lohi", "hilo").flatMap(option -> Stream.of(
                Arguments.of("TC-SORT-05", BUG_PROBLEM_USER, "problem_user", option),
                Arguments.of("TC-SORT-06", BUG_VISUAL_USER, "visual_user", option)));
    }

    /** Quy tắc chung của FR-04: đủ 6 sản phẩm, tên và giá từng sản phẩm không đổi. */
    private void assertIntact(InventoryPage inventory, Map<String, Double> baseline, String id) {
        assertEquals(6, inventory.itemCount(), () -> id + ": expected 6 products");
        assertEquals(baseline, inventory.items(),
                () -> id + ": a product name or price changed after sorting");
    }
}
