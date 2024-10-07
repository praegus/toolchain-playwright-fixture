package nl.praegus.fitnesse.slim.fixtures.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.ColorScheme;
import com.microsoft.playwright.options.Proxy;
import nl.hsac.fitnesse.fixture.slim.SlimFixture;
import nl.hsac.fitnesse.fixture.slim.SlimFixtureException;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;

public final class PlaywrightSetup extends SlimFixture {
    private static final Playwright playwright = Playwright.create();
    private static Browser browser;
    private static final BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();
    private static final Browser.NewContextOptions newContextOptions = new Browser.NewContextOptions();
    private final File harDir = new File(getEnvironment().getFitNesseFilesSectionDir(), "har");

    public static void configureProxy(String server) {
        launchOptions.setProxy(new Proxy(server));
    }

    public static void startBrowser(String browserName) {
        switch (browserName.toLowerCase()) {
            case "chromium":
                browser = playwright.chromium().launch(launchOptions);
                break;
            case "firefox":
                browser = playwright.firefox().launch(launchOptions);
                break;
            case "webkit":
                browser = playwright.webkit().launch(launchOptions);
                break;
            default:
                throw new SlimFixtureException("Unsupported browser name. Use Chromium, Firefox or Webkit!");
        }
    }

    public static void setDeviceScaleFactor(int scaleFactor) {
        newContextOptions.setDeviceScaleFactor(scaleFactor);
    }

    public static void setViewportWidthAndHeight(int width, int height) {
        newContextOptions.setViewportSize(width, height);
    }

    public static Browser.NewContextOptions getNewContextOptions() {
        return newContextOptions;
    }

    public static Browser getBrowser() {
        return browser;
    }

    public void setHeadless(Boolean headless) {
        launchOptions.setHeadless(headless);
    }

    public void createHarWithName(String harName) {
        newContextOptions.setRecordHarOmitContent(true);
        newContextOptions.setRecordHarPath(Paths.get(harDir + "/" + harName + ".har"));
    }

    public void createHar() {
        createHarWithName("harFile");
    }

    public void setAcceptDownloads(Boolean acceptDownloads) {
        newContextOptions.setAcceptDownloads(acceptDownloads);
    }

    public void setBypassCSP(Boolean bypassCSP) {
        newContextOptions.setBypassCSP(bypassCSP);
    }

    public void setColorScheme(String colorScheme) {
        newContextOptions.setColorScheme(ColorScheme.valueOf(colorScheme.toUpperCase()));
    }

    public void setExtraHTTPHeaders(Map<String, String> extraHTTPHeaders) {
        newContextOptions.setExtraHTTPHeaders(extraHTTPHeaders);
    }

    public void setBaseUrl(String baseUrl) {
        newContextOptions.setBaseURL(baseUrl);
    }

    public void closeBrowser() {
        browser.close();
    }

    public void closePlaywright() {
        playwright.close();
    }
}

