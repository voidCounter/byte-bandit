package com.bytebandit.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {

    private final List<String> permittedRoutes = List.of(
            "/api/v1/user/login",
            "/api/v1/user/register"
    );

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // don't need CSRF for stateless sessions
                .authorizeHttpRequests(
                    (req) -> req.requestMatchers(
                        getAllPermittedRoutes(permittedRoutes)
                ).permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement((session) -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ))
                .build();
    }

    private RequestMatcher getAllPermittedRoutes(List<String> permittedRoutes) {
        List<RequestMatcher> matchers = permittedRoutes.stream()
                .map(AntPathRequestMatcher::new)
                .collect(Collectors.toList());
        return new OrRequestMatcher(matchers);
    }

}
