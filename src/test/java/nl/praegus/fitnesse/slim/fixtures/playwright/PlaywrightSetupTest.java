package nl.praegus.fitnesse.slim.fixtures.playwright;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlaywrightSetupTest {

    @Test
    void startBrowser() {
        var playwrightSetUp = new PlaywrightSetup();
        playwrightSetUp.startBrowser();
        assertNotNull(playwrightSetUp.browser);
    }

    @Test
    void default_constructor_defaults_to_chromium() {
        var playwrightSetUp = new PlaywrightSetup();
        assertEquals("chromium", playwrightSetUp.browserType.name());
    }

    @Test
    void constructor_arg_sets_browser_type() {
        var playwrightSetUp = new PlaywrightSetup("webkit");
        assertEquals("webkit", playwrightSetUp.browserType.name());
    }
}