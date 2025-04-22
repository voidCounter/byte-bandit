package com.bytebandit.gateway.filter;

import com.bytebandit.gateway.config.PermittedRoutesConfig;
import com.bytebandit.gateway.enums.CookieKey;
import com.bytebandit.gateway.exception.CookieNotFoundException;
import com.bytebandit.gateway.service.CustomUserDetailsService;
import com.bytebandit.gateway.service.TokenService;
import com.bytebandit.gateway.utils.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filters incoming requests to authenticate via JWT in cookies and add X-User-Id header. Bypasses
 * authentication for permitted routes (e.g., public endpoints).
 */
@Component
public class AuthCookieFilter extends OncePerRequestFilter {
    
    private final List<String> permittedRoutes;
    private final TokenService tokenService;
    private final CustomUserDetailsService userDetailsService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    @Value("${app.access-token-expiration}")
    private long accessTokenExpirationTime;
    
    @Value("${app.refresh-token-expiration}")
    private long refreshTokenExpirationTime;
    
    /**
     * Constructs a new instance of the AuthCookieFilter.
     *
     * @param permittedRoutesConfig the configuration object containing permitted routes
     * @param tokenService          the service responsible for handling token-related operations
     * @param userDetailsService    the service responsible for retrieving user details
     */
    public AuthCookieFilter(PermittedRoutesConfig permittedRoutesConfig, TokenService tokenService,
                            CustomUserDetailsService userDetailsService) {
        this.permittedRoutes = permittedRoutesConfig.getRoutes();
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        logger.debug("Processing request: " + request.getServletPath());
        
        if (isPermittedRoute(request.getServletPath())) {
            logger.debug("Permitted route, bypassing authentication");
            filterChain.doFilter(request, response);
            return;
        }
        
        String accessToken = getAccessToken(request);
        if (accessToken == null) {
            throw new CookieNotFoundException("Access token cookie not found");
        }
        
        String username = tokenService.extractUsername(accessToken);
        if (username == null) {
            filterChain.doFilter(request, response);
            return;
        }
        
        UserDetails user = userDetailsService.loadUserByUsername(username);
        UUID userId = processToken(accessToken, user, request, response);
        if (userId == null) {
            logger.debug("No userId, proceeding without X-User-Id");
            filterChain.doFilter(request, response);
            return;
        }
        
        HttpServletRequest wrappedRequest = wrapRequestWithUserId(request, userId);
        logger.debug("Added X-User-Id: " + userId);
        filterChain.doFilter(wrappedRequest, response);
    }
    
    /**
     * Checks if the request path matches any permitted routes.
     */
    private boolean isPermittedRoute(String path) {
        return permittedRoutes.stream().anyMatch(route -> pathMatcher.match(route, path));
    }
    
    /**
     * Retrieves the access token from the request's cookie.
     */
    private String getAccessToken(HttpServletRequest request) {
        return CookieUtil.getCookieValue(request, CookieKey.ACCESS_TOKEN.getKey());
    }
    
    /**
     * Processes the token: validates it or handles expiration by issuing a new token. Returns the
     * userId if authentication is successful.
     */
    private UUID processToken(String accessToken, UserDetails user, HttpServletRequest request,
                              HttpServletResponse response) {
        try {
            tokenService.isValidToken(accessToken, user);
            setAuthentication(user, request);
            return tokenService.extractUserId(accessToken);
        } catch (ExpiredJwtException e) {
            logger.warn("Expired JWT, generating new token");
            return handleExpiredToken(accessToken, user, response);
        }
    }
    
    /**
     * Sets the authentication context for a valid token.
     */
    private void setAuthentication(UserDetails user, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(token);
    }
    
    /**
     * Handles expired tokens by generating a new access token and refresh token.
     */
    private UUID handleExpiredToken(String accessToken, UserDetails user,
                                    HttpServletResponse response) {
        UUID userId = tokenService.extractUserId(accessToken);
        String newAccessToken = tokenService.generateToken(user, accessTokenExpirationTime, userId);
        tokenService.generateAndSaveRefreshToken(user, refreshTokenExpirationTime, accessToken);
        CookieUtil.setCookie(response, CookieKey.ACCESS_TOKEN.getKey(), newAccessToken, false,
            24 * 60 * 60, "/", false);
        return userId;
    }
    
    /**
     * Wraps the request to include the X-User-Id header.
     */
    private HttpServletRequest wrapRequestWithUserId(HttpServletRequest request, UUID userId) {
        return new HttpServletRequestWrapper(request) {
            @Override
            public String getHeader(String name) {
                if ("X-User-Id".equalsIgnoreCase(name)) {
                    return userId.toString();
                }
                return super.getHeader(name);
            }
            
            @Override
            public Enumeration<String> getHeaders(String name) {
                if ("X-User-Id".equalsIgnoreCase(name)) {
                    return Collections.enumeration(Collections.singletonList(userId.toString()));
                }
                return super.getHeaders(name);
            }
            
            @Override
            public Enumeration<String> getHeaderNames() {
                List<String> names = Collections.list(super.getHeaderNames());
                names.add("X-User-Id");
                return Collections.enumeration(names);
            }
        };
    }
}