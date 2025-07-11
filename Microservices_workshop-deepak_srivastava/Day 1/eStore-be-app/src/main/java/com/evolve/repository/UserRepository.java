package com.evolve.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.evolve.model.Usr;

import reactor.core.publisher.Flux;

public interface UserRepository extends ReactiveCrudRepository<Usr, Long> {
    Flux<Usr> findByName(String name);
}
