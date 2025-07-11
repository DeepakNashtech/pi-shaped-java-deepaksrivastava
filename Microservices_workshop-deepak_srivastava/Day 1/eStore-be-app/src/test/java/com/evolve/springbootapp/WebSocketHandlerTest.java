package com.evolve.springbootapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CopyOnWriteArrayList;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.main.web-application-type=reactive")
public class WebSocketHandlerTest {
    @LocalServerPort
    private int port;

    @Test
    void echoMessage() {
        ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();
        URI uri = UriComponentsBuilder.fromUriString("ws://localhost:" + port + "/ws/chat").build().toUri();
        CopyOnWriteArrayList<String> received = new CopyOnWriteArrayList<>();
        Mono<Void> result = client.execute(uri, session ->
            session.send(Mono.just(session.textMessage("Hello")))
                   .thenMany(session.receive().take(1).map(msg -> {
                       received.add(msg.getPayloadAsText());
                       return msg;
                   })).then()
        );
        StepVerifier.create(result)
                .expectSubscription()
                .verifyComplete();
        StepVerifier.create(Flux.fromIterable(received))
                .expectNext("Echo: Hello")
                .verifyComplete();
    }

    @Test
    void echoMultipleMessages() {
        ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();
        URI uri = UriComponentsBuilder.fromUriString("ws://localhost:" + port + "/ws/chat").build().toUri();
        CopyOnWriteArrayList<String> received = new CopyOnWriteArrayList<>();
        Mono<Void> result = client.execute(uri, session ->
            session.send(Flux.just(
                    session.textMessage("One"),
                    session.textMessage("Two")
            ))
            .thenMany(session.receive().take(2).map(msg -> {
                received.add(msg.getPayloadAsText());
                return msg;
            })).then()
        );
        StepVerifier.create(result)
                .expectSubscription()
                .verifyComplete();
        StepVerifier.create(Flux.fromIterable(received))
                .expectNext("Echo: One")
                .expectNext("Echo: Two")
                .verifyComplete();
    }

    @Test
    void connectionClose() {
        ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();
        URI uri = UriComponentsBuilder.fromUriString("ws://localhost:" + port + "/ws/chat").build().toUri();
        Mono<Void> result = client.execute(uri, session -> session.close());
        StepVerifier.create(result)
                .expectSubscription()
                .verifyComplete();
    }
} 