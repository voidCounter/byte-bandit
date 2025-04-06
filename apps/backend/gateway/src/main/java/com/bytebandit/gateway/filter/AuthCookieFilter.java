package com.bytebandit.gateway.filter;

import com.bytebandit.gateway.config.PermittedRoutesConfig;
import com.bytebandit.gateway.enums.CookieKey;
import com.bytebandit.gateway.exception.CookieNotFoundException;
import com.bytebandit.gateway.service.CustomUserDetailsService;
import com.bytebandit.gateway.service.TokenService;
import com.bytebandit.gateway.utils.CookieUtil;
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
import org.springframework.web.filter.OncePerRequestFilter;

public class AuthCookieFilter extends OncePerRequestFilter {

    private final List<String> permittedRoutes;
    private final TokenService tokenService;
    private final CustomUserDetailsService userDetailsService;

    @Value("${app.access-token-expiration}")
    private long accessTokenExpirationTime;

    @Value("${app.refresh-token-expiration}")
    private long refreshTokenExpirationTime;

    /**
     * Constructor for AuthCookieFilter.
     *
     * @param permittedRoutesConfig the configuration for permitted routes
     * @param tokenService the token service
     * @param userDetailsService the user details service
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

        if (permittedRoutes.contains(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String accessToken = CookieUtil.getCookieValue(
            request,
            CookieKey.ACCESS_TOKEN.getKey());

        if (accessToken == null) {
            throw new CookieNotFoundException("Access token cookie not found");
        }

        final String username = tokenService.extractUsername(accessToken);

        if (username != null) {
            UserDetails user = userDetailsService.loadUserByUsername(username);
            if (tokenService.isValidToken(accessToken, user)) {
                UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                    );
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(token);
            } else if (tokenService.isTokenExpired(accessToken)) {

                final UUID userId = tokenService.extractUserId(accessToken);

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
            }
        }
    }
}
