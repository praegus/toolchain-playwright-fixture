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
     * Closes current browser context
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
     *                  <p>
     *                  <pre>
     *                  {@code
     *
     *                  |ddt:map fixture                                                         |
     *                  |name|value|expires         |domain|path|secure|httpOnly|sameSite|cookie?|
     *                  |test|yes  |2023-12-31 00:00|.c.com|/   |false |false   |true    |$var=  |
     *
     *                  } </pre>
     *                  <p>
     *                  The cookie can then be used in a script like this
     *
     *                  <pre>
     *                  {@code
     *
     *                  |script| playwright fixture |
     *                  |set cookie | $var          |
     *
     *                  }
     *                  </pre>
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
     *
     * @param cookiesList list of cookieMaps
     * @deprecated creating a list of maps in FitNesse is not very convenient. Adding the cookies to a context one by
     * one using {@link PlaywrightFixture#setCookie(Map)} requires the same or smaller amount of code.
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

    /**
     * Clicks on an element
     *
     * @param selector playwright selector to locate element to click on.
     */
    public void click(String selector) {
        getLocator(selector).click();
    }

    /**
     * Click on an element located by ARIA role and accessible name.
     *
     * @param role ARIA role
     * @param name ARIA accessible name
     */
    public void clickRoleWithName(String role, String name) {
        currentPage.getByRole(AriaRole.valueOf(role.toUpperCase()), new Page.GetByRoleOptions().setName(name)).click();
    }

    /**
     * Click on element with given text.
     *
     * @param selector playwright selector to locate element to click on
     * @param text     (sub)string required to be present in the element or one of its children.
     */
    public void clickWithText(String selector, String text) {
        getLocator(selector, new Page.LocatorOptions().setHasText(text)).click();
    }

    /**
     * Click on an element the given amount of times.
     *
     * @param times    number of times to click
     * @param selector playwright selector to locate element to click on
     */
    public void clickTimes(int times, String selector) {
        for (int i = 0; i < times; i++) {
            this.click(selector);
        }
    }

    /**
     * Clicks an element and then waits for navigation to complete.
     *
     * @param selector playwright selector to locate element to click on
     * @deprecated use assertions after clicking an element to check if the expected navigation has been completed.
     */
    @Deprecated(since = "1.4.0")
    public void clickAndWaitForNavigation(String selector) {
        currentPage.waitForNavigation(() -> this.click(selector));
    }

    /**
     * Double-click on given element.
     *
     * @param selector playwright selector to locate element to click on
     */
    public void doubleClick(String selector) {
        getLocator(selector).dblclick();
    }

    /**
     * Fills an element with given value.
     *
     * @param value    value to enter
     * @param selector playwright selector to locate element to enter data in
     */
    public void enterInto(String value, String selector) {
        getLocator(selector).fill(value);
    }

    /**
     * Selects option by given label in a select element
     *
     * @param value    string of label to select
     * @param selector playwright selector to locate element to select option in
     */
    public void selectLabelIn(String value, String selector) {
        getLocator(selector).selectOption(new SelectOption().setLabel(value));
    }

    /**
     * Selects option by given value in select element
     *
     * @param value    string of value to select
     * @param selector playwright selector to locate element to select option in
     */
    public void selectValueIn(String value, String selector) {
        getLocator(selector).selectOption(value);
    }

    /**
     * Selects option by index in select element
     *
     * @param index    index of option to select
     * @param selector playwright selector to locate element to select option in
     */
    public void selectIndexIn(int index, String selector) {
        getLocator(selector).selectOption(new SelectOption().setIndex(index));
    }

    /**
     * Makes sure a checkbox or radio button is selected. So it selects when unselected and does nothing when already selected
     *
     * @param selector Playwright selector to locate element to select
     */
    public void selectCheckbox(String selector) {
        getLocator(selector).check();
    }

    /**
     * Makes sure a checkbox or radiobutton is selected, but bypasses the <a href="https://playwright.dev/java/docs/actionability">actionability</a> checks
     *
     * @param selector Playwright selector to locate element to select
     */
    public void forceSelectCheckbox(String selector) {
        getLocator(selector).check(new Locator.CheckOptions().setForce(true));
    }

    /**
     * Makes sure a checkbox or radiobutton is not selected, but bypasses the <a href="https://playwright.dev/java/docs/actionability">actionability</a> checks
     *
     * @param selector Playwright selector to locate element to deselect
     */
    public void forceDeselectCheckbox(String selector) {
        getLocator(selector).uncheck(new Locator.UncheckOptions().setForce(true));
    }

    //Keyboard interaction

    /**
     * Presses given key or combination of keys.
     *
     * @param keyOrChord key or keys to press
     */
    public void press(String keyOrChord) {
        currentPage.keyboard().press(keyOrChord);
    }

    /**
     * Type given text on current page. Where the text ends up depends on element currently in focus.
     *
     * @param text string to type
     */
    public void type(String text) {
        currentPage.keyboard().type(text);
    }

    /**
     * Type given text in an element.
     *
     * @param text     string to type
     * @param selector playwright selector to locate the element to type into
     */
    public void typeIn(String text, String selector) {
        getLocator(selector).type(text, new Locator.TypeOptions().setDelay(200));
    }

    //Waiting stuff

    /**
     * Waits for a navigation to an url to be complete.
     *
     * @param url url navigate
     */
    public void waitForUrl(String url) {
        currentPage.waitForURL(url);
    }

    /**
     * Waits for an element to be visible.
     *
     * @param selector Playwright selector to locate element to wait for
     */
    public void waitForVisible(String selector) {
        getLocator(selector).waitFor();
    }

    /**
     * Waits for an element to be hidden.
     *
     * @param selector Playwright selector to locate element to wait for
     */
    public void waitForHidden(String selector) {
        getLocator(selector).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
    }

    /**
     * Waits for an element to be present in the DOM.
     *
     * @param selector Playwright selector to locate element to wait for
     */
    public void waitForPresentInDom(String selector) {
        getLocator(selector).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED));
    }

    /**
     * Waits until there are no network connections for at least 500 ms.
     */
    public void waitForNetworkIdle() {
        currentPage.waitForLoadState(LoadState.NETWORKIDLE);
    }

    /**
     * Wait for a given amount of time. Only use for debugging, flaky!
     *
     * @param timeout timeout in milliseconds
     */
    public void waitForMilliseconds(Double timeout) {
        currentPage.waitForTimeout(timeout);
    }

    //Assertions

    /**
     * Asserts that an element is visible.
     *
     * @param selector Playwright selector to locate element to be visible.
     */
    public void assertThatIsVisible(String selector) {
        assertThat(getLocator(selector)).isVisible();
    }

    /**
     * Asserts that an element is hidden.
     *
     * @param selector Playwright selector to locate element to be hidden.
     */
    public void assertThatIsHidden(String selector) {
        assertThat(getLocator(selector)).isHidden();
    }

    /**
     * Asserts that an element is enabled.
     *
     * @param selector Playwright selector to locate element to be enabled.
     */
    public void assertThatIsEnabled(String selector) {
        assertThat(getLocator(selector)).isEnabled();
    }

    /**
     * Asserts that an element is visible before the given timeout expires
     *
     * @param selector Playwright selector to locate element to be visible
     * @param timeout  timeout in milliseconds
     */
    public void assertThatIsVisibleWithTimeout(String selector, double timeout) {
        assertThat(getLocator(selector)).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(timeout));
    }

    /**
     * Asserts that an element is checked
     *
     * @param selector Playwright selector to locate element to be checked
     */
    public void assertThatIsChecked(String selector) {
        assertThat(getLocator(selector)).isChecked();
    }

    /**
     * Asserts that an element is hidden before the given timeout expires
     *
     * @param selector Playwright selector to locate element to be hidden
     * @param timeout  timeout in milliseconds
     */
    public void assertThatIsHiddenWithTimeout(String selector, double timeout) {
        assertThat(getLocator(selector)).isHidden(new LocatorAssertions.IsHiddenOptions().setTimeout(timeout));
    }

    /**
     * Asserts that an element contains a given text.
     *
     * @param selector Playwright selector to locate element that contains text
     * @param value string that should be present in element
     */
    public void assertThatContainsText(String selector, String value) {
        assertThat(getLocator(selector)).containsText(value);
    }

    /**
     * Asserts that an element has a given value.
     * @param selector Playwright selector to locate element that should have a value
     * @param value value that should be present in element
     */
    public void assertThatHasValue(String selector, String value) {
        assertThat(getLocator(selector)).hasValue(value);
    }

    /**
     * Asserts that an element has a given value before the timeout expires.
     *
     * @param selector Playwright selector to locate element that should have a value
     * @param value value that should be present in element
     * @param timeout timeout in milliseconds
     */
    public void assertThatHasValueWithTimeout(String selector, String value, double timeout) {
        assertThat(getLocator(selector)).hasValue(value, new LocatorAssertions.HasValueOptions().setTimeout(timeout));
    }

    /**
     * Asserts that the page has a given url.
     *
     * @param url expected url
     */
    public void assertThatPageHasUrl(String url) {
        assertThat(currentPage).hasURL(Pattern.compile(url));
    }

    /**
     * Asserts that the page has a given url before the timeout expires.
     *
     * @param url expected url
     * @param timeout timeout in milliseconds
     */
    public void assertThatPageHasUrlWithTimeout(String url, double timeout) {
        assertThat(currentPage).hasURL(Pattern.compile(url), new PageAssertions.HasURLOptions().setTimeout(timeout));
    }

    /**
     * Asserts that the page does not have a given url.
     *
     * @param url url the page should not have
     */
    public void assertThatPageHasNotUrl(String url) {
        assertThat(currentPage).not().hasURL(Pattern.compile(url));
    }

    /**
     * Asserts that the page doen not have a given url before the timeout expires.
     *
     * @param url url the page should not have
     * @param timeout timeout in milliseconds
     */
    public void assertThatPageHasNotUrlWithTimeout(String url, double timeout) {
        assertThat(currentPage).not().hasURL(Pattern.compile(url), new PageAssertions.HasURLOptions().setTimeout(timeout));
    }

    /**
     * Checks if an element is visible.
     *
     * @param selector Playwright selector to locating the element to check.
     * @return boolean indicating if the element is visible
     */
    public boolean isVisible(String selector) {
        return getLocator(selector).isVisible();
    }

    /**
     * Checks if an element is hidden.
     *
     * @param selector Playwright selector to locating the element to check.
     * @return boolean indicating if the element is hidden
     */
    public boolean isHidden(String selector) {
        return getLocator(selector).isHidden();
    }

    /**
     * Checks if an element is enabled.
     *
     * @param selector Playwright selector to locating the element to check.
     * @return boolean indicating if the element is enabled
     */
    public boolean isEnabled(String selector) {
        return getLocator(selector).isEnabled();
    }

    /**
     * Checks if an element is checked.
     *
     * @param selector Playwright selector to locating the element to check.
     * @return boolean indicating if the element is checked
     */
    public boolean isChecked(String selector) {
        return getLocator(selector).isChecked();
    }

    /**
     * Checks if clicking an element opens a new tab with given url.
     *
     * @param selector Playwright selector to locating the element to click
     * @param url url to be opened in new tab
     * @return boolean indicating if the new tab was opened with the given url
     */
    public boolean clickOnOpensTabWithUrl(String selector, String url) {
        return browserContext.waitForPage(() -> getLocator(selector).click(new Locator.ClickOptions())).url().equals(url);
    }

    /**
     * Checks that clicking an element opens a new tab with given url, else an exception is thrown.
     *
     * @param selector Playwright selector to locate the element
     * @param url url to be opened in new tab
     * @return boolean indicating that the new tab was opened with the given url
     */
    public boolean clickOnAndWaitOpensTabWithUrl(String selector, String url) {
        browserContext.waitForPage(() -> getLocator(selector).click()).waitForURL(url);
        // if waitForURL() did not throw, assume that the page has the expected url
        return true;
    }

    //Value retrieval

    /**
     * Gets the value of an element.
     *
     * @param selector Playwright selector to locate the element to get the value from
     * @return value of the given element
     */
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

    /**
     * Gets the value of a given attribute for an element.
     *
     * @param attributeName attribute to get the value from
     * @param selector Playwright selector to locate the element to get the attribute value from
     * @return value of the given attribute
     */
    public String valueOfAttributeForSelector(String attributeName, String selector) {
        return currentPage.getAttribute(selector, attributeName);
    }

    /**
     * Gets the label that is selected in an element.
     *
     * @param selector Playwright selector to locate the element to get the label from
     * @return selected label
     */
    public String selectedLabelIn(String selector) {
        var selectedIndex = currentPage.evalOnSelector(selector, "e => e.selectedIndex");
        return currentPage.evalOnSelector(selector, String.format("e => e.options[%s].innerText", selectedIndex)).toString();
    }

    /**
     * Gets the for whitespace normalized value of an element.
     *
     * @param selector Playwright selector to locate the element to get the normalized value from
     * @return for whitespace normalized value of the element
     */
    public String normalizedValueOf(String selector) {
        return getNormalizedText(valueOf(selector));
    }

    /**
     * Gets the for whitespace normalized value of a string.
     *
     * @param text string to normalize
     * @return normalized input string
     */
    public String getNormalizedText(String text) {
        return (text != null) ? Pattern.compile("[" + "\u00a0" + "\\s]+").matcher(text).replaceAll(" ").trim() : null;
    }

    /**
     * Gets url of the current page.
     *
     * @return url of the current page
     */
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

    /**
     * Takes and stores a screenshot with a given name.
     *
     * @param baseName name of the screenshot without extension.
     * @return location of the screenshot as a html link
     */
    public String takeScreenshot(String baseName) {
        var screenshotFile = screenshotsDir.resolve(baseName);
        currentPage.screenshot(new Page.ScreenshotOptions().setPath(screenshotFile).setFullPage(true));
        return getScreenshotLink(screenshotFile);
    }

    /**
     * Takes and stores a screenshot with timestamp of current time as name.
     *
     * @return location of the screenshot as a html link
     */
    public String takeScreenshot() {
        return takeScreenshot(String.valueOf(Instant.now().toEpochMilli()));
    }

    /**
     * Gets the html link for a given screenshot path.
     *
     * @param screenshotFilePath
     * @return html link to screenshot
     */
    private String getScreenshotLink(Path screenshotFilePath) {
        return String.format("<a href=\"%1$s\" target=\"_blank\" style=\"border-style:none\"><img src=\"%1$s\" title=\"%2$s\" height=\"%3$s\" style=\"border-style:none\"/></a>",
                getWikiPath(screenshotFilePath), screenshotFilePath.getFileName(), 400);
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

    /**
     * Calling pause() starts the PlayWright Inspector, but only when NOT running headless!
     * Scripts recorded in the Playwright Inspector can not be used in FitNesse, but the inspector might be useful
     * when finding and debugging selectors.
     */
    public void debug() {
        currentPage.pause();
    }

    /**
     * Gets current page object. Useful for fixture debugging.
     *
     * @return string representation of current page object
     */
    public String getCurrentPage() {
        return currentPage.toString();
    }

    /**
     * Gets list of all pages in current browser context. Useful for fixture debugging.
     *
     * @return string representation of current pages list
     */
    public String getPages() {
        return getPageList().toString();
    }

    /**
     * Get all browser contexts. Useful for fixture debugging.
     *
     * @return string representation of all browser context.
     */
    public String getContexts() {
        return browser.contexts().toString();
    }

    /**
     * Get current browser context.
     *
     * @return string representation of current browser context
     */
    public String getCurrentContext() {
        return browserContext.toString();
    }

    //Manage re-usable state

    /**
     * Saves current storage state in memory.
     */
    public void saveStorageState() {
        storageState = browserContext.storageState();
    }

    /**
     * Saves current storage state as a json file
     *
     * @param name name of the json file without extension
     */
    public void saveStorageStateToFile(String name) {
        browserContext.storageState(new BrowserContext.StorageStateOptions().setPath(Paths.get(storageStateDir + "/" + name + ".json")));
    }

    /**
     * Gets the saved storage state from memory.
     *
     * @return storage state
     */
    public String getStorageState() {
        return storageState;
    }

    /**
     * Open a new browser context with a saved storage state
     */
    public void openNewContextWithSavedStorageState() {
        browserContext = browser.newContext(PlaywrightSetup.getNewContextOptions().setStorageState(getStorageState()));
        setTimeout(timeout);
    }

    /**
     * Opens a new browser context with a storage state from file.
     *
     * @param name name of the storage state file
     */
    public void openNewContextWithSavedStorageStateFromFile(String name) {
        try {
            browserContext = browser.newContext(PlaywrightSetup.getNewContextOptions().setStorageStatePath(Paths.get(storageStateDir + "/" + name + ".json")));
        } catch (Exception e) {
            throw new PlaywrightFitnesseException(e.getMessage());
        }
    }

    //Tracing

    /**
     * Starts a trace
     */
    public void startTrace() {
        browserContext.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(false));
    }

    /**
     * Save a trace to file with a given name.
     *
     * @param name name of the trace file
     */
    public void saveTrace(String name) {
        browserContext.tracing().stop(new Tracing.StopOptions().setPath(Paths.get(tracesDir + "/" + name + ".zip")));
    }

    /**
     * Opens a trace file.
     *
     * @deprecated Does not work properly. Not all images are loaded. Using
     * mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="show-trace trace.zip" is preferred.
     *
     * @param name name of the trace to open.
     * @throws IOException
     * @throws InterruptedException
     */
    @Deprecated(since = "1.4.0")
    public void openTrace(String name) throws IOException, InterruptedException {
        String[] args = {"show-trace", tracesDir + "/" + name + ".zip"};
        CLI.main(args);
    }

    //Network

    /**
     * Opens a page and waits for a response from another server.
     *
     * @param openUrl url to open.
     * @param responseUrl url that should be called during loading of the page.
     */
    public void openAndWaitForResponseFromUrl(String openUrl, String responseUrl) {
        this.currentPage = browserContext.newPage();
        currentPage.waitForResponse(responseUrl, () -> navigateTo(openUrl));
    }

    /**
     * Clicks an element and waits for a response from a given url.
     *
     * @param selector Playwright selector to locate element to click on.
     * @param url url that should respond after clicking
     */
    public void clickAndWaitForResponseFromUrl(String selector, String url) {
        currentPage.waitForResponse(url, () -> this.click(selector));
    }

    /**
     * Clicks an element and waits for the resulting request to finish.
     *
     * @param selector Playwright selector to locate element to click on.
     */
    public void clickAndWaitForRequestFinished(String selector) {
        currentPage.waitForRequestFinished(() -> this.click(selector));
    }

    /**
     * Selects an element and waits for a response from a given url.
     *
     * @param selector Playwright selector to locate element to select.
     * @param url url that should respond after selecting
     */
    public void selectAndWaitForResponseFromUrl(String selector, String url) {
        currentPage.waitForResponse(Pattern.compile(url), () -> this.selectCheckbox(selector));
    }

    /**
     * Selects an element and wait for the resulting request to finish.
     *
     * @param selector Playwright selector to locate element to select.
     */
    public void selectAndWaitForRequestFinished(String selector) {
        currentPage.waitForRequestFinished(() -> this.selectCheckbox(selector));
    }

    /**
     * Enters and value into an element and waits for a response from a given url.
     * @param value value to enter
     * @param selector Playwright selector to locate element to enter the value into.
     * @param url url that should respond after entering the value
     */
    public void enterIntoAndWaitForResponseFromUrl(String value, String selector, String url) {
        currentPage.waitForResponse(url, () -> this.enterInto(value, selector));
    }

    /**
     * Waits for a response for an url matching a regex.
     *
     * @param urlRegex regex of the url that should respond.
     */
    public void waitForResponseFromUrlMatching(String urlRegex) {
        currentPage.waitForResponse(Pattern.compile(urlRegex), () -> {
        });
    }

    /**
     * Mocks an endpoint to return a given body.
     *
     * @param url url to mock
     * @param body response body to return when mocked url is called.
     */
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