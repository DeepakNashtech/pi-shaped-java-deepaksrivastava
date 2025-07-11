package com.evolve.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;

import com.evolve.model.Cart;
import com.evolve.repository.CartRepository;
import com.evolve.service.CartEventService;

import reactor.core.publisher.Mono;

@Component
public class CartHandler {

    private final CartRepository cartRepository;
    private final CartEventService cartEventService;

    public CartHandler(CartRepository cartRepository, CartEventService cartEventService) {
        this.cartRepository = cartRepository;
        this.cartEventService = cartEventService;
    }

    public Mono<ServerResponse> getAll(ServerRequest req) {
        return ServerResponse.ok().body(cartRepository.findAll(), Cart.class);
    }

    public Mono<ServerResponse> getById(ServerRequest req) {
        Long id = Long.parseLong(req.pathVariable("id"));
        return cartRepository.findById(id)
                .flatMap(cart -> ServerResponse.ok().bodyValue(cart))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getByUserId(ServerRequest req) {
        Long userId = Long.parseLong(req.pathVariable("userId"));
        return ServerResponse.ok().body(cartRepository.findAllByUserId(userId), Cart.class);
    }

    public Mono<ServerResponse> create(ServerRequest req) {
        Long userId = Long.parseLong(req.pathVariable("userId"));
        return req.bodyToMono(Cart.class)
                .map(cart -> {
                    cart.setUserId(userId);
                    return cart;
                })
                .flatMap(cartRepository::save)
                .flatMap(saved -> {
                    // Publish cart creation event
                    return cartEventService.publishUpdateCartEvent(saved.getId(), userId, saved.getStatus())
                            .then(ServerResponse.ok().bodyValue(saved));
                });
    }

    public Mono<ServerResponse> update(ServerRequest req) {
        Long id = Long.parseLong(req.pathVariable("id"));
        return req.bodyToMono(Cart.class).flatMap(incoming ->
            cartRepository.findById(id).flatMap(existing -> {
                existing.setStatus(incoming.getStatus());
                return cartRepository.save(existing)
                        .flatMap(updated -> {
                            // Publish cart update event
                            return cartEventService.publishUpdateCartEvent(updated.getId(), updated.getUserId(), updated.getStatus())
                                    .then(ServerResponse.ok().bodyValue(updated));
                        });
            }).switchIfEmpty(ServerResponse.notFound().build())
        );
    }

    public Mono<ServerResponse> delete(ServerRequest req) {
        Long id = Long.parseLong(req.pathVariable("id"));
        return cartRepository.findById(id)
                .flatMap(cart -> {
                    // Publish cart deletion event before deleting
                    return cartEventService.publishClearCartEvent(cart.getId(), cart.getUserId())
                            .then(cartRepository.deleteById(id))
                            .then(ServerResponse.noContent().build());
                })
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}


