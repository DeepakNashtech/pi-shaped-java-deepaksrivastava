package com.evolve.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

import com.evolve.handler.CartItemHandler;

@Configuration
public class CartItemRouter {

    @Bean
    public RouterFunction<ServerResponse> cartItemRoutes(CartItemHandler handler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/carts/{cartId}/items"), handler::list)
                .andRoute(RequestPredicates.POST("/carts/{cartId}/items"), handler::add)
                .andRoute(RequestPredicates.DELETE("/carts/items/{itemId}"), handler::remove);
    }
}
