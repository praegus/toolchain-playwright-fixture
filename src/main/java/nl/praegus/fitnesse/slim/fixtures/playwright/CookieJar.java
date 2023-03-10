package nl.praegus.fitnesse.slim.fixtures.playwright;

import com.microsoft.playwright.options.Cookie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

final class CookieJar {
    private static HashMap<String, Cookie> cookieMap = new HashMap<>();

    public static void addCookie(String key, Cookie cookie) {
        cookieMap.put(key, cookie);
    }

    public static Cookie getCookie(String key) {
        return cookieMap.get(key);
    }

    public static List<Cookie> getCookies() {
        return new ArrayList<>(cookieMap.values());
    }
}
