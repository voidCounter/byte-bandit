package com.bytebandit.gateway.config;

import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;

@Configuration
public class RoutingConfig {

    @Bean
    public RouterFunction<ServerResponse> userServiceRouterFunction() {
        return GatewayRouterFunctions.route("user-service")
                .route(GatewayRequestPredicates.path("/api/v1/user/**"), HandlerFunctions.http())
                .filter(lb("user-service"))
                .before(BeforeFilterFunctions.stripPrefix(3))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fileServiceRouterFunction() {
        return GatewayRouterFunctions.route("file-service")
                .route(GatewayRequestPredicates.path("/api/v1/file/**"), HandlerFunctions.http())
                .filter(lb("file-service"))
                .before(BeforeFilterFunctions.stripPrefix(3))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> syncServiceRouterFunction() {
        return GatewayRouterFunctions.route("sync-service")
                .route(GatewayRequestPredicates.path("/api/v1/sync/**"), HandlerFunctions.http())
                .filter(lb("sync-service"))
                .before(BeforeFilterFunctions.stripPrefix(3))
                .build();
    }
}
