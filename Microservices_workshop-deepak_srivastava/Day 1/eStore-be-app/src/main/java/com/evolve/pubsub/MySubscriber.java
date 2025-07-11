package com.evolve.pubsub;

public interface MySubscriber<T> {
    void onSubscribe(MySubscription subscription);
    void onNext(T item);
    void onError(Throwable t);
    void onComplete();
}

