package nl.praegus.fitnesse.slim.fixtures.playwright.DI;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

import dagger.Module;
import dagger.Provides;
import nl.praegus.fitnesse.slim.fixtures.playwright.PlaywrightFitnesseException;

import javax.inject.Singleton;

@Module
public class PlaywrightModule {
    String browserType;

    public PlaywrightModule(String browserType) {
        this.browserType = browserType.toLowerCase();
    }

    @Provides
    @Singleton
    Playwright providePlaywright() {
        return Playwright.create();
    }

    @Provides
    @Singleton
    BrowserType.LaunchOptions provideLaunchOptions() {
        return new BrowserType.LaunchOptions();
    }

    @Provides
    @Singleton
    Browser.NewContextOptions provideNewContextOptions() {
        return new Browser.NewContextOptions();
    }

    @Provides
    BrowserType provideBrowser(Playwright playwright, BrowserType.LaunchOptions launchOptions) {
        //TODO Get rid of try catch somehow.
        try {
            return (BrowserType) Playwright.class.getMethod(browserType).invoke(playwright);
        } catch (Exception e) {
            throw new PlaywrightFitnesseException("Unsupported browser name. Use Chromium, Firefox or Webkit!");
        }
    }
}