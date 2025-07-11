package com.evolve.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.evolve.handler.UserHandler;

@Configuration
public class UserRouter {

   @Bean(name="userRoutes")
    public RouterFunction<ServerResponse> route(UserHandler handler) {
        return RouterFunctions
            .route(RequestPredicates.GET("/users"), handler::getAll)
            .andRoute(RequestPredicates.GET("/users/{id}"), handler::getById)
            .andRoute(RequestPredicates.POST("/users"), handler::create)
            .andRoute(RequestPredicates.PUT("/users/{id}"), handler::update)
            .andRoute(RequestPredicates.DELETE("/users/{id}"), handler::delete);
    }
}

