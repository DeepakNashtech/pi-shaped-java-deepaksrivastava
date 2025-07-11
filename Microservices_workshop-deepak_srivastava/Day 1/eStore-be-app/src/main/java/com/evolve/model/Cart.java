package com.evolve.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("carts")
public class Cart {
    @Id
    private Long id;

    private Long userId;
    private String status; // active, abandoned, checked_out

    // Getters and Setters
}