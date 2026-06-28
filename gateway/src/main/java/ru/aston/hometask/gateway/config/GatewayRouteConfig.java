package ru.aston.hometask.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class GatewayRouteConfig {
    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return route("user-service")
                .GET("/api/users/**", http())
                .POST("/api/users/**", http())
                .PUT("/api/users/**", http())
                .DELETE("/api/users/**", http())
                .filter(lb("user-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> notificationServiceRoute() {
        return route("notification-service")
                .POST("/api/notifications/**", http())
                .filter(lb("notification-service"))
                .build();
    }
}