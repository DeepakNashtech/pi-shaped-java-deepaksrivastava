package com.evolve.dto;

import com.evolve.model.Product;

import lombok.Data;

@Data
public class CartItemWithProduct {
    private Long id;
    private Long cartId;
    private Integer quantity;
    private Product product;

    // Constructors, Getters and Setters
}