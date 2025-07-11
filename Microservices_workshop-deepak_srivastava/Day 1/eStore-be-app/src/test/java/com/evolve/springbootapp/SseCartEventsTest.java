package com.evolve.springbootapp;

import com.evolve.model.Cart;
import com.evolve.model.CartItem;
import com.evolve.repository.CartRepository;
import com.evolve.repository.CartItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.main.web-application-type=reactive",
    "spring.redis.host=localhost",
    "spring.redis.port=6379"
})
public class SseCartEventsTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    private Long cartId;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        // Clean up and create a test cart
        cartRepository.deleteAll().block();
        cartItemRepository.deleteAll().block();

        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setStatus("active");
        cartId = cartRepository.save(cart).block().getId();
    }

    @Test
    void testCartEventsSseStream() {
        CopyOnWriteArrayList<String> receivedEvents = new CopyOnWriteArrayList<>();

        // Start SSE connection
        Flux<String> sseStream = webTestClient.get()
                .uri("/cart-events")
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class)
                .getResponseBody();

        // Subscribe to SSE stream and collect events
        sseStream.take(3) // Take first 3 events
                .doOnNext(event -> {
                    if (event.startsWith("data: ")) {
                        receivedEvents.add(event.substring(6)); // Remove "data: " prefix
                    }
                })
                .subscribe();

        // Wait a bit for connection to establish
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Trigger cart events by adding items
        CartItem item1 = new CartItem();
        item1.setCartId(cartId);
        item1.setProductId(101L);
        item1.setQuantity(2);

        CartItem item2 = new CartItem();
        item2.setCartId(cartId);
        item2.setProductId(102L);
        item2.setQuantity(1);

        // Add first item
        webTestClient.post()
                .uri("/carts/{cartId}/items", cartId)
                .bodyValue(item1)
                .exchange()
                .expectStatus().isOk();

        // Add second item
        webTestClient.post()
                .uri("/carts/{cartId}/items", cartId)
                .bodyValue(item2)
                .exchange()
                .expectStatus().isOk();

        // Wait for events to be processed
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify that we received some events
        assertThat(receivedEvents).isNotEmpty();
        
        // Verify that events contain expected data
        receivedEvents.forEach(event -> {
            assertThat(event).contains("eventType");
            assertThat(event).contains("cartId");
            assertThat(event).contains("userId");
        });
    }

    @Test
    void testUserSpecificCartEventsSseStream() {
        CopyOnWriteArrayList<String> receivedEvents = new CopyOnWriteArrayList<>();

        // Start SSE connection for specific user
        Flux<String> sseStream = webTestClient.get()
                .uri("/cart-events/{userId}", userId)
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class)
                .getResponseBody();

        // Subscribe to SSE stream and collect events
        sseStream.take(2) // Take first 2 events
                .doOnNext(event -> {
                    if (event.startsWith("data: ")) {
                        receivedEvents.add(event.substring(6)); // Remove "data: " prefix
                    }
                })
                .subscribe();

        // Wait a bit for connection to establish
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Trigger cart event by updating cart status
        Cart updatedCart = new Cart();
        updatedCart.setId(cartId);
        updatedCart.setUserId(userId);
        updatedCart.setStatus("checked_out");

        webTestClient.put()
                .uri("/carts/{id}", cartId)
                .bodyValue(updatedCart)
                .exchange()
                .expectStatus().isOk();

        // Wait for events to be processed
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify that we received events for the specific user
        assertThat(receivedEvents).isNotEmpty();
        
        receivedEvents.forEach(event -> {
            assertThat(event).contains("\"userId\":" + userId);
            assertThat(event).contains("eventType");
        });
    }

    @Test
    void testCartEventsSseStreamWithStepVerifier() {
        // Test using StepVerifier for more precise control
        Flux<String> sseStream = webTestClient.get()
                .uri("/cart-events")
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class)
                .getResponseBody();

        StepVerifier.create(sseStream.take(1))
                .expectNextMatches(event -> 
                    event.startsWith("data: ") && 
                    (event.contains("ADD_ITEM") || event.contains("UPDATE_CART") || event.contains("REMOVE_ITEM"))
                )
                .verifyComplete();
    }
} 