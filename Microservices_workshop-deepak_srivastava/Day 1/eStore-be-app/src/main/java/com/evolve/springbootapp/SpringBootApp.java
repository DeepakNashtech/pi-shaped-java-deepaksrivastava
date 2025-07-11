package com.evolve.springbootapp;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServer;

@SpringBootApplication(scanBasePackages="com.evolve")
@EnableR2dbcRepositories(basePackages = "com.evolve.repository") // optional if same package
@EntityScan(basePackages = {"com.evolve.model"}) // optional if same package
public class SpringBootApp {
  public static void main(String[] args) {
    SpringApplication.run(SpringBootApp.class, args);
    System.out.println("Thread: " + Thread.currentThread().getName());
  }
  /*@Bean
	public CommandLineRunner nettyServerRunner() {
		return args -> {
			HttpServer.create()
					.port(8081)
					.route(routes -> routes
						.get("/webservice", (request, response) -> response.sendString(Mono.just("Request Processed")))
					)
					.bindNow(); // Start and block (or use bind().subscribe() for non-blocking)
		};
	}*/
	@Primary
	@Bean
    public CommandLineRunner nettyServerRunner(HttpServer server) {
        return args -> server.bindNow().onDispose().block();
    }

    @Bean
    public HttpServer httpServer(@Qualifier("mainRouter") RouterFunction<ServerResponse> router) {
        HttpHandler httpHandler = RouterFunctions.toHttpHandler(router);
        ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
        return HttpServer.create().port(8081).handle(adapter);
    }
}
