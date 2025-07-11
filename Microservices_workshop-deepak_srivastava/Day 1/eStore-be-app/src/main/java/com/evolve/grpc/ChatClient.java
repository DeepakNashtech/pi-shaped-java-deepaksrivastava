package com.evolve.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ChatClient {

    private final ChatServiceGrpc.ChatServiceStub stub;

    public ChatClient(String host, int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        stub = ChatServiceGrpc.newStub(channel);
    }

    public void startChat() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<ChatProto.ChatMessage> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(ChatProto.ChatMessage message) {
                System.out.println(message.getUser() + " says: " + message.getText());
            }
            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                latch.countDown();
            }
            @Override
            public void onCompleted() {
                System.out.println("Chat ended by server.");
                latch.countDown();
            }
        };

        StreamObserver<ChatProto.ChatMessage> requestObserver = stub.chat(responseObserver);

        // Example messages
        ChatProto.ChatMessage msg1 = ChatProto.ChatMessage.newBuilder()
                .setUser("ClientUser")
                .setText("Hello!")
                .setTimestamp(Instant.now().toEpochMilli())
                .build();

        ChatProto.ChatMessage msg2 = ChatProto.ChatMessage.newBuilder()
                .setUser("ClientUser")
                .setText("How are you?")
                .setTimestamp(Instant.now().toEpochMilli())
                .build();

        requestObserver.onNext(msg1);
        requestObserver.onNext(msg2);

        requestObserver.onCompleted();
        latch.await(1, TimeUnit.MINUTES);
    }

    public static void main(String[] args) throws InterruptedException {
        new ChatClient("localhost", 9090).startChat();
    }
}
