package com.evolve.pubsub;

public class Main {
    public static void main(String[] args) {
        MyPublisher<String> publisher = new MyPublisher<>();
        publisher.publish("A");
        MySubscriber mySubscriber = new MySubscriber<String>() {
            private MySubscription subscription;

            @Override
            public void onSubscribe(MySubscription s) {
                this.subscription = s;
                System.out.println("Subscribed");
                subscription.request(2); // ask for 2 items
            }

            @Override
            public void onNext(String item) {
                System.out.println("Received: " + item);
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error: " + t.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("Completed");
            }
        };

        publisher.subscribe(mySubscriber);
                // Publish BEFORE subscribing

        publisher.publish("B");
        publisher.publish("C");
    }
}
