package com.evolve.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

import com.evolve.handler.CartHandler;

@Configuration
public class CartRouter {

    @Bean
    public RouterFunction<ServerResponse> cartRoutes(CartHandler handler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/carts"), handler::getAll)
                .andRoute(RequestPredicates.GET("/carts/{id}"), handler::getById)
                .andRoute(RequestPredicates.GET("/carts/user/{userId}"), handler::getByUserId)
                .andRoute(RequestPredicates.POST("/carts/user/{userId}"), handler::create)
                .andRoute(RequestPredicates.PUT("/carts/{id}"), handler::update)
                .andRoute(RequestPredicates.DELETE("/carts/{id}"), handler::delete);
    }
}