package nl.praegus.fitnesse.slim.fixtures.playwright.converters;

import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.SameSiteAttribute;
import fitnesse.slim.converters.ConverterBase;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static java.time.LocalDateTime.parse;

public class CookieConverter extends ConverterBase<Cookie> {
    @Override
    public String toString(Cookie c) {
        return String.format("name=%s;value=%s;domain=%s;path=%s;expires=%s;secure=%s;httpOnly=%s;sameSite=%s",
                c.name, c.value, c.domain, c.path, c.expires, c.secure, c.httpOnly, c.sameSite);
    }

    @Override
    public Cookie getObject(String cookieString) {
        var cookieMap = getCookieMap(cookieString);

        var cookie = new Cookie(getCookieMap(cookieString).get("name"), getCookieMap(cookieString).get("value"));

        if (cookieMap.get("domain") != null) {
            cookie.setDomain(cookieMap.get("domain"));
        }

        if (cookieMap.get("path") != null) {
            cookie.setPath(cookieMap.get("path"));
        }

        if (cookieMap.get("expires") != null) {
            cookie.setExpires(timestampToEpoch(cookieMap.get("expires")));
        }

        if (cookieMap.get("httpOnly") != null) {
            cookie.setHttpOnly(Boolean.parseBoolean(cookieMap.get("httpOnly")));
        }

        if (cookieMap.get("secure") != null) {
            cookie.setSecure(Boolean.parseBoolean(cookieMap.get("secure")));
        }

        if (cookieMap.get("sameSite") != null) {
            cookie.setSameSite(SameSiteAttribute.valueOf(cookieMap.get("sameSite")));
        }

        return cookie;
    }

    private double timestampToEpoch(String timestamp) {
        return parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).toEpochSecond(ZoneOffset.UTC);
    }

    private Map<String, String> getCookieMap(String s) {
        HashMap<String, String> map = new HashMap<>();
        for (String cookieData : s.split(";")) {
            String[] kv = cookieData.split("=");
            map.put(kv[0], kv[1]);
        }
        return map;
    }
}
