package banyan.rxjava;
import helpers.Dish;
import helpers.Utils;
import rx.Observable;
import rx.observables.SyncOnSubscribe;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class FlowControl {
    public static void testEntry() {
        //simpleBackpressureBugWithNaiveCreate();
        simpleBackpressureHandled();
        Utils.threadSleep(10000);
    }


    private static Observable<Integer> myRange(int from, int count) {
        return Observable.create(subscriber -> {
            int i = from;
            while (i < from + count) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(i++);
                } else {
                    return;
                }
            }
            subscriber.onCompleted();
        });
    }

    private static void simpleBackpressureBugWithNaiveCreate() {
       myRange(1, 500)
                .map(Dish::new)
                .observeOn(Schedulers.io()) // Try removing this, no backpressure in that case
                .subscribe(x -> {
                    Utils.printVerbose("simpleBackpressureSample1", "Washing:begin " + x);
                    Utils.threadSleep(10);
                    Utils.printVerbose("simpleBackpressureSample1", "Washing:end: " + x);
                }, e -> Utils.printVerbose(e.toString()));
    }


    private static void simpleBackpressureHandled() {
        Observable.range(1, 500)
                .map(Dish::new)
                .observeOn(Schedulers.io())
                .subscribe(x -> {
                    Utils.printVerbose("simpleBackpressureSample1", "Washing:begin: " + x);
                    Utils.threadSleep(10);
                    Utils.printVerbose("simpleBackpressureSample1", "Washing:end: " + x);
                }, error -> Utils.printVerbose("simpleBackpressureSample1", error.toString()));

        Utils.threadSleep(10000);
    }


    // SyncOnSubscribe
    public void syncOnSubscribeSample1() {
        Observable.OnSubscribe<Long> onSubscribe =
                SyncOnSubscribe.createStateful(
                        () -> 0L,
                        (cur, observer) -> {
                            observer.onNext(cur);
                            return cur + 1;
                        }
                );

        Observable<Long> naturals = Observable.create(onSubscribe);

        naturals.map(Dish::new)
                .observeOn(Schedulers.io())
                .subscribe(x -> {
                    Utils.printVerbose("simpleBackpressureSample1", "Washing:begin: " + x);
                    Utils.threadSleep(10);
                    Utils.printVerbose("simpleBackpressureSample1", "Washing:end: " + x);
                }, error -> Utils.printVerbose("simpleBackpressureSample1", error.toString()));

        Utils.threadSleep(10000);
    }
}
