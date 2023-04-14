package nl.praegus.fitnesse.slim.fixtures.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.options.Cookie;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class BrowserContextManager {
    private final Browser browser;
    private final Browser.NewContextOptions newContextOptions;
    private BrowserContext browserContext;
//    private final Path storageStateDir = PlaywrightFixtureBase.getWikiFilesDir().resolve("storage-states");
//    private final Path tracesDir = PlaywrightFixtureBase.getWikiFilesDir().resolve("traces");

    public BrowserContextManager(Browser currentBrowser, Browser.NewContextOptions currentNewContextOptions) {
        browser = currentBrowser;
        newContextOptions = currentNewContextOptions;
    }

    public String getStorageState() {
        return getBrowserContext().storageState();
    }

    public BrowserContext getBrowserContext() {
        if (browserContext == null) {
            browserContext = newContext();
        }
        return browserContext;
    }

    public BrowserContext newContext() {
        browserContext = browser.newContext(newContextOptions);
        return browserContext;
    }

    public void openNewContextWithStorageState(String name) {
//        try {
//            browserContext = browser.newContext(newContextOptions.setStorageStatePath(Paths.get(storageStateDir + "/" + name + ".json")));
//        } catch (Exception e) {
//            throw new PlaywrightFitnesseException(e.getMessage());
//        }
    }

    public void close() {
        browserContext.close();
        browserContext = null;
    }

    public void addCookies(String cookieKey) {
        getBrowserContext().addCookies(Arrays.asList(CookieJar.getCookie(cookieKey)));
    }

    public List<Cookie> cookies() {
        return getBrowserContext().cookies();
    }

    public void clearCookies() {
        getBrowserContext().clearCookies();
    }

    public Page newPage() {
        return getBrowserContext().newPage();
    }

    public void storageState(String name) {
//        getBrowserContext().storageState(new BrowserContext.StorageStateOptions().setPath(Paths.get(storageStateDir + "/" + name + ".json")));
    }

    public void startTrace() {
        getBrowserContext().tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(false));
    }

    public void saveTrace(String name) {
//        getBrowserContext().tracing().stop(new Tracing.StopOptions().setPath(Paths.get(tracesDir + "/" + name + ".zip")));
    }

    public void setUrlToReturnBody(String url, String body) {
        getBrowserContext().route(url, route -> route.fulfill(new Route.FulfillOptions().setBody(body)));
    }

    public List<Page> pages() {
        return getBrowserContext().pages();
    }

    public void setDefaultTimeout(double timeout) {
        getBrowserContext().setDefaultTimeout(timeout);
    }
}
