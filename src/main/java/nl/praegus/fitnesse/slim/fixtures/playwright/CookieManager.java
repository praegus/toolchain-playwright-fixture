package nl.praegus.fitnesse.slim.fixtures.playwright;

import com.microsoft.playwright.options.Cookie;
import nl.praegus.fitnesse.slim.fixtures.playwright.converters.CookieConverter;

public class CookieManager {
    private String key;
    private String name;
    private String value;
    private String domain;
    private String path;
    private String expires;
    private Boolean httpOnly = false;
    private Boolean secure = false;
    private String sameSite;

    private final CookieConverter converter;

    public CookieManager() {
        fitnesse.slim.converters.ConverterRegistry.addConverter(Cookie.class, new CookieConverter());
        converter = (CookieConverter) fitnesse.slim.converters.ConverterRegistry.getConverterForClass(Cookie.class);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public void setHttpOnly(Boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public void setSameSite(String sameSite) {
        this.sameSite = sameSite;
    }

    public void execute() {
            CookieJar.addCookie(key, converter.getObject(this.toString()));
    }
    public Cookie cookie() {
        return converter.getObject(this.toString());
    }

    public String toString() {
        return String.format("name=%s;value=%s;domain=%s;path=%s;expires=%s;secure=%s;httpOnly=%s;sameSite=%s",
                name, value, domain, path, expires, secure, httpOnly, sameSite);
    }
}
