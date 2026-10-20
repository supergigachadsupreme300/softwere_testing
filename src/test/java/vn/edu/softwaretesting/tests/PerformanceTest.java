package vn.edu.softwaretesting.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import vn.edu.softwaretesting.core.WebTestBase;
import vn.edu.softwaretesting.pages.LoginPage;
import vn.edu.softwaretesting.pages.LoginPage.LoginResult;

public class PerformanceTest extends WebTestBase {

    @Test
    @Tag("known-bug")
    @DisplayName("TC-NFR-01 [BUG-01]: inventory loads within 3 s")
    void inventoryLoadsWithinThreeSeconds() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        int runs = 5;
        long[] times = new long[runs];

        for (int i = 0; i < runs; i++) {
            loginPage.openFresh();

            long start = System.nanoTime();
            LoginResult result = loginPage.login("performance_glitch_user", "secret_sauce");
            long millis = Duration.ofNanos(System.nanoTime() - start).toMillis();

            assertTrue(result.accepted(), "Login failed at run " + (i + 1));
            times[i] = millis;
        }

        long[] sortedTimes = times.clone();
        Arrays.sort(sortedTimes);
        long median = sortedTimes[runs / 2];

        System.out.println("Runs (ms): " + Arrays.toString(times));
        System.out.println("Sorted (ms): " + Arrays.toString(sortedTimes));
        System.out.println("Median (ms): " + median);

        assertTrue(
                median <= 3000,
                () -> "Median took " + median + " ms (5 runs: " + Arrays.toString(times) + ")");
    }
}