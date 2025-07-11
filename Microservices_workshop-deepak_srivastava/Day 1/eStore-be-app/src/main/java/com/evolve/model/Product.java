package com.evolve.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("products")
public class Product {
    @Id
    private Long id;

    private String name;
    private String description;
    private Double price;

    // Getters and Setters
}

