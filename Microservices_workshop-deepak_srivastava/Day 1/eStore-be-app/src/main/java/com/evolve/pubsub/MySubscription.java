package com.evolve.pubsub;

public interface MySubscription {
    void request(int n);
    void cancel();
}
