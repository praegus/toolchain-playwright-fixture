package nl.praegus.fitnesse.slim.fixtures.playwright.enums;

import java.util.Arrays;
import java.util.Optional;

public enum PlaywrightBrowserType {
    CHROMIUM("chromium"),
    FIREFOX("firefox"),
    WEBKIT("webkit");

    private final String name;

    PlaywrightBrowserType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Optional<PlaywrightBrowserType> findByName(String name) {
        return Arrays.stream(values()).filter(playwrightBrowserType -> playwrightBrowserType.getName().equalsIgnoreCase(name)).findFirst();
    }
}
