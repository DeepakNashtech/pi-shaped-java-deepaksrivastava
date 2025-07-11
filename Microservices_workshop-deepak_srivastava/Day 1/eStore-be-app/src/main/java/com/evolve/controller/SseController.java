package com.evolve.controller;

import com.evolve.model.CartEvent;
import com.evolve.service.CartEventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@Slf4j
public class SseController {

    private final CartEventService cartEventService;
    private final ObjectMapper objectMapper;

    public SseController(CartEventService cartEventService, ObjectMapper objectMapper) {
        this.cartEventService = cartEventService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamEvents() {
        return Flux.interval(Duration.ofSeconds(1))
                   .map(seq -> {
                       System.out.println("SSE event #" + seq);
                       return "data: SSE event #" + seq + "\n\n";
                   });
    }

    @GetMapping(value = "/cart-events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamCartEvents() {
        return cartEventService.subscribeToCartEvents()
                .map(event -> {
                    try {
                        String eventJson = objectMapper.writeValueAsString(event);
                        log.info("Streaming cart event via SSE: {}", eventJson);
                        return "data: " + eventJson + "\n\n";
                    } catch (JsonProcessingException e) {
                        log.error("Error serializing cart event for SSE", e);
                        return "data: {\"error\": \"Serialization error\"}\n\n";
                    }
                })
                .onErrorResume(e -> {
                    log.error("Error in cart events SSE stream", e);
                    return Flux.just("data: {\"error\": \"Stream error: " + e.getMessage() + "\"}\n\n");
                });
    }

    @GetMapping(value = "/cart-events/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamUserCartEvents(@PathVariable Long userId) {
        return cartEventService.subscribeToCartEvents()
                .filter(event -> event.getUserId().equals(userId))
                .map(event -> {
                    try {
                        String eventJson = objectMapper.writeValueAsString(event);
                        log.info("Streaming user cart event via SSE for userId {}: {}", userId, eventJson);
                        return "data: " + eventJson + "\n\n";
                    } catch (JsonProcessingException e) {
                        log.error("Error serializing cart event for SSE", e);
                        return "data: {\"error\": \"Serialization error\"}\n\n";
                    }
                })
                .onErrorResume(e -> {
                    log.error("Error in user cart events SSE stream for userId {}", userId, e);
                    return Flux.just("data: {\"error\": \"Stream error: " + e.getMessage() + "\"}\n\n");
                });
    }
}

