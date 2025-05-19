package lib.core.utils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lib.core.exception.CookieNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.WebUtils;


@ExtendWith(SpringExtension.class)
@ActiveProfiles({"default", "test"})
class CookieUtilsTest {
    
    private static final String TEST_COOKIE_NAME = "testCookie";
    private static final String TEST_COOKIE_VALUE = "testValue";
    
    /**
     * Test the setCookie method with all parameters.
     */
    @Test
    void testSetCookieSuccess() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        CookieUtil.setCookie(response, TEST_COOKIE_NAME, TEST_COOKIE_VALUE, true, 3600, "/", true);
        
        verify(response, times(1)).addCookie(argThat(cookie ->
            cookie.getName().equals(TEST_COOKIE_NAME)
                && cookie.getValue().equals(TEST_COOKIE_VALUE)
                && cookie.isHttpOnly()
                && cookie.getSecure()
                && cookie.getMaxAge() == 3600
                && cookie.getPath().equals("/")
        ));
    }
    
    /**
     * Test the setCookie method with non-secure and non-HTTP-only cookie.
     */
    @Test
    void testSetCookieNonSecureHttpOnly() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        CookieUtil.setCookie(response, TEST_COOKIE_NAME, TEST_COOKIE_VALUE, true, 1200,
            "/custom-path", false);
        
        verify(response, times(1)).addCookie(argThat(cookie ->
            cookie.getName().equals(TEST_COOKIE_NAME)
                && cookie.getValue().equals(TEST_COOKIE_VALUE)
                && cookie.isHttpOnly()
                && !cookie.getSecure()
                && cookie.getMaxAge() == 1200
                && cookie.getPath().equals("/custom-path")
        ));
    }
    
    /**
     * Test the setCookie method with default values for httpOnly and secure.
     */
    @Test
    void testGetCookieValueSuccess() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        when(request.getCookies()).thenReturn(new Cookie[] {new Cookie(
            "unrelatedCookie", "someValue"
        )});
        
        Exception exception = assertThrows(CookieNotFoundException.class, () ->
            CookieUtil.getCookieValue(request, TEST_COOKIE_NAME)
        );
        
        assertTrue(exception.getMessage().contains("Cookie with provided name "
            + TEST_COOKIE_NAME + " not found"));
        
    }
    
    /**
     * Test the getCookieValue method when the cookie is not found.
     */
    @Test
    void testGetCookieValueNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        when(WebUtils.getCookie(request, TEST_COOKIE_NAME)).thenReturn(null);
        
        Exception exception = assertThrows(CookieNotFoundException.class, () ->
            CookieUtil.getCookieValue(request, TEST_COOKIE_NAME)
        );
        
        assertTrue(exception.getMessage()
            .contains("Cookie with provided name " + TEST_COOKIE_NAME + " not found"));
    }
    
    /**
     * Test the deleteCookie method.
     */
    @Test
    void testDeleteCookie() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        CookieUtil.deleteCookie(response, TEST_COOKIE_NAME);
        
        verify(response, times(1)).addCookie(argThat(cookie ->
            cookie.getName().equals(TEST_COOKIE_NAME)
                && cookie.getValue().isEmpty()
                && cookie.getMaxAge() == 0
                && cookie.getPath().equals("/")
                && cookie.isHttpOnly()
                && !cookie.getSecure()
        ));
    }
    
    /**
     * Test the deleteCookie method with default values for httpOnly and secure.
     */
    @Test
    void testDeleteCookieWithDefaultValues() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        CookieUtil.deleteCookie(response, TEST_COOKIE_NAME);
        
        verify(response).addCookie(argThat(cookie ->
            cookie.getName().equals(TEST_COOKIE_NAME)
                && "".equals(cookie.getValue())
                && cookie.getMaxAge() == 0
                && "/".equals(cookie.getPath())
        ));
    }
    
    /**
     * Test the setCookie method with a null value.
     */
    @Test
    void testSetCookieEdgeCaseMaxAgeZero() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        CookieUtil.setCookie(response, TEST_COOKIE_NAME, TEST_COOKIE_VALUE,
            true, 0, "/", true);
        
        verify(response, times(1)).addCookie(argThat(cookie ->
            cookie.getName().equals(TEST_COOKIE_NAME)
                && cookie.getValue().equals(TEST_COOKIE_VALUE)
                && cookie.getMaxAge() == 0
                && cookie.getPath().equals("/")
                && cookie.isHttpOnly()
                && cookie.getSecure()
        ));
    }
    
    /**
     * Test the setCookie method with a negative max age.
     */
    @Test
    void testSetCookieEdgeCaseNegativeMaxAge() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        CookieUtil.setCookie(response, TEST_COOKIE_NAME, TEST_COOKIE_VALUE,
            false, -1, "/", true);
        
        verify(response, times(1)).addCookie(argThat(cookie ->
            cookie.getName().equals(TEST_COOKIE_NAME)
                && cookie.getValue().equals(TEST_COOKIE_VALUE)
                && cookie.getMaxAge() == -1
                && cookie.getPath().equals("/")
                && !cookie.isHttpOnly()
                && cookie.getSecure()
        ));
    }
    
    /**
     * Test that checks multiple cookies.
     */
    @Test
    void testMultipleCookiesSet() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        CookieUtil.setCookie(response, "cookie1", "value1", true, 500, "/path1", true);
        CookieUtil.setCookie(response, "cookie2", "value2", false, 1000, "/path2", false);
        
        verify(response, times(1)).addCookie(argThat(cookie ->
            cookie.getName().equals("cookie1")
                && cookie.getValue().equals("value1")
                && cookie.isHttpOnly()
                && cookie.getMaxAge() == 500
                && "/path1".equals(cookie.getPath())
                && cookie.getSecure()
        ));
        
        verify(response, times(1)).addCookie(argThat(cookie ->
            cookie.getName().equals("cookie2")
                && cookie.getValue().equals("value2")
                && !cookie.isHttpOnly()
                && cookie.getMaxAge() == 1000
                && "/path2".equals(cookie.getPath())
                && !cookie.getSecure()
        ));
    }
}