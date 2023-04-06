package nl.praegus.fitnesse.slim.fixtures.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import nl.praegus.fitnesse.slim.fixtures.playwright.DI.DaggerPlaywrightComponent;
import nl.praegus.fitnesse.slim.fixtures.playwright.DI.PlaywrightComponent;
import nl.praegus.fitnesse.slim.fixtures.playwright.DI.PlaywrightModule;

import javax.inject.Inject;

public final class PlaywrightSetup extends SlimFixtureBase {
    @Inject
    Playwright playwright;
    @Inject
    BrowserType browserType;
    @Inject
    BrowserType.LaunchOptions launchOptions;
    private Browser browser;
    @Inject
    Browser.NewContextOptions newContextOptions;

    public PlaywrightSetup(String browserName) {
        PlaywrightComponent component = DaggerPlaywrightComponent.builder()
                .playwrightModule(new PlaywrightModule(browserName))
                .build();

        component.inject(this);
    }

    public void startBrowser() {
        browser = browserType.launch(launchOptions);
    }

    public Browser.NewContextOptions getNewContextOptions() {
        return newContextOptions;
    }

    public void setHeadless(Boolean headless) {
        launchOptions.setHeadless(headless);
    }

    public void nu() {
        browser.newContext().newPage().navigate("https://nu.nl");
    }

    public void closePlaywright() {
        playwright.close();
    }
}

