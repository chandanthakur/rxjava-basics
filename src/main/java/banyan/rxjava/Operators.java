package banyan.rxjava;

import helpers.Utils;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class Operators {

    public static void testEntry() {
        //demoMapOperator();
        //debugInSyncWorld(2);
        //debugInASyncWorld(2).subscribe(x -> Utils.printVerbose(x));
        //demoDoOnNext();
        demoRetryOperator();
    }

    private static void demoMapOperator() {
        Observable.range(200, 10)
                .map(x -> x + 5)
                .map(x -> String.valueOf("stream value: " + x))
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String value) {
                        Utils.printVerbose("demoMapOperator", String.valueOf(value));
                    }
                });
    }

    /**
     * Debugging with with doOnNext, doOnSubscribe, doOnUnSubscribe
     */
    private static void demoDoOnNext() {
        Observable.range(200, 10)
                .map(x -> x + 4)
                .map(x -> x + 5)
                .map(x -> x + 6)
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Utils.printVerbose("Debug with DoOnNext:" + integer);
                    }
                })
                .map(x -> x + 7)
                .map(x -> x + 8)
                .map(x -> x + 9)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer value) {
                        Utils.printVerbose("demoMapOperator", String.valueOf(value));
                    }
                });
    }

    /**
     * Relate with debugging in sync world
     */
    private static String debugInSyncWorld(int value) {
        double x = value;
        x = Math.pow(2, 5);
        x = Math.ceil(x/2);
        String x_str = String.valueOf(x);
        x_str = x_str.concat(x_str);
        return x_str;
    }

    /**
     * Relate with debugging in Async world with RxJava
     */
    private static Observable<String> debugInASyncWorld(int value) {
        return Observable.just(value)
               .map(x -> Math.pow(x, 5))
               .map(x-> Math.ceil(x/2))
               .map(x -> String.valueOf(x))
               .map(x -> x.concat(x));
    }

    /**
     * Showcase retry use case
     */
    private static int failCount = 5;
    private static void demoRetryOperator() {
        Observable.range(200, 10)
                .map(x -> x + 5)
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        if(failCount >= 0) {
                            failCount = failCount - 1;
                            throw new RuntimeException("I am exception");
                        } else {
                            return integer;
                        }
                    }
                })
                .map(x -> String.valueOf("stream value: " + x))
                .retry()
                //.retry(3)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String value) {
                        Utils.printVerbose("demoMapOperator", String.valueOf(value));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Utils.printVerbose("demoRetryOperator:" + throwable.toString());
                    }
                });
    }
}
