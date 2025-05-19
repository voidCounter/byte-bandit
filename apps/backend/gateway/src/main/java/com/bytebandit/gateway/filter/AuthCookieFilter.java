package com.bytebandit.gateway.filter;

import com.bytebandit.gateway.config.PermittedRoutesConfig;
import com.bytebandit.gateway.enums.CookieKey;
import com.bytebandit.gateway.exception.InvalidTokenException;
import com.bytebandit.gateway.service.CustomUserDetailsService;
import com.bytebandit.gateway.service.TokenService;
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
import lib.core.enums.CustomHttpHeader;
import lib.core.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
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
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        logger.debug("permittedRoutes: " + permittedRoutes);
        logger.debug("request.getServletPath(): " + request.getServletPath());
        
        if (isPermittedRoute(request.getServletPath())) {
            logger.debug("Permitted route, bypassing authentication");
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }
        
        String accessToken = getAccessToken(request);
        String username = tokenService.extractUsername(accessToken);
        UserDetails user = userDetailsService.loadUserByUsername(username);
        UUID userId = processToken(accessToken, user, request, response);
        HttpServletRequest wrappedRequest = wrapRequestWithUserId(request, userId);
        logger.debug("Added X-User-Id: " + userId);
        filterChain.doFilter(wrappedRequest, response);
    }
    
    private boolean isPermittedRoute(String path) {
        return permittedRoutes.stream().anyMatch(route -> pathMatcher.match(route, path));
    }
    
    private String getAccessToken(HttpServletRequest request) {
        return CookieUtil.getCookieValue(request, CookieKey.ACCESS_TOKEN.getKey());
    }
    
    /**
     * Processes the access token to authenticate the user and handle token expiration.
     *
     * @param accessToken the access token to process
     * @param user        the user details
     * @param request     the HTTP request
     * @param response    the HTTP response
     *
     * @return the user ID extracted from the token
     */
    UUID processToken(String accessToken, UserDetails user, HttpServletRequest request,
                      HttpServletResponse response) {
        try {
            UUID userId = tokenService.extractUserId(accessToken);
            if (tokenService.isValidToken(accessToken, user)) {
                setAuthentication(user, request);
                return userId;
            }
            
            if (tokenService.isTokenExpired(accessToken)) {
                userId = handleExpiredToken(accessToken, user, response);
                setAuthentication(user, request);
                return userId;
            }
            throw new InvalidTokenException("Invalid token");
        } catch (InvalidTokenException e) {
            throw new InvalidTokenException(e.getMessage());
        }
    }
    
    private void setAuthentication(UserDetails user, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(token);
    }
    
    private UUID handleExpiredToken(String accessToken, UserDetails user,
                                    HttpServletResponse response) {
        UUID userId = tokenService.extractUserId(accessToken);
        String newAccessToken = tokenService.generateToken(user, accessTokenExpirationTime, userId);
        tokenService.generateAndSaveRefreshToken(user, refreshTokenExpirationTime, accessToken);
        CookieUtil.setCookie(response, CookieKey.ACCESS_TOKEN.getKey(), newAccessToken, true,
            24 * 60 * 60, "/", false);
        return userId;
    }
    
    private HttpServletRequest wrapRequestWithUserId(HttpServletRequest request, UUID userId) {
        return new HttpServletRequestWrapper(request) {
            @Override
            public String getHeader(String name) {
                if (CustomHttpHeader.USER_ID.getValue().equalsIgnoreCase(name)) {
                    return userId.toString();
                }
                return super.getHeader(name);
            }
            
            @Override
            public Enumeration<String> getHeaders(String name) {
                if (CustomHttpHeader.USER_ID.getValue().equalsIgnoreCase(name)) {
                    return Collections.enumeration(Collections.singletonList(userId.toString()));
                }
                return super.getHeaders(name);
            }
            
            @Override
            public Enumeration<String> getHeaderNames() {
                List<String> names = Collections.list(super.getHeaderNames());
                if (!names.contains(CustomHttpHeader.USER_ID.getValue())) {
                    names.add(CustomHttpHeader.USER_ID.getValue());
                }
                return Collections.enumeration(names);
            }
        };
    }
}