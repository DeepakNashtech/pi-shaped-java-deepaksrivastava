package com.evolve.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

import com.evolve.handler.OrderHandler;

@Configuration
public class OrderRouter {

    @Bean
    public RouterFunction<ServerResponse> orderRoutes(OrderHandler handler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/orders"), handler::getAll)
                .andRoute(RequestPredicates.GET("/orders/{id}"), handler::getById)
                .andRoute(RequestPredicates.GET("/orders/user/{userId}"), handler::getByUserId)
                .andRoute(RequestPredicates.POST("/orders/user/{userId}"), handler::create)
                .andRoute(RequestPredicates.PUT("/orders/{id}"), handler::update)
                .andRoute(RequestPredicates.DELETE("/orders/{id}"), handler::delete);
    }
}

