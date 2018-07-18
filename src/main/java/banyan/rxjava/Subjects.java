package banyan.rxjava;


import java.util.concurrent.TimeUnit;

import helpers.Utils;
import rx.Observable;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;

/**
 * Subjects are one of the most important constructs used in Skype Code
 */
public class Subjects {
    static BehaviorSubject<Integer> behaviorSubject = BehaviorSubject.create();
    static BehaviorSubject<Integer> publishSubject = BehaviorSubject.create();
    static int behaviorCounter = 0;
    static int publishCounter = 0;
    public static void testEntry() {
        //behaviorSubject();
        publishSubject();
        subscribePublishSubject();
        Utils.threadSleep(100000);
    }

    private static void behaviorSubject() {
        Observable.interval(1, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                behaviorSubject.onNext(behaviorCounter);
                behaviorCounter++;
            }
        });
    }

    private static void publishSubject() {
        Observable.interval(1, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                publishSubject.onNext(publishCounter);
                publishCounter++;
            }
        });
    }

    private static void subscribePublishSubject() {
        publishSubject.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Utils.printVerbose("PublishSubject: subscribe1", String.valueOf(integer));
            }
        });

        publishSubject.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Utils.printVerbose("PublishSubject: subscribe2", String.valueOf(integer));
            }
        });

        publishSubject.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Utils.printVerbose("PublishSubject: subscribe3", String.valueOf(integer));
            }
        });
    }
}
