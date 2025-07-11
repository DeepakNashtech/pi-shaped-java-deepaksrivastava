package com.evolve.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CombinedRouter {

    @Bean(name = "mainRouter")
    public RouterFunction<ServerResponse> mainRouter(
        @Qualifier("userRoutes") RouterFunction<ServerResponse> userRoutes
    ) {
        return userRoutes;
    }
}
