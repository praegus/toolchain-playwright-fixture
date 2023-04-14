package nl.praegus.fitnesse.slim.fixtures.playwright.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.SameSiteAttribute;
import org.junit.jupiter.api.Test;

class CookieConverterTest {
    private final String name = "Room";
    private final String value = "42";
    private final String domain = ".praegus.nl";
    private final String path = "/";
    private final double expiresEpochTime = 1681457828;
    private final String expires = "2023-04-14 07:37:08";
    private final Boolean secure = true;
    private final Boolean httpOnly = true;
    private final SameSiteAttribute sameSiteAttribute = SameSiteAttribute.LAX;

    private final Cookie cookie = new Cookie(name, value)
            .setDomain(domain)
            .setPath(path)
            .setExpires(expiresEpochTime)
            .setSecure(secure)
            .setHttpOnly(httpOnly)
            .setSameSite(sameSiteAttribute);

    private final String cookieString = String.format("name=%s;value=%s;domain=%s;path=%s;expires=%s;secure=%s;httpOnly=%s;sameSite=%s",
            name, value, domain, path, expires, secure, httpOnly, sameSiteAttribute.name());

    private final CookieConverter cookieConverter = new CookieConverter();

    Cookie actualObject = cookieConverter.getObject(cookieString);

    /**
     * Method under test: {@link CookieConverter#toString(Cookie)}
     */
    @Test
    void toString_returns_string_representation_of_cookie() {
        assertEquals(cookieString, cookieConverter.toString(cookie));
    }

    /**
     * Method under test: {@link CookieConverter#getObject(String)}
     */
    @Test
    void cookie_object_has_right_name() {
        assertEquals(name, actualObject.name);
    }

    @Test
    void cookie_object_has_right_value() {
        assertEquals(value, actualObject.value);
    }

    @Test
    void cookie_object_has_right_domain() {
        assertEquals(domain, actualObject.domain);
    }

    @Test
    void cookie_object_has_right_path() {
        assertEquals(path, actualObject.path);
    }

    @Test
    void cookie_object_has_right_expiry_date() {
        assertEquals(expiresEpochTime, actualObject.expires);
    }

    @Test
    void cookie_object_has_secure_set() {
        assertEquals(secure, actualObject.secure);
    }

    @Test
    void cookie_object_has_http_only_set() {
        assertEquals(httpOnly, actualObject.httpOnly);
    }

    @Test
    void cookie_object_has_same_site_set() {
        assertEquals(sameSiteAttribute, actualObject.sameSite);
    }

}

