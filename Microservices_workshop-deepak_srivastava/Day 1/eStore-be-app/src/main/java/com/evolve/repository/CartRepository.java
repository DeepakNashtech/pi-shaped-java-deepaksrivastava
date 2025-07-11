package com.evolve.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.evolve.model.Cart;

import reactor.core.publisher.Flux;

public interface CartRepository extends ReactiveCrudRepository<Cart, Long> {
    Flux<Cart> findAllByUserId(Long userId);
}
