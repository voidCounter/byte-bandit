package com.bytebandit.gateway.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.bytebandit.gateway.config.PermittedRoutesConfig;
import com.bytebandit.gateway.exception.CookieNotFoundException;
import com.bytebandit.gateway.model.UserEntity;
import com.bytebandit.gateway.service.CustomUserDetailsService;
import com.bytebandit.gateway.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthCookieFilterTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private PermittedRoutesConfig permittedRoutesConfig;

    @InjectMocks
    private AuthCookieFilter authFilter;

    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private final MockFilterChain mockFilterChain = new MockFilterChain();

    private final String accessToken = "mocked.token.value";
    private final String username = "test-user@mail.com";
    private final UUID userId = UUID.randomUUID();
    private final UserEntity mockUser = new UserEntity();

    /**
     * Setup method to initialize the test environment.
     */
    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(authFilter, "accessTokenExpirationTime", 3600L);
        ReflectionTestUtils.setField(authFilter, "refreshTokenExpirationTime", 86400L);
        mockUser.setEmail(username);
        mockUser.setId(userId);
    }

    /**
     * Test method to verify that the filter correctly processes a request with a valid.
     */
    @Test
    void shouldPassThroughPermittedRoute()
        throws ServletException, IOException {
        request.setServletPath("/public");
        when(permittedRoutesConfig.getRoutes()).thenReturn(List.of("/public"));

        authFilter = new AuthCookieFilter(
            permittedRoutesConfig,
            tokenService,
            customUserDetailsService
        );

        authFilter.doFilterInternal(request, response, mockFilterChain);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
    }

    /**
     * Test method to verify that the filter correctly processes a
     * request with a valid access token.
     */
    @Test
    void shouldThrowExceptionWhenCookieMissing() {
        request.setServletPath("/secure");

        when(permittedRoutesConfig.getRoutes()).thenReturn(List.of("/public"));
        authFilter = new AuthCookieFilter(
            permittedRoutesConfig,
            tokenService,
            customUserDetailsService
        );

        assertThrows(
            CookieNotFoundException.class,
            () -> authFilter.doFilterInternal(request, response, mockFilterChain)
        );
    }

    /**
     * Test method to verify that the filter correctly processes a
     * request with a valid access token.
     */
    @Test
    void shouldAuthenticateWithValidToken() throws ServletException, IOException {
        request.setServletPath("/secure");
        Cookie cookie = new Cookie("access_token", accessToken);
        request.setCookies(cookie);

        when(permittedRoutesConfig.getRoutes()).thenReturn(List.of("/public"));
        when(tokenService.extractUsername(accessToken)).thenReturn(username);
        when(customUserDetailsService.loadUserByUsername(username)).thenReturn(
            mockUser);
        when(tokenService.isValidToken(accessToken, mockUser)).thenReturn(true);

        authFilter = new AuthCookieFilter(
            permittedRoutesConfig,
            tokenService,
            customUserDetailsService
        );
        authFilter.doFilterInternal(request, response, mockFilterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(
            SecurityContextHolder.getContext().getAuthentication().getName()
        ).isEqualTo(username);
    }

    /**
     * Test method to verify that the filter correctly processes a
     * request with an expired access token.
     */
    @Test
    void shouldRefreshExpiredToken() throws ServletException, IOException {
        request.setServletPath("/secure");
        request.setCookies(new Cookie("access_token", accessToken));

        when(permittedRoutesConfig.getRoutes()).thenReturn(List.of("/public"));
        when(tokenService.extractUsername(accessToken)).thenReturn(username);
        when(customUserDetailsService.loadUserByUsername(username))
            .thenReturn(mockUser);
        doThrow(ExpiredJwtException.class).when(tokenService).isValidToken(accessToken, mockUser);
        when(tokenService.extractUserId(accessToken)).thenReturn(userId);
        when(tokenService.generateToken(eq(mockUser), anyLong(), eq(userId)))
            .thenReturn("new-token");

        authFilter = new AuthCookieFilter(
            permittedRoutesConfig,
            tokenService,
            customUserDetailsService
        );
        authFilter.doFilterInternal(request, response, mockFilterChain);

        Cookie updatedCookie = response.getCookie("access_token");
        assertThat(updatedCookie).isNotNull();
        assertThat(updatedCookie.getValue()).isEqualTo("new-token");
    }
}
