package com.evolve.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.evolve.model.Product;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
}
