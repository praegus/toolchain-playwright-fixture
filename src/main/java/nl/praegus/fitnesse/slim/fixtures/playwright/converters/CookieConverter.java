package nl.praegus.fitnesse.slim.fixtures.playwright.converters;

import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.SameSiteAttribute;
import fitnesse.slim.converters.ConverterBase;
import nl.praegus.fitnesse.slim.fixtures.playwright.exceptions.PlaywrightFitnesseException;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Converter classes convert a string argument from the FitNesse wiki into a Java object.
 * What type to convert the string to is determined by the argument type of the fixture method that is used in the
 * wiki.
 *
 * <pre> Example:
 * {@code
 *        If a string is passed to a setCookie method in the someFixture fixture
 *
 *       | script     | someFixture         |
 *       | set cookie | name=Room; value=42 |
 *
 *       And in the someFixture fixture the method is defined like:
 *
 *       public void setCookie(Cookie cookie) { ... }
 *
 *       Then FitNesse wil use this converters getObject method to convert the string to a
 *       Cookie Object so the object can be used in the fixture cookie and the conversion
 *       from/to a string representation of the cookie is
 *       nicely encapsulated.
 * }
 * </pre>
 * <p>
 * This particular converter can be used to convert string into {@link Cookie} objects.
 */
public class CookieConverter extends ConverterBase<Cookie> {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    @Override
    public String toString(Cookie cookie) {
        return String.format("name=%s;value=%s;domain=%s;path=%s;expires=%s;secure=%s;httpOnly=%s;sameSite=%s",
                cookie.name, cookie.value, cookie.domain, cookie.path, epochToString(cookie.expires), cookie.secure, cookie.httpOnly, cookie.sameSite);
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

    private long timestampToEpochTime(String timestamp) {
        try {
           return Instant.parse(timestamp).getEpochSecond();
        } catch (DateTimeParseException exception) {
            throw new PlaywrightFitnesseException("Provided timestamp should be formatted like: 2023-04-14T12:40:43Z");
        }
    }

    private String epochToString(double epochTime) {
        return dateTimeFormatter.format(Instant.ofEpochSecond((long) epochTime));
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
