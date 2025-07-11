package com.evolve.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("cart_items")
public class CartItem {
    @Id
    private Long id;

    private Long cartId;
    private Long productId;
    private Integer quantity;

    // Getters and Setters
}
