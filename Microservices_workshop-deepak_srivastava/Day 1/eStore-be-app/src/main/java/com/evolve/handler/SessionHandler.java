package com.evolve.handler;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.WebSession;

import reactor.core.publisher.Mono;

@Component
public class SessionHandler {

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.session().flatMap(session -> {
            session.getAttributes().put("user", "Indrajeet");
            return ServerResponse.ok()
            .cookie(ResponseCookie.from("debug", "true").build()) // optional debug
            .bodyValue("Logged in");
        });
    }

    public Mono<ServerResponse> profile(ServerRequest request) {
        return request.session().flatMap(session -> {
            String user = (String) session.getAttribute("user");
            if (user != null) {
                return ServerResponse.ok().bodyValue("Welcome, " + user);
            } else {
                return ServerResponse.status(401).bodyValue("Not logged in");
            }
        });
    }

    public Mono<ServerResponse> logout(ServerRequest request) {
        return request.session().doOnNext(WebSession::invalidate)
            .then(ServerResponse.ok().bodyValue("Logged out"));
    }
}

