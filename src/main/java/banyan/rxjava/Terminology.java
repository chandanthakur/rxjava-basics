package banyan.rxjava;

import java.util.concurrent.TimeUnit;

import helpers.Utils;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

public class Terminology {

    public static void testEntry() {
        //rxSubscriberInterface();
        // onNext is always serialized, gauranteed by observable
        //serializeContractBroken();
        //
        // workOnSubscribe();
        //observableMultipleSubscribers();
        //observableIntervalUnsubscribe();
        //observableDefaultExecution();
        //observableLocalIntervalUnsubscribe();
    }


    /**
     * Understand Subscriber interface
     */
    private static void rxSubscriberInterface() {
        Utils.printVerbose("rxSubscriberInterface", "start");
        //rxObservableRange(100, 20).serialize().subscribe(new Subscriber<Integer>() {
        Observable.range(100, 20).subscribe(new Subscriber<Integer>() {
            /**
             * Called every time a new data is available
             */
            @Override
            public void onNext(Integer integer) {
                Utils.printVerbose("Streaming integers, curr value: " + integer);
                // Interface supports unsubscribe as well.
                // Once you unsubscribe no more events will be fired
                // Try uncomment below, you will get onNext only once
                // this.unsubscribe();
            }

            /**
             * Called on completion, no more events after it is complete
             */
            @Override
            public void onCompleted() {

            }

            /**
             * Called on error
             * @param e exception thrown
             */
            @Override
            public void onError(Throwable e) {

            }
        });

        Utils.printVerbose("rxSubscriberInterface", "end");
    }

    /**
     * Crude implementation of range observable for demo purpose only.
     */
    private static Observable<Integer> rxObservableRange(int start, int count) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for(int ii = start; ii < (start + count); ii++) {
                    //if(!subscriber.isUnsubscribed()) {
                        subscriber.onNext(ii);
                    //}
                }
            }
        });
    }

    /**
     * Show the work should happen in the chain on subscribe only and not construction
     */
    private static void workOnSubscribe() {
        //getIntegerStreamBuggy();
        getIntegerStreamFixed();
    }

    /**
     * Resolve is happening out the chain, avoid doing that.
     */
    private static Observable<Integer> getIntegerStreamBuggy() {
        int rand = (int)(Math.random()*100);
        if(rand%2 == 0) {
            System.out.println("Pick range 100 to 200");
            return Observable.range(100, 199);
        } else {
            System.out.println("Pick range 200 to 300");
            return Observable.range(200, 299);
        }
    }

    /**
     * Resolve happening after subscribe
     */
    private static Observable<Integer> getIntegerStreamFixed() {
        return Observable.defer(new Func0<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                int rand = (int)(Math.random()*100);
                if(rand%2 == 0) {
                    return Observable.range(100, 199);
                } else {
                    return Observable.range(100, 199);
                }
            }
        });
    }

    // Useful shortcuts for create observer
    public void observableSubscriberSimple5(){
        String [] words = new String[] { "Hello", "Welcome", "to", "the", "world", "of", "RxJava" };
        Observable.from(words).
                subscribe(word -> Utils.printVerbose("observableSubscriberSimple5", word));
    }

    // Simple concept
    public void observableMultipleSubscribers() {
        String [] words = new String[] { "Hello", "Welcome", "to", "the", "world", "of", "RxJava" };
        Observable<String> observable = Observable.from(words);
        observable.subscribe(word -> Utils.printVerbose("observableMultipleSubscribers - 1", word));
        observable.subscribe(word -> Utils.printVerbose("observableMultipleSubscribers - 2", word));
    }

    // Default execution
    private static void observableDefaultExecution() {
        String [] words = new String[] { "Hello", "Welcome", "to", "the", "world", "of", "RxJava" };
        Observable<String> observable = Observable.from(words);
        observable.subscribe(word -> Utils.printVerbose("observableDefaultExecution", word));
        Utils.printVerbose("Main thread exit");
    }

    // Default execution
    private static void observableIntervalUnsubscribe() {
        Observable<String> observable = Observable.interval(50, TimeUnit.MILLISECONDS)
                .map(val -> String.valueOf(val));
        Subscription subscription = observable.subscribe(word -> Utils.printVerbose("observableIntervalUnsubscribe", word));
        Utils.printVerbose("Main thread end of code");
        Utils.threadSleep(4000);
        Utils.printVerbose("Main thread, un-subscribe");
        subscription.unsubscribe(); // unsubscribe need to be implemented
        Utils.threadSleep(2000);
    }

    private static Observable<Long> localInterval(Long interval) {
        Observable<Long> observable = Observable.create(new Observable.OnSubscribe<Long>(){
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                Thread thread = new Thread(() -> {
                    try {
                        Long count = 0L;
                        while (true) {
                            if(subscriber.isUnsubscribed()) {
                                break;
                            }

                            subscriber.onNext(count);
                            Thread.sleep(interval);
                            count = count + interval;
                        }
                    } catch (Exception ex) {
                        subscriber.onError(ex);
                    }
                });

                thread.start();
                subscriber.add(Subscriptions.create(thread::interrupt));
            }
        });

        return observable;
    }

    // Default execution
    public static void observableLocalIntervalUnsubscribe() {
        Observable<String> observable = localInterval(50L).map(val -> String.valueOf(val));
        Subscription subscription = observable.subscribe(word -> Utils.printVerbose("observableIntervalUnsubscribe", word), exception -> Utils.printVerbose(exception.getMessage()));
        Utils.printVerbose("Main thread end of code");
        Utils.threadSleep(4000);
        Utils.printVerbose("Main thread, un-subscribe");
        subscription.unsubscribe(); // unsubscribe need to be implemented
        Utils.threadSleep(2000);
    }
}
