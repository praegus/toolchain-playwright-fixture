package nl.praegus.fitnesse.slim.fixtures.playwright.converters;

import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.SameSiteAttribute;
import fitnesse.slim.converters.ConverterBase;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static java.time.LocalDateTime.parse;

public class CookieConverter extends ConverterBase<Cookie> {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);

    @Override
    public String toString(Cookie c) {
        return String.format("name=%s;value=%s;domain=%s;path=%s;expires=%s;secure=%s;httpOnly=%s;sameSite=%s",
                c.name, c.value, c.domain, c.path, epochToString(c.expires), c.secure, c.httpOnly, c.sameSite);
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
            cookie.setExpires(timestampToEpochTime(cookieMap.get("expires")));
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

    private double timestampToEpochTime(String timestamp) {
        return parse(timestamp, dateTimeFormatter).toEpochSecond(ZoneOffset.UTC);
    }

    private String epochToString(double epochTime) {
        return dateTimeFormatter.format(Instant.ofEpochSecond((long)epochTime));
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
