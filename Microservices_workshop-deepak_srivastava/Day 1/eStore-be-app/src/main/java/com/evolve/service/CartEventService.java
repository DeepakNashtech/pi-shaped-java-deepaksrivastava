package com.evolve.service;

import com.evolve.model.CartEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CartEventService {
    
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String CART_EVENTS_CHANNEL = "cart:events";
    
    public CartEventService(ReactiveRedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }
    
    public Mono<Void> publishCartEvent(CartEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            log.info("Publishing cart event: {}", eventJson);
            return redisTemplate.convertAndSend(CART_EVENTS_CHANNEL, eventJson).then();
        } catch (JsonProcessingException e) {
            log.error("Error serializing cart event", e);
            return Mono.error(e);
        }
    }
    
    public Flux<CartEvent> subscribeToCartEvents() {
        return redisTemplate.listenTo(ChannelTopic.of(CART_EVENTS_CHANNEL))
                .map(message -> {
                    try {
                        return objectMapper.readValue(message.getMessage(), CartEvent.class);
                    } catch (JsonProcessingException e) {
                        log.error("Error deserializing cart event", e);
                        return null;
                    }
                })
                .filter(event -> event != null)
                .doOnNext(event -> log.info("Received cart event: {}", event));
    }
    
    // Convenience methods for different event types
    public Mono<Void> publishAddItemEvent(Long cartId, Long userId, Long productId, Integer quantity) {
        CartEvent event = new CartEvent("ADD_ITEM", cartId, userId, productId, quantity, 
            "Item added to cart: productId=" + productId + ", quantity=" + quantity);
        return publishCartEvent(event);
    }
    
    public Mono<Void> publishRemoveItemEvent(Long cartId, Long userId, Long productId) {
        CartEvent event = new CartEvent("REMOVE_ITEM", cartId, userId, productId, null, 
            "Item removed from cart: productId=" + productId);
        return publishCartEvent(event);
    }
    
    public Mono<Void> publishUpdateCartEvent(Long cartId, Long userId, String status) {
        CartEvent event = new CartEvent("UPDATE_CART", cartId, userId, 
            "Cart updated: status=" + status);
        event.setCartStatus(status);
        return publishCartEvent(event);
    }
    
    public Mono<Void> publishClearCartEvent(Long cartId, Long userId) {
        CartEvent event = new CartEvent("CLEAR_CART", cartId, userId, 
            "Cart cleared");
        return publishCartEvent(event);
    }
} 