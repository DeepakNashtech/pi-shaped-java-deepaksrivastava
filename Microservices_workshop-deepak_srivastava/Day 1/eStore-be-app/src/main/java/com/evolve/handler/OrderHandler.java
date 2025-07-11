package com.evolve.handler;


import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.server.*;

import com.evolve.exception.OrderException;
import com.evolve.model.Order;
import com.evolve.repository.OrderRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class OrderHandler {

    private final OrderRepository orderRepository;

    public OrderHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok().body(orderRepository.findAll(), Order.class);
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return orderRepository.findById(id)
                .flatMap(order -> ServerResponse.ok().bodyValue(order))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getByUserId(ServerRequest request) {
        Long userId = Long.parseLong(request.pathVariable("userId"));
        return ServerResponse.ok().body(orderRepository.findAllByUserId(userId), Order.class);
    }
    @Transactional(rollbackFor = IOException.class)
    public Mono<ServerResponse> create(ServerRequest request) {
        Long userId = Long.parseLong(request.pathVariable("userId"));
        return request.bodyToMono(Order.class)
                .map(order -> {
                    order.setUserId(userId);
                    return order;
                })
                .flatMap(orderRepository::save).flatMap(saved -> {
                            log.info("Validating Order-User {}", userId);
                            return Mono.error(new OrderException("Simulated checked exception"));
                        })
                .flatMap(saved -> ServerResponse.ok().bodyValue(saved));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return request.bodyToMono(Order.class).flatMap(incoming ->
            orderRepository.findById(id).flatMap(existing -> {
                existing.setStatus(incoming.getStatus());
                return orderRepository.save(existing);
            }).flatMap(updated -> ServerResponse.ok().bodyValue(updated))
             .switchIfEmpty(ServerResponse.notFound().build())
        );
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return orderRepository.deleteById(id)
                .then(ServerResponse.noContent().build());
    }
}

