package com.evolve.pubsub;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MyPublisher<T> {
    private final BlockingQueue<T> dataQueue = new LinkedBlockingQueue<>();
    private MySubscriber<T> subscriber;
    private MySubscription mySubscription= new MySubscription() {
            private boolean cancelled = false;

            @Override
            public void request(int n) {
                if (cancelled) return;
                try {
                    for (int i = 0; i < n && !dataQueue.isEmpty(); i++) {
                        subscriber.onNext(dataQueue.poll());
                    }
                    if (dataQueue.isEmpty()) {
                        subscriber.onComplete();
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }

            @Override
            public void cancel() {
                cancelled = true;
            }
        };

    public void subscribe(MySubscriber<T> subscriber) {
        this.subscriber = subscriber;
        subscriber.onSubscribe(mySubscription);
    }

    public void publish(T data) {
        dataQueue.add(data);
    }
}

