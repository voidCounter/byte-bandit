package com.bytebandit.fileservice.utils;

import com.bytebandit.fileservice.exception.UnauthenticatedException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lib.core.enums.CustomHttpHeader;

public class HttpHeaderUtils {

    private HttpHeaderUtils() {
    }

    /**
     * Retrieves the user ID from the request header.
     *
     * @param servletRequest the HTTP servlet request
     *
     * @return the user ID from the header
     * @throws UnauthenticatedException if the user ID header is missing
     */
    public static String getUserIdHeader(HttpServletRequest servletRequest) {
        String userIdHeader = servletRequest.getHeader(CustomHttpHeader.USER_ID.getValue());

        if (userIdHeader == null) {
            throwError();
        }

        try {
            UUID.fromString(userIdHeader);
        } catch (IllegalArgumentException ex) {
            throwError();
        }

        return userIdHeader;
    }

    private static void throwError() {
        throw new UnauthenticatedException("User ID header is missing");
    }
}
