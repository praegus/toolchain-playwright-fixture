package nl.praegus.fitnesse.slim.fixtures.playwright;

import nl.praegus.fitnesse.slim.fixtures.playwright.enums.PlaywrightBrowserType;
import nl.praegus.fitnesse.slim.fixtures.playwright.exceptions.UnsupportedBrowserException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlaywrightSetupTest {
    @Test
    void startBrowser_creates_browser_instance() {
        var playwrightSetUp = new PlaywrightSetup();
        playwrightSetUp.startBrowser();
        assertNotNull(playwrightSetUp.browser);
    }

    @Test
    void default_constructor_defaults_to_chromium() {
        var playwrightSetUp = new PlaywrightSetup();
        assertEquals(PlaywrightBrowserType.CHROMIUM.getName(), playwrightSetUp.browserType.name());
    }

    @Test
    void constructor_arg_sets_browser_type() {
        var playwrightSetUp = new PlaywrightSetup("webkit");
        assertEquals(PlaywrightBrowserType.WEBKIT.getName(), playwrightSetUp.browserType.name());
    }
    @Test
    void unknown_browser_throws_unsupported_browser_exception(){
        assertThrows(UnsupportedBrowserException.class, () -> new PlaywrightSetup("netscape"));
    }
}