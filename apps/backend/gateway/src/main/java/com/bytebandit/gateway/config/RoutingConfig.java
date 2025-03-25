package com.bytebandit.gateway.config;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;

import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class RoutingConfig {

    private RouterFunction<ServerResponse> createServiceRoute(String serviceName,
                                                              String pathPattern) {
        return GatewayRouterFunctions.route(serviceName)
            .route(GatewayRequestPredicates.path(pathPattern), HandlerFunctions.http())
            .filter(lb(serviceName))
            .before(BeforeFilterFunctions.stripPrefix(3))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> userServiceRouterFunction() {
        return createServiceRoute("user-service", "/api/v1/user/**");
    }

    @Bean
    public RouterFunction<ServerResponse> fileServiceRouterFunction() {
        return createServiceRoute("file-service", "/api/v1/file/**");
    }

    @Bean
    public RouterFunction<ServerResponse> syncServiceRouterFunction() {
        return createServiceRoute("sync-service", "/api/v1/sync/**");
    }
}