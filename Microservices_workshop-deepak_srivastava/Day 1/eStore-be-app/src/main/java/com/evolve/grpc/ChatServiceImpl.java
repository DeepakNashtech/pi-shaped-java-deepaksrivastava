package com.evolve.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.Instant;

@GrpcService
public class ChatServiceImpl extends ChatServiceGrpc.ChatServiceImplBase {

    @Override
    public StreamObserver<ChatProto.ChatMessage> chat(
            StreamObserver<ChatProto.ChatMessage> responseObserver) {

        return new StreamObserver<>() {
            @Override
            public void onNext(ChatProto.ChatMessage request) {
                System.out.println("Received: " + request.getUser() + ": " + request.getText());

                // Echo message back
                ChatProto.ChatMessage reply = ChatProto.ChatMessage.newBuilder()
                        .setUser("Server")
                        .setText("Echo: " + request.getText())
                        .setTimestamp(Instant.now().toEpochMilli())
                        .build();
                responseObserver.onNext(reply);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
