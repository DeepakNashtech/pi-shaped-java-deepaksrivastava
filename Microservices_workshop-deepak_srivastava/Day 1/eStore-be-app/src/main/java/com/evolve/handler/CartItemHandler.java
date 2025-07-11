package com.evolve.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;

import com.evolve.dto.CartItemWithProduct;
import com.evolve.model.CartItem;
import com.evolve.repository.CartItemRepository;
import com.evolve.repository.CartRepository;
import com.evolve.repository.ProductRepository;
import com.evolve.service.CartEventService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CartItemHandler {

    private final CartItemRepository cartItemRepo;
    private final ProductRepository productRepo;
    private final CartRepository cartRepository;
    private final CartEventService cartEventService;

    public CartItemHandler(CartItemRepository cartItemRepo, ProductRepository productRepo, 
                          CartRepository cartRepository, CartEventService cartEventService) {
        this.cartItemRepo = cartItemRepo;
        this.productRepo = productRepo;
        this.cartRepository = cartRepository;
        this.cartEventService = cartEventService;
    }

    public Mono<ServerResponse> list(ServerRequest req) {
        Long cartId = Long.parseLong(req.pathVariable("cartId"));

        Flux<CartItemWithProduct> enrichedItems = cartItemRepo.findAllByCartId(cartId)
            .flatMap(item ->
                productRepo.findById(item.getProductId())
                    .switchIfEmpty(Mono.error(new RuntimeException("Product not found: " + item.getProductId())))
                    .map(product -> {
                        CartItemWithProduct dto = new CartItemWithProduct();
                        dto.setId(item.getId());
                        dto.setCartId(item.getCartId());
                        dto.setQuantity(item.getQuantity());
                        dto.setProduct(product);
                        return dto;
                    })
            );

            return ServerResponse.ok().body(enrichedItems, CartItemWithProduct.class)
            .onErrorResume(e -> {
                e.printStackTrace();
                return ServerResponse.status(500).bodyValue("Internal error: " + e.getMessage());
            });
    }

    public Mono<ServerResponse> add(ServerRequest req) {
        Long cartId = Long.parseLong(req.pathVariable("cartId"));
        return req.bodyToMono(CartItem.class)
                .map(item -> {
                    item.setCartId(cartId);
                    return item;
                })
                .flatMap(cartItemRepo::save)
                .flatMap(saved -> {
                    // Get cart to find userId for event publishing
                    return cartRepository.findById(cartId)
                            .flatMap(cart -> {
                                // Publish add item event
                                return cartEventService.publishAddItemEvent(
                                        cartId, cart.getUserId(), saved.getProductId(), saved.getQuantity())
                                        .then(ServerResponse.ok().bodyValue(saved));
                            })
                            .switchIfEmpty(ServerResponse.status(500).bodyValue("Cart not found"));
                });
    }

    public Mono<ServerResponse> remove(ServerRequest req) {
        Long itemId = Long.parseLong(req.pathVariable("itemId"));
        return cartItemRepo.findById(itemId)
                .flatMap(item -> {
                    // Get cart to find userId for event publishing
                    return cartRepository.findById(item.getCartId())
                            .flatMap(cart -> {
                                // Publish remove item event before deleting
                                return cartEventService.publishRemoveItemEvent(
                                        item.getCartId(), cart.getUserId(), item.getProductId())
                                        .then(cartItemRepo.deleteById(itemId))
                                        .then(ServerResponse.noContent().build());
                            })
                            .switchIfEmpty(ServerResponse.status(500).bodyValue("Cart not found"));
                })
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
