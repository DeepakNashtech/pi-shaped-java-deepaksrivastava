package com.evolve.controller;

import java.time.Duration;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class GreetingController {
    @MessageMapping("greet")
    public Mono<String> greet(String name) {
        return Mono.just("Hello " + name);
    }
    @MessageMapping("greet.stream")
    public Flux<String> greetStream(String name) {
        return Flux.interval(Duration.ofSeconds(1))
                   .map(i -> "Hello " + name + " #" + i)
                   .take(10); // or return infinite
    }
}
