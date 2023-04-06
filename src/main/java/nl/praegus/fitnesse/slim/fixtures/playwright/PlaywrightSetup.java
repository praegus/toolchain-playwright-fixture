package nl.praegus.fitnesse.slim.fixtures.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import nl.praegus.fitnesse.slim.fixtures.playwright.DI.DaggerPlaywrightComponent;
import nl.praegus.fitnesse.slim.fixtures.playwright.DI.PlaywrightComponent;
import nl.praegus.fitnesse.slim.fixtures.playwright.DI.PlaywrightModule;

import javax.inject.Inject;
import java.nio.file.Path;

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

    public PlaywrightSetup() {
        init("chromium");
    }

    public PlaywrightSetup(String browserType) {
        init(browserType);
    }

    private void init(String browserType){
        var component = DaggerPlaywrightComponent.builder()
                .playwrightModule(new PlaywrightModule(browserType))
                .build();

        component.inject(this);
    }

    public void startBrowser() {
        browser = browserType.launch(launchOptions);
    }

    public void  closeBrowser( ){
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

