package com.evolve.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

import com.evolve.model.Order;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
    Flux<Order> findAllByUserId(Long userId);
}
