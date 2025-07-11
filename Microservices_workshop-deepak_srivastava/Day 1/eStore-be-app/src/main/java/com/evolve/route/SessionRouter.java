package com.evolve.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.RequestPredicates;

import com.evolve.handler.SessionHandler;

@Configuration
public class SessionRouter {
    @Bean
    public RouterFunction<ServerResponse> sessionRoutes(SessionHandler handler) {
        return RouterFunctions
            .route(RequestPredicates.GET("/login"), handler::login)
            .andRoute(RequestPredicates.GET("/profile"), handler::profile)
            .andRoute(RequestPredicates.POST("/logout"), handler::logout);
    }
}
