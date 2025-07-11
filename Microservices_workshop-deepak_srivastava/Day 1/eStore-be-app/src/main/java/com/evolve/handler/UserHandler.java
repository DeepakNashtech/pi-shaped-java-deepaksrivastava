package com.evolve.handler;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.server.*;

import com.evolve.model.Usr;
import com.evolve.repository.UserRepository;

import reactor.core.publisher.Mono;

@Component
public class UserHandler {

    private final UserRepository repository;

    public UserHandler(UserRepository repository) {
        this.repository = repository;
    }

    public Mono<ServerResponse> getAll(ServerRequest req) {
        return ServerResponse.ok().body(repository.findAll(),Usr.class);
    }

    // GET /users/{id}
    public Mono<ServerResponse> getById(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return repository.findById(id)
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

      // POST /users
    @Transactional
    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(Usr.class)
                .flatMap(repository::save)
                .flatMap(savedUser ->
                        ServerResponse.ok().bodyValue(savedUser));
    }

    // PUT /users/{id}
    public Mono<ServerResponse> update(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        Mono<Usr> existing = repository.findById(id);
        Mono<Usr> updates = request.bodyToMono(Usr.class);

        return existing.zipWith(updates, (oldUser, newUser) -> {
                    oldUser.setName(newUser.getName());
                    oldUser.setEmail(newUser.getEmail());
                    return oldUser;
                })
                .flatMap(repository::save)
                .flatMap(updated -> ServerResponse.ok().bodyValue(updated))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    // DELETE /users/{id}
    public Mono<ServerResponse> delete(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return repository.findById(id)
                .flatMap(user ->
                        repository.delete(user).then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
