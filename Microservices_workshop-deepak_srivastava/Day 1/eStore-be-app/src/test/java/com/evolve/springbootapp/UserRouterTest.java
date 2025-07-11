package com.evolve.springbootapp;

import com.evolve.handler.UserHandler;
import com.evolve.model.Usr;
import com.evolve.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
public class UserRouterTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserRepository userRepository;

    @MockBean(name = "mainRouter")
    RouterFunction<ServerResponse> mainRouter;

    @Test
    void getAllUsers_returnsList() {
        Usr user1 = new Usr();
        user1.setId(1L);
        user1.setName("Alice");
        user1.setEmail("alice@example.com");
        Usr user2 = new Usr();
        user2.setId(2L);
        user2.setName("Bob");
        user2.setEmail("bob@example.com");
        Mockito.when(userRepository.findAll()).thenReturn(Flux.just(user1, user2));

        webTestClient.get().uri("/users")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Usr.class)
                .hasSize(2)
                .contains(user1, user2);
    }

    @Test
    void getUserById_returnsUser() {
        Usr user = new Usr();
        user.setId(1L);
        user.setName("Alice");
        user.setEmail("alice@example.com");
        Mockito.when(userRepository.findById(1L)).thenReturn(Mono.just(user));

        webTestClient.get().uri("/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Usr.class)
                .isEqualTo(user);
    }

    @Test
    void createUser_returnsCreatedUser() {
        Usr user = new Usr();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        Usr savedUser = new Usr();
        savedUser.setId(1L);
        savedUser.setName("Alice");
        savedUser.setEmail("alice@example.com");
        Mockito.when(userRepository.save(Mockito.any(Usr.class))).thenReturn(Mono.just(savedUser));

        webTestClient.post().uri("/users")
                .bodyValue(user)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Usr.class)
                .isEqualTo(savedUser);
    }
} 