package nl.praegus.fitnesse.slim.fixtures.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.assertions.PageAssertions;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static fitnesse.slim.SlimVersion.PRETTY_PRINT_TAG_END;
import static fitnesse.slim.SlimVersion.PRETTY_PRINT_TAG_START;

/**
 * FitNesse fixture enabling the use of the java-playwright api for browser automation
 *
 * @see <a href="https://playwright.dev/java/">Playwright Java documentation</a>.
 */
public class PlaywrightFixture extends SlimFixtureBase {
    private final Browser browser = PlaywrightSetup.getBrowser();
    private final CookieManager cookieManager = new CookieManager();
    private final Path screenshotsDir = getWikiFilesDir().resolve("screenshots");
    private final Path tracesDir = getWikiFilesDir().resolve("traces");
    private final Path storageStateDir = getWikiFilesDir().resolve("storage-states");
    private BrowserContext browserContext = browser.newContext(PlaywrightSetup.getNewContextOptions());
    private Page currentPage = browserContext.newPage();
    private String storageState;
    private Double timeout;

    /**
     * Sets the timeout for the current browser context.
     *
     * @param timeoutInMilliseconds the timeout in milliseconds.
     *
     */
    public void setTimeout(Double timeoutInMilliseconds) {
        timeout = timeoutInMilliseconds;
        browserContext.setDefaultTimeout(timeout);
    }

    //Page management
    /**
     * Opens a new browser context
     */
    public void openNewContext() {
        browserContext = browser.newContext();
    }

    /**
     * Closes current page
     */
    public void closePage() {
        currentPage.close();
    }

    /**
     * Closes current brower context
     */
    public void closeContext() {
        browserContext.close();
    }

    /**
     * Sets accept handler for next dialog. So when a browser dialog appears it will be automatically accepted.
     */
    public void acceptNextDialog() {
        currentPage.onceDialog(Dialog::accept);
    }

    //     Tab management

    /**
     * Switches to next tab.
     *
     * @throws PlaywrightFitnesseException when no next tab is found
     */
    public void switchToNextTab() {
        if (isLastPage(currentPage)) {
            throw new PlaywrightFitnesseException("Exception: Next tab not found.");
        }
        currentPage = getPageList().get(getPageIndex(currentPage) + 1);
    }

    /**
     * Switches to the previous tab
     *
     * @deprecated renamed. Use {@link PlaywrightFixture#switchToPrecedingTab()}.
     */
    @Deprecated(since = "1.4.0")
    public void switchToPreviousTab() {
        switchToPrecedingTab();
    }

    /**
     * Switches to preceding tab.
     *
     * @throws PlaywrightFitnesseException when no preceding tab is found
     */
    public void switchToPrecedingTab() {
        if (isFirstPage(currentPage)) {
            throw new PlaywrightFitnesseException("Exception: preceding tab not found.");
        }
        currentPage = getPageList().get(getPageIndex(currentPage) - 1);
    }

    /**
     * Closes the currently active tab.
     *
     * @deprecated unneeded convenience method. Use {@link PlaywrightFixture#switchToPrecedingTab} and
     * {@link PlaywrightFixture#closeNextTab()} instead.
     * Also works only when a previous tab is present.
     */
    @Deprecated(since = "1.4.0")
    public void closeCurrentTab() {
        var tabToCloseIndex = getPageIndex(currentPage);
        switchToPreviousTab();
        getPageList().get(tabToCloseIndex).close();
    }

    /**
     * Closes the next tab
     *
     * @throws PlaywrightFitnesseException if no next page is present
     */
    public void closeNextTab() {
        if (isLastPage(currentPage)) {
            throw new PlaywrightFitnesseException("Exception: no next tab found");
        }
        getPageList().get(getPageIndex(currentPage) + 1).close();
    }

    //Cookie management
    /**
     * Sets a cookie on the current browser context
     *
     * @param cookieMap map of cookie key values. The cookie can be created in FitNesse by using the HSAC fixtures
     *                  Map fixture as show below.
     *
     *
     * <p>
     * <pre>
     * {@code
     *
     * |ddt:map fixture                                                         |
     * |name|value|expires         |domain|path|secure|httpOnly|sameSite|cookie?|
     * |test|yes  |2023-12-31 00:00|.c.com|/   |false |false   |true    |$var=  |
     *
     * } </pre>
     *
     * The cookie can then be used in a script like this
     *
     * <pre>
     * {@code
     *
     * |script| playwright fixture |
     * |set cookie | $var          |
     *
     * }
     * </pre>
     *
     */
    public void setCookie(Map<String, String> cookieMap) {
        cookieManager.setCookie(cookieMap, browserContext);
    }

    /**
     * Returns cookies on the current browser context.
     *
     * @return Map of cookies on the current browser context. Key = cookie name and value is the cookie value.
     */
    public Map<String, String> getCookies() {
        return cookieManager.getCookies(browserContext);
    }

    /**
     * Set multiple cookies at once
     * @deprecated creating a list of maps in FitNesse is not very convenient. Adding the cookies to a context one by
     * one using {@link PlaywrightFixture#setCookie(Map)} requires the same or smaller amount of code.
     *
     * @param cookiesList list of cookieMaps
     */
    @Deprecated(since = "1.4.0")
    public void setCookies(List<Map<String, String>> cookiesList) {
        cookieManager.setCookies(cookiesList, browserContext);
    }

    /**
     * Delete all cookies from current browser context.
     */
    public void deleteCookies() {
        cookieManager.deleteCookies(browserContext);
    }

    //Navigation

    /**
     * Navigates to given url from current page. Expects an existing browser context.
     *
     * @param url url of location to navigate to
     */
    public void navigateTo(String url) {
        currentPage.navigate(url);
    }

    /**
     * Opens a new page (tab) and navigates to given url.
     *
     * @param url url of location to navigate to
     */
    public void open(String url) {
        this.currentPage = browserContext.newPage();
        navigateTo(url);
    }

    /**
     * Navigates to the previous page in browser history.
     */
    public void goBack() {
        currentPage.goBack();
    }

    /**
     * Reloads current page.
     */
    public void reloadPage() {
        currentPage.reload();
    }

    //User page interaction
    public void click(String selector) {
        getLocator(selector).click();
    }

    public void clickRoleWithName(String role, String name) {
        currentPage.getByRole(AriaRole.valueOf(role.toUpperCase()), new Page.GetByRoleOptions().setName(name)).click();
    }

    public void clickWithText(String selector, String text) {
        getLocator(selector, new Page.LocatorOptions().setHasText(text)).click();
    }

    public void clickTimes(int times, String selector) {
        for (int i = 0; i < times; i++) {
            this.click(selector);
        }
    }

    /**
     * Clicks an element and then waits for navigation to complete
     *
     * @deprecated use assertions after clicking an element to check if the expected navigation has been completed.
     * @param selector playwright selector of element to click on
     */
    @Deprecated(since = "1.4.0")
    public void clickAndWaitForNavigation(String selector) {
        currentPage.waitForNavigation(() -> this.click(selector));
    }

    public void doubleClick(String selector) {
        getLocator(selector).dblclick();
    }

    public void enterInto(String value, String selector) {
        getLocator(selector).fill(value);
    }

    public void selectLabelIn(String value, String selector) {
        getLocator(selector).selectOption(new SelectOption().setLabel(value));
    }

    public void selectValueIn(String value, String selector) {
        getLocator(selector).selectOption(value);
    }

    public void selectIndexIn(int index, String selector) {
        getLocator(selector).selectOption(new SelectOption().setIndex(index));
    }

    public void selectCheckbox(String selector) {
        getLocator(selector).check();
    }

    public void forceSelectCheckbox(String selector) {
        getLocator(selector).check(new Locator.CheckOptions().setForce(true));
    }

    public void forceDeselectCheckbox(String selector) {
        getLocator(selector).uncheck(new Locator.UncheckOptions().setForce(true));
    }

    //Keyboard interaction
    public void press(String keyOrChord) {
        currentPage.keyboard().press(keyOrChord);
    }

    public void type(String text) {
        currentPage.keyboard().type(text);
    }

    public void typeIn(String text, String selector) {
        getLocator(selector).type(text, new Locator.TypeOptions().setDelay(200));
    }

    //Waiting stuff
    public void waitForUrl(String url) {
        currentPage.waitForURL(url);
    }

    public void waitForVisible(String selector) {
        getLocator(selector).waitFor();
    }

    public void waitForHidden(String selector) {
        getLocator(selector).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
    }

    public void waitForPresentInDom(String selector) {
        getLocator(selector).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED));
    }

    public void waitForNetworkIdle() {
        currentPage.waitForLoadState(LoadState.NETWORKIDLE);
    }

    public void waitForMilliseconds(Double timeout) {
        currentPage.waitForTimeout(timeout);
    }

    //Assertions
    public void assertThatIsVisible(String selector) {
        assertThat(getLocator(selector)).isVisible();
    }

    public void assertThatIsHidden(String selector) {
        assertThat(getLocator(selector)).isHidden();
    }

    public void assertThatIsEnabled(String selector) {
        assertThat(getLocator(selector)).isEnabled();
    }

    public void assertThatIsVisibleWithTimeout(String selector, double timeout) {
        assertThat(getLocator(selector)).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(timeout));
    }

    public void assertThatIsChecked(String selector) {
        assertThat(getLocator(selector)).isChecked();
    }

    public void assertThatIsHiddenWithTimeout(String selector, double timeout) {
        assertThat(getLocator(selector)).isHidden(new LocatorAssertions.IsHiddenOptions().setTimeout(timeout));
    }

    public void assertThatContainsText(String selector, String value) {
        assertThat(getLocator(selector)).containsText(value);
    }

    public void assertThatHasValue(String selector, String value) {
        assertThat(getLocator(selector)).hasValue(value);
    }

    public void assertThatHasValueWithTimeout(String selector, String value, double timeout) {
        assertThat(getLocator(selector)).hasValue(value, new LocatorAssertions.HasValueOptions().setTimeout(timeout));
    }

    public void assertThatPageHasUrl(String url) {
        assertThat(currentPage).hasURL(Pattern.compile(url));
    }

    public void assertThatPageHasUrlWithTimeout(String url, double timeout) {
        assertThat(currentPage).hasURL(Pattern.compile(url), new PageAssertions.HasURLOptions().setTimeout(timeout));
    }

    public void assertThatPageHasNotUrl(String url) {
        assertThat(currentPage).not().hasURL(Pattern.compile(url));
    }

    public void assertThatPageHasNotUrlWithTimeout(String url, double timeout) {
        assertThat(currentPage).not().hasURL(Pattern.compile(url), new PageAssertions.HasURLOptions().setTimeout(timeout));
    }

    public boolean isVisible(String selector) {
        return getLocator(selector).isVisible();
    }

    public boolean isHidden(String selector) {
        return getLocator(selector).isHidden();
    }

    public boolean isEnabled(String selector) {
        return getLocator(selector).isEnabled();
    }

    public boolean isChecked(String selector) {
        return getLocator(selector).isChecked();
    }

    public boolean clickOnOpensTabWithUrl(String selector, String url) {
        return browserContext.waitForPage(() -> getLocator(selector).click(new Locator.ClickOptions())).url().equals(url);
    }

    public boolean clickOnAndWaitOpensTabWithUrl(String selector, String url) {
        browserContext.waitForPage(() -> getLocator(selector).click()).waitForURL(url);
        return true;
    }

    //Value retrieval
    public String valueOf(String selector) {
        String result;
        switch (getLocator(selector).evaluate("e => e.tagName", null, new Locator.EvaluateOptions()).toString().toLowerCase()) {
            case "input":
            case "textarea":
            case "select":
                result = getLocator(selector).inputValue();
                break;
            case "button":
            case "option":
            case "text":
                result = getLocator(selector).innerHTML();
                break;
            default:
                result = getLocator(selector).innerText();
        }
        return result;
    }

    public String valueOfAttributeForSelector(String attributeName, String selector) {
        return currentPage.getAttribute(selector, attributeName);
    }

    public String selectedLabelIn(String selector) {
        var selectedIndex = currentPage.evalOnSelector(selector, "e => e.selectedIndex");
        return currentPage.evalOnSelector(selector, String.format("e => e.options[%s].innerText", selectedIndex)).toString();
    }

    public String normalizedValueOf(String selector) {
        return getNormalizedText(valueOf(selector));
    }

    public String getNormalizedText(String text) {
        return (text != null) ? Pattern.compile("[" + "\u00a0" + "\\s]+").matcher(text).replaceAll(" ").trim() : null;
    }

    public String getUrl() {
        return currentPage.url();
    }

    /**
     * Returns a string containing the page title
     *
     * @return the page title of the current page
     * @since 1.4.0
     */
    public String getTitle() {
        return currentPage.title();
    }

    //Taking screenshots
    public String takeScreenshot(String baseName) {
        var screenshotFile = screenshotsDir.resolve(baseName);
        currentPage.screenshot(new Page.ScreenshotOptions().setPath(screenshotFile).setFullPage(true));
        return getScreenshotLink(screenshotFile);
    }

    private String getScreenshotLink(Path screenshotFile) {
        return String.format("<a href=\"%1$s\" target=\"_blank\" style=\"border-style:none\"><img src=\"%1$s\" title=\"%2$s\" height=\"%3$s\" style=\"border-style:none\"/></a>",
                getWikiPath(screenshotFile), screenshotFile.getFileName(), 400);
    }

    /**
     * Converts a file path into a relative wiki path, if the path is insides the wiki's 'files' section.
     *
     * @param path path to file.
     * @return relative URL pointing to the file (so a hyperlink to it can be created).
     */
    public Path getWikiPath(Path path) {
        return path.startsWith(getWikiFilesDir()) ? path.subpath(1, (path.getNameCount())) : path;
    }

    //Debugging

    public String takeScreenshot() {
        return takeScreenshot(String.valueOf(Instant.now().toEpochMilli()));
    }

    /**
     * Calling pause() starts the PlayWright Inspector, but only when NOT running headless!
     * Scripts recorded in the Playwright Inspector can not be used in FitNesse, but the inspector might be useful
     * when finding and debugging selectors.
     */
    public void debug() {
        currentPage.pause();
    }

    public String getCurrentPage() {
        return currentPage.toString();
    }

    public String getPages() {
        return getPageList().toString();
    }

    public String getContexts() {
        return browser.contexts().toString();
    }

    public String getCurrentContext() {
        return browserContext.toString();
    }

    //Manage re-usable state
    public void saveStorageState() {
        storageState = browserContext.storageState();
    }

    public void saveStorageStateToFile(String name) {
        browserContext.storageState(new BrowserContext.StorageStateOptions().setPath(Paths.get(storageStateDir + "/" + name + ".json")));
    }

    public String getStorageState() {
        return storageState;
    }

    public void openNewContextWithSavedStorageState() {
        browserContext = browser.newContext(PlaywrightSetup.getNewContextOptions().setStorageState(getStorageState()));
        setTimeout(timeout);
    }

    public void openNewContextWithSavedStorageStateFromFile(String name) {
        try {
            browserContext = browser.newContext(PlaywrightSetup.getNewContextOptions().setStorageStatePath(Paths.get(storageStateDir + "/" + name + ".json")));
        } catch (Exception e) {
            throw new PlaywrightFitnesseException(e.getMessage());
        }
    }

    //Tracing
    public void startTrace() {
        browserContext.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(false));
    }

    public void saveTrace(String name) {
        browserContext.tracing().stop(new Tracing.StopOptions().setPath(Paths.get(tracesDir + "/" + name + ".zip")));
    }

    public void openTrace(String name) throws IOException, InterruptedException {
        String[] args = {"show-trace", tracesDir + "/" + name + ".zip"};
        CLI.main(args);
    }

    //Network
    public void openAndWaitForResponseFromUrl(String openUrl, String responseUrl) {
        this.currentPage = browserContext.newPage();
        currentPage.waitForResponse(responseUrl, () -> navigateTo(openUrl));
    }

    public void clickAndWaitForResponseFromUrl(String selector, String url) {
        currentPage.waitForResponse(url, () -> this.click(selector));
    }

    public void clickAndWaitForRequestFinished(String selector) {
        currentPage.waitForRequestFinished(() -> this.click(selector));
    }


    public void selectAndWaitForResponseFromUrl(String selector, String url) {
        currentPage.waitForResponse(Pattern.compile(url), () -> this.selectCheckbox(selector));
    }

    public void selectAndWaitForRequestFinished(String selector) {
        currentPage.waitForRequestFinished(() -> this.selectCheckbox(selector));
    }

    public void enterIntoAndWaitForResponseFromUrl(String value, String selector, String url) {
        currentPage.waitForResponse(url, () -> this.enterInto(value, selector));
    }

    public void waitForResponseFromUrlMatching(String urlRegex) {
        currentPage.waitForResponse(Pattern.compile(urlRegex), () -> {
        });
    }

    public void setUrlToReturnBody(String url, String body) {
        browserContext.route(url, route -> route.fulfill(new Route.FulfillOptions().setBody(body)));
    }

    //Helper methods

    /**
     * Helper function returning a Locator object based on a selector string and an locationOptions object.
     *
     * @param selector       playwright selector string
     * @param locatorOptions playwright locator options
     * @return locator of an element on the current page
     */
    private Locator getLocator(String selector, Page.LocatorOptions locatorOptions) {
        return currentPage.locator(selector, locatorOptions);
    }

    /**
     * Helper function returning a Locator object based on a selector string.
     *
     * @param selector playwright selector string
     * @return locator of an element on the current page
     */
    private Locator getLocator(String selector) {
        return currentPage.locator(selector);
    }

    /**
     * Helper function to get the list of pages present in current browser context.
     *
     * @return list of pages in current browser context
     */
    private List<Page> getPageList() {
        return browserContext.pages();
    }

    /**
     * Returns index of given Page in Pages list of given BrowserContext.
     *
     * @param page page object. Assumed is that this page is present in the pages list on current browser context
     *             {@link PlaywrightFixture#getPageList()}}
     * @return index of the given page in the list of pages on current browser context. Returns -1 if not found.
     */
    private Integer getPageIndex(Page page) {
        return getPageList().indexOf(page);
    }

    /**
     * Indicates if page is the first page in list of page of current browser context
     *
     * @param page page to check
     * @return true when given page is first page in browser contexts pages list
     */
    private boolean isFirstPage(Page page) {
        return getPageIndex(page) == 0;
    }

    /**
     * Indicaties if the given page is the last page in the list of pages for the current browser context.
     *
     * @param page page to check
     * @return boolean true is the page is the last in the list of pages for the current browser context
     */
    private boolean isLastPage(Page page) {
        return getPageIndex(page) == getPageList().size() - 1;
    }


    @Override
    protected Throwable handleException(Throwable t) {
        return (currentPage == null || browserContext == null) ? t : new PlaywrightFitnesseException(getExceptionMessageWithScreenshot(t));
    }

    protected String getExceptionMessageWithScreenshot(Throwable t) {
        return String.format("%s<div style=\"border-style:none\"><p style=\"border-style:none\" >%s</p>%s</div>%s",
                PRETTY_PRINT_TAG_START, StringEscapeUtils.escapeHtml4(t.getMessage()), takeScreenshot(), PRETTY_PRINT_TAG_END);
    }
}
