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

    public PlaywrightSetup() {
        init(PlaywrightBrowserType.CHROMIUM);
    }

    public PlaywrightSetup(String browserName) {
        init(PlaywrightBrowserType.findByName(browserName).orElseThrow(UnsupportedBrowserException::new));
    }

    private void init(PlaywrightBrowserType browserType) {
        var component = DaggerPlaywrightComponent.builder()
                .playwrightModule(new PlaywrightModule(browserType))
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

