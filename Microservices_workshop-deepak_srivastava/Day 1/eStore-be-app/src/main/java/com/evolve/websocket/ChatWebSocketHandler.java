package com.evolve.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Mono;

@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(
            session.receive()
                   .map(msg -> session.textMessage("Echo: " + msg.getPayloadAsText()))
        );
    }
}