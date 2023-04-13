package nl.praegus.fitnesse.slim.fixtures.playwright;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.praegus.fitnesse.slim.fixtures.playwright.enums.PlaywrightBrowserType;
import org.junit.jupiter.api.Test;

class PlaywrightBrowserTypeTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link PlaywrightBrowserType#valueOf(String)}
     *   <li>{@link PlaywrightBrowserType#getName()}
     * </ul>
     */
    @Test
    void getName_return_lowercase() {
        assertEquals("chromium", PlaywrightBrowserType.valueOf("CHROMIUM").getName());
    }
}

