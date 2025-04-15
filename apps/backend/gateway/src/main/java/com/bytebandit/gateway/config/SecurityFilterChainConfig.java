package com.bytebandit.gateway.config;

import com.bytebandit.gateway.filter.AuthCookieFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {
    private final List<String> permittedRoutes;
    private final AuthCookieFilter authCookieFilter;

    @Autowired
    public SecurityFilterChainConfig(PermittedRoutesConfig permittedRoutesConfig, AuthCookieFilter authCookieFilter) {
        this.permittedRoutes = permittedRoutesConfig.getRoutes();
        this.authCookieFilter = authCookieFilter;
    }

    /**
     * This method sets up the security configuration by customizing the CSRF protection mechanism,
     * defining authorization rules, and managing session policies. It includes the following:
     * <li>CSRF tokens are stored as HTTP-only cookies using a {@link CookieCsrfTokenRepository}
     * .</li>
     * <li> CSRF protection via {@link SpaCsrfTokenRequestHandler}.</li>
     * <li>Authorization rules to permit access to specific routes while securing all other
     * requests.</li>
     * <li>Stateless session management policy to ensure no server-side session is maintained.</li>
     *
     * @param http the {@link HttpSecurity} object to configure security settings such as CSRF
     *             protection, authorization rules, and session management.
     * @return the fully configured {@link SecurityFilterChain} for the application.
     * @throws Exception in case of any security configuration errors.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))
                .authorizeHttpRequests(
                        req -> req.requestMatchers(getAllPermittedRoutes(permittedRoutes)).permitAll()
                                .anyRequest().authenticated())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authCookieFilter, UsernamePasswordAuthenticationFilter.class).build();
    }

    private RequestMatcher getAllPermittedRoutes(List<String> permittedRoutes) {
        List<RequestMatcher> matchers =
                permittedRoutes.stream().map(AntPathRequestMatcher::new).collect(Collectors.toList());
        return new OrRequestMatcher(matchers);
    }

    private static final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {
        private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
        private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

        /**
         * Handles the CSRF token for the incoming request and response by employing BREACH
         * protection using the {@link XorCsrfTokenRequestAttributeHandler} to securely process and
         * render the token.
         *
         * @param request   the {@link HttpServletRequest} associated with the current client
         *                  request
         * @param response  the {@link HttpServletResponse} associated with the current client
         *                  request
         * @param csrfToken a {@link Supplier} that supplies the
         *                  {@link org.springframework.security.web.csrf.CsrfToken}, allowing
         *                  deferred loading and subsequent processing of the token
         */
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response,
                           Supplier<org.springframework.security.web.csrf.CsrfToken> csrfToken) {
            /*
             * Always use XorCsrfTokenRequestAttributeHandler to provide BREACH protection of
             * the CsrfToken when it is rendered in the response body.
             */
            this.xor.handle(request, response, csrfToken);
            /*
             * Render the token value to a cookie by causing the deferred token to be loaded.
             */
            csrfToken.get();
        }

        /**
         * Resolves the CSRF token value from the provided request, using the appropriate strategy
         * based on whether the token is present in the request header or as a parameter.
         *
         * @param request   the {@link HttpServletRequest} used to extract the CSRF token value
         * @param csrfToken the {@link org.springframework.security.web.csrf.CsrfToken} object
         *                  containing details about the CSRF token (e.g., header name and token
         *                  value)
         * @return the resolved CSRF token value as a {@link String}, or {@code null} if it cannot
         * be resolved.
         */
        @Override
        public String resolveCsrfTokenValue(HttpServletRequest request,
                                            org.springframework.security.web.csrf
                                                    .CsrfToken csrfToken) {
            String headerValue = request.getHeader(csrfToken.getHeaderName());
            /*
             * If the request contains a request header, use CsrfTokenRequestAttributeHandler
             * to resolve the CsrfToken. This applies when a single-page application includes
             * the header value automatically, which was obtained via a cookie containing the
             * raw CsrfToken.
             *
             * In all other cases (e.g. if the request contains a request parameter), use
             * XorCsrfTokenRequestAttributeHandler to resolve the CsrfToken. This applies
             * when a server-side rendered form includes the _csrf request parameter as a
             * hidden input.
             */
            return (StringUtils.hasText(headerValue) ? this.plain : this.xor).resolveCsrfTokenValue(
                    request, csrfToken);
        }
    }
}
