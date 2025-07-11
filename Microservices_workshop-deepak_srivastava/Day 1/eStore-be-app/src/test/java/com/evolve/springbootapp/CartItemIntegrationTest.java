package com.evolve.springbootapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.evolve.model.Cart;
import com.evolve.model.CartItem;
import com.evolve.repository.CartItemRepository;
import com.evolve.repository.CartRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
public class CartItemIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    private Long cartId;

    @BeforeEach
    public void setup() {
        cartRepository.deleteAll().block();
        cartItemRepository.deleteAll().block();

        Cart cart = new Cart();
        cart.setUserId(1L);
        cart.setStatus("active");
        cartId = cartRepository.save(cart).block().getId();
    }

    @Test
    public void testAddCartItem() {
        CartItem item = new CartItem();
        item.setProductId(101L);
        item.setQuantity(2);

        webTestClient.post()
                .uri("/carts/{cartId}/items", cartId)
                .bodyValue(item)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CartItem.class)
                .value(ci -> {
                    assertThat(ci.getId()).isNotNull();
                    assertThat(ci.getProductId()).isEqualTo(101L);
                });
    }

    @Test
    public void testListCartItems() {
        CartItem item = new CartItem();
        item.setCartId(cartId);
        item.setProductId(200L);
        item.setQuantity(1);
        cartItemRepository.save(item).block();

        webTestClient.get()
                .uri("/carts/{cartId}/items", cartId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CartItem.class)
                .hasSize(1)
                .value(list -> assertThat(list.get(0).getProductId()).isEqualTo(200L));
    }

    @Test
    public void testRemoveCartItem() {
        CartItem item = new CartItem();
        item.setCartId(cartId);
        item.setProductId(300L);
        item.setQuantity(5);
        Long itemId = cartItemRepository.save(item).block().getId();

        webTestClient.delete()
                .uri("/carts/items/{itemId}", itemId)
                .exchange()
                .expectStatus().isNoContent();

        assertThat(cartItemRepository.findById(itemId).block()).isNull();
    }
}
