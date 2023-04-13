package nl.praegus.fitnesse.slim.fixtures.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import nl.praegus.fitnesse.slim.fixtures.playwright.di.DaggerPlaywrightComponent;
import nl.praegus.fitnesse.slim.fixtures.playwright.di.PlaywrightModule;
import nl.praegus.fitnesse.slim.fixtures.playwright.enums.PlaywrightBrowserType;
import nl.praegus.fitnesse.slim.fixtures.playwright.exceptions.UnsupportedBrowserException;

import javax.inject.Inject;

public final class PlaywrightSetup extends SlimFixtureBase {
    @Inject
    Playwright playwright;
    @Inject
    BrowserType browserType;
    @Inject
    BrowserType.LaunchOptions launchOptions;
    Browser browser;
    @Inject
    Browser.NewContextOptions newContextOptions;

    /**
     * If the empty constructor is used. Chromium is started.
     */
    public PlaywrightSetup() {
        init(PlaywrightBrowserType.CHROMIUM);
    }

    /**
     * When constructor argument is provided start given browser.
     *
     * @param browserName name of the browser to use. E.g. "chromium"
     *
     * @throws UnsupportedBrowserException
     */

    public PlaywrightSetup(String browserName) {
        init(PlaywrightBrowserType.findByName(browserName).orElseThrow(UnsupportedBrowserException::new));
    }

    /**
     * Sets up dependency injection with Dagger
     *
     * @param playwrightBrowserType PlaywrightBrowserType to initialize
     */
    private void init(PlaywrightBrowserType playwrightBrowserType) {
        var component = DaggerPlaywrightComponent.builder()
                .playwrightModule(new PlaywrightModule(playwrightBrowserType))
                .build();

        component.inject(this);
    }

    public void startBrowser() {
        browser = browserType.launch(launchOptions);
    }

    public void closeBrowser() {
        browser.close();
    }

    public void setHeadless(Boolean headless) {
        launchOptions.setHeadless(headless);
    }

    // For debugging
    @Deprecated
    public void nu() {
        browser.newContext().newPage().navigate("https://nu.nl");
    }

    public void closePlaywright() {
        playwright.close();
    }
}

