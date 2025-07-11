package com.evolve.springbootapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;

@Component
public class RSocketClient implements CommandLineRunner {

    @Override
    public void run(String... args) {
        RSocketRequester requester = RSocketRequester.builder()
                .connectTcp("localhost", 7000)
                .block();

        // Unary call
        requester.route("greet")
                .data("Spring Boot")
                .retrieveMono(String.class)
                .doOnNext(System.out::println)
                .block();

        // Streaming call
        requester.route("greet.stream")
                .data("RSocket")
                .retrieveFlux(String.class)
                .take(5)
                .doOnNext(System.out::println)
                .blockLast();
    }
}

