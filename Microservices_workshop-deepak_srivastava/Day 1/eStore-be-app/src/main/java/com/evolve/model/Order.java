package com.evolve.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("orders")
public class Order {
    @Id
    private Long id;

    private String status;

    @Column("user_id")
    private Long userId;
}