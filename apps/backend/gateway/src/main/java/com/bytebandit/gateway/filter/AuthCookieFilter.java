package com.bytebandit.gateway.filter;

import com.bytebandit.gateway.config.PermittedRoutesConfig;
import com.bytebandit.gateway.enums.CookieKey;
import com.bytebandit.gateway.exception.CookieNotFoundException;
import com.bytebandit.gateway.exception.InvalidTokenException;
import com.bytebandit.gateway.service.CustomUserDetailsService;
import com.bytebandit.gateway.service.TokenService;
import com.bytebandit.gateway.utils.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.micrometer.core.instrument.config.validate.Validated;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

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
     * Constructor for AuthCookieFilter.
     *
     * @param permittedRoutesConfig the configuration for permitted routes
     * @param tokenService          the token service
     * @param userDetailsService    the user details service
     */
    public AuthCookieFilter(
        PermittedRoutesConfig permittedRoutesConfig,
        TokenService tokenService,
        CustomUserDetailsService userDetailsService
    ) {
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
        logger.info("permittedRoutes: " + permittedRoutes);
        logger.info("request.getServletPath(): " + request.getServletPath());
        
        String servletPath = request.getServletPath();
        boolean isPermitted = permittedRoutes.stream()
            .anyMatch(route -> pathMatcher.match(route, servletPath));
        if (isPermitted) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String accessToken = CookieUtil.getCookieValue(
            request,
            CookieKey.ACCESS_TOKEN.getKey());
        
        if (accessToken == null) {
            throw new CookieNotFoundException("Access token cookie not found");
        }
        
        
        try {
            final String username = tokenService.extractUsername(accessToken);
            UserDetails user = userDetailsService.loadUserByUsername(username);
            if (tokenService.isTokenExpired(accessToken)) {
                throw new ExpiredJwtException(null, null, "Token expired");
            }
            if (!tokenService.isValidToken(accessToken, user)) {
                throw new InvalidTokenException("Invalid token");
            }
            UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
                );
            token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(token);
        } catch (ExpiredJwtException e) {
            final UUID userId = tokenService.extractUserId(accessToken);
            final String username = tokenService.extractUsername(accessToken);
            UserDetails user = userDetailsService.loadUserByUsername(username);
            final String newAccessToken = tokenService.generateToken(
                user,
                accessTokenExpirationTime,
                userId
            );
            tokenService.generateAndSaveRefreshToken(
                user,
                refreshTokenExpirationTime,
                accessToken
            );
            CookieUtil.setCookie(
                response,
                CookieKey.ACCESS_TOKEN.getKey(),
                newAccessToken,
                false,
                24 * 60 * 60,
                "/",
                false
            );
        } catch (InvalidTokenException e) {
            return;
        }
        filterChain.doFilter(request, response);
    }
}
