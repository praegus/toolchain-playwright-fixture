package nl.praegus.fitnesse.slim.fixtures.playwright.exceptions;

import nl.praegus.fitnesse.slim.fixtures.playwright.enums.PlaywrightBrowserType;

import java.util.Arrays;

public class UnsupportedBrowserException extends RuntimeException {

    public UnsupportedBrowserException() {
        super("Browser not supported. Currently supported browsers are: " + Arrays.asList(PlaywrightBrowserType.values()));
    }
}
