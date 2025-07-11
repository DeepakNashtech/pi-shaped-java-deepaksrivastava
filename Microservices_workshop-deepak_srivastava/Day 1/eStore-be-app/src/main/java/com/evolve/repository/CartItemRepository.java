package com.evolve.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.evolve.model.CartItem;

import reactor.core.publisher.Flux;

public interface CartItemRepository extends ReactiveCrudRepository<CartItem, Long> {
    Flux<CartItem> findAllByCartId(Long cartId);
}

