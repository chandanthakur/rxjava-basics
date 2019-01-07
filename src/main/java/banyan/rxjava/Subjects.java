package banyan.rxjava;


import java.util.UUID;
import java.util.concurrent.TimeUnit;

import helpers.Utils;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

/**
 * Subjects are one of the most important constructs used in Skype Code
 */
public class Subjects {
    static BehaviorSubject<String> tokenSubject = BehaviorSubject.create();

    static BehaviorSubject<Integer> behaviorSubject = BehaviorSubject.create();
    static int behaviorCounter = 0;
    public static void testEntry() {
        tokenWorker();
        populationUsingToken(17.4126274, 78.2679609);
        populationUsingToken(28.7041, 77.1025);
        populationUsingToken(19.0760, 72.8777);
        populationUsingToken(13.0827, 80.2707);
        populationUsingToken(12.9716, 77.5946);
        Utils.threadSleep(8000);
    }


    private static Observable<String> getToken() {
        return tokenSubject;

    }

    private static void tokenWorker() {
        Observable.interval(5, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                String token = UUID.randomUUID().toString();
                Utils.printVerbose("tokenWorker:newToken: ", token);
                tokenSubject.onNext(token);
            }
        });
    }

    private  static void populationUsingToken(final double lat, final double lng){
        getToken()
        .flatMap(token -> Composability.cityAroundLatLng(lat, lng))
        .flatMap(city -> Composability.populationOf(city))
        .subscribe((population) -> Utils.printVerbose("populationUsingToken:population: ", String.valueOf(population)));
    }
}
