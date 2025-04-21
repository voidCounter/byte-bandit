package com.bytebandit.gateway.utils;

import com.bytebandit.gateway.exception.CookieNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.WebUtils;

/** Utility class for managing cookies in HTTP requests and responses. */
public class CookieUtil {

    /**
     * Sets a cookie in the HTTP response with the specified parameters.
     *
     * @param response the HttpServletResponse object to set the cookie in
     * @param name the name of the cookie
     * @param value the value of the cookie
     * @param httpOnly whether the cookie is HTTP-only
     * @param maxAge the maximum age of the cookie in seconds
     * @param path the path for which the cookie is valid
     * @param secure whether the cookie is secure (only sent over HTTPS)
     */
    public static void setCookie(
        HttpServletResponse response,
        String name,
        String value,
        boolean httpOnly,
        int maxAge,
        String path,
        boolean secure
    ) {
        Cookie cookie = createCookie(name, value, httpOnly, maxAge, path, secure);
        response.addCookie(cookie);
    }

    /**
     * Retrieves the value of a cookie with the specified name from the HTTP request.
     *
     * @param request the HttpServletRequest object to retrieve the cookie from
     * @param name the name of the cookie
     * @return the value of the cookie
     * @throws CookieNotFoundException if the cookie is not found
     */
    public static String getCookieValue(
        HttpServletRequest request,
        String name
    ) {
        Cookie cookie = WebUtils.getCookie(request, name);
        if (cookie == null) {
            throw new CookieNotFoundException("Cookie with provided name " + name + " not found");
        }
        return cookie.getValue();
    }

    /**
     * Deletes a cookie with the specified name from the HTTP response.
     *
     * @param response the HttpServletResponse object to delete the cookie from
     * @param name the name of the cookie
     */
    public static void deleteCookie(HttpServletResponse response, String name) {
        response.addCookie(createCookie(name, "", false, 0, "/", false));
    }

    private static Cookie createCookie(
        String name,
        String value,
        boolean httpOnly,
        int maxAge,
        String path,
        boolean secure
    ) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(httpOnly);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        cookie.setSecure(secure);
        return cookie;
    }
}
