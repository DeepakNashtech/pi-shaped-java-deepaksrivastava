package com.evolve.springbootapp;

import com.evolve.grpc.ChatProto;
import com.evolve.grpc.ChatServiceImpl;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class GrpcChatServiceTest {
    @Test
    void chat_echoesMessage() {
        ChatServiceImpl service = new ChatServiceImpl();
        AtomicReference<ChatProto.ChatMessage> response = new AtomicReference<>();
        StreamObserver<ChatProto.ChatMessage> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(ChatProto.ChatMessage value) {
                response.set(value);
            }
            @Override public void onError(Throwable t) { }
            @Override public void onCompleted() { }
        };
        StreamObserver<ChatProto.ChatMessage> requestObserver = service.chat(responseObserver);
        ChatProto.ChatMessage msg = ChatProto.ChatMessage.newBuilder().setUser("Alice").setText("Hello").build();
        requestObserver.onNext(msg);
        assertNotNull(response.get());
        assertEquals("Server", response.get().getUser());
        assertTrue(response.get().getText().contains("Echo: Hello"));
    }

    @Test
    void chat_onCompleted_callsResponseCompleted() {
        ChatServiceImpl service = new ChatServiceImpl();
        AtomicBoolean completed = new AtomicBoolean(false);
        StreamObserver<ChatProto.ChatMessage> responseObserver = new StreamObserver<>() {
            @Override public void onNext(ChatProto.ChatMessage value) { }
            @Override public void onError(Throwable t) { }
            @Override public void onCompleted() { completed.set(true); }
        };
        StreamObserver<ChatProto.ChatMessage> requestObserver = service.chat(responseObserver);
        requestObserver.onCompleted();
        assertTrue(completed.get());
    }

    @Test
    void chat_onError_doesNotThrow() {
        ChatServiceImpl service = new ChatServiceImpl();
        StreamObserver<ChatProto.ChatMessage> responseObserver = new StreamObserver<>() {
            @Override public void onNext(ChatProto.ChatMessage value) { }
            @Override public void onError(Throwable t) { }
            @Override public void onCompleted() { }
        };
        StreamObserver<ChatProto.ChatMessage> requestObserver = service.chat(responseObserver);
        assertDoesNotThrow(() -> requestObserver.onError(new RuntimeException("test error")));
    }
} 