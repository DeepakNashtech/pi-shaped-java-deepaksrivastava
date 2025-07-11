package com.evolve.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartEvent {
    private String eventType; // ADD_ITEM, REMOVE_ITEM, UPDATE_CART, CLEAR_CART
    private Long cartId;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private String cartStatus;
    private Instant timestamp;
    private String message;
    
    public CartEvent(String eventType, Long cartId, Long userId, String message) {
        this.eventType = eventType;
        this.cartId = cartId;
        this.userId = userId;
        this.message = message;
        this.timestamp = Instant.now();
    }
    
    public CartEvent(String eventType, Long cartId, Long userId, Long productId, Integer quantity, String message) {
        this.eventType = eventType;
        this.cartId = cartId;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.message = message;
        this.timestamp = Instant.now();
    }
} 