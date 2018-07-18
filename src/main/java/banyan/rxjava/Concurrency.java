package banyan.rxjava;

import helpers.ApiFactory;
import helpers.Cities;
import helpers.City;
import helpers.GeoNames;
import helpers.MeetupApi;
import helpers.Utils;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class Concurrency {

    public static void testEntry() {
        //syncBasic();
        //asyncBasicWithSubscribeOn();
        asyncBasicWithObserveOn();
        //concurrencyContractViolation();
        //concurrencyContractViolationFix();
        Utils.threadSleep(10000);
    }

    private static void syncBasic() {
        Utils.printVerbose("syncBasic:start");
        sleepingObservable(5).subscribe(x -> Utils.printVerbose("syncBasic:" + x));
        Utils.printVerbose("syncBasic:end");
    }

    /**
     * Understand the difference between subscribeOn and observeOn
     */
    private static void asyncBasicWithSubscribeOn() {
        Utils.printVerbose("asyncBasic:start");
        sleepingObservable(5)
                .subscribeOn(Schedulers.io())
                .subscribe(x -> Utils.printVerbose("asyncBasic:" + x));
        Utils.printVerbose("asyncBasic:end");
    }

    private static void asyncBasicWithObserveOn() {
        Utils.printVerbose("asyncBasic:start");
        sleepingObservable(5)
                .observeOn(Schedulers.io())
                .subscribe(x -> Utils.printVerbose("asyncBasic:" + x));
        Utils.printVerbose("asyncBasic:end");
    }

    private static Observable<String> sleepingObservable(int count) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                for(int kk = 0; kk < count; kk++) {
                    subscriber.onNext("value" + kk);
                    // dummy work
                    Utils.threadSleep(1000);
                }
            }
        });
    }

    /**
     * The source can be async but we should use RxLibraries as much as possible to
     * ensure the correctness. Easy to go wrong
     */
    private static void concurrencyContractViolation() {
        // DO NOT DO THIS
        Observable<String> c = Observable.create(s -> {
            // Thread A
            new Thread(() -> {
                s.onNext("one");
                s.onNext("two");
            }).start();

            // Thread B
            new Thread(() -> {
                s.onNext("three");
                s.onNext("four");
            }).start();
            // ignoring need to emit s.onCompleted() due to race of threads
        });

        c.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Utils.printVerbose("concurrencyContractViolation", s);
            }
        });
    }

    private static void concurrencyContractViolationFix() {
        Observable<String> a = Observable.create(s -> {
            new Thread(() -> {
                s.onNext("one");
                s.onNext("two");
                s.onCompleted();
            }).start();
        });

        Observable<String> b = Observable.create(s -> {
            new Thread(() -> {
                s.onNext("three");
                s.onNext("four");
                s.onCompleted();
            }).start();
        });

        // this subscribes to a and b concurrently,
        // and merges into a third sequential stream
        Observable<String> c = Observable.merge(a, b);
    }


    // demonstrate subscribeOn, observeOn
    void schedulingCitiesAroundLatLng() {
        final ApiFactory api = new ApiFactory();
        MeetupApi meetup = api.meetup();
        double hydLat = 17.4126274;
        double hydLng = 78.2679609;
        Observable<Cities> cities = meetup.listCities(hydLat, hydLng);
        Observable<City> cityObs = cities.concatMapIterable(Cities::getResults);
        Observable<String> citiesNamesObs = cityObs.map(City::getCity);
        citiesNamesObs.
                subscribe(cityName -> {
                    Utils.printVerbose(String.valueOf(cityName));
                }, y -> {
                    Utils.printVerbose(y.toString());
                });

        Utils.printVerbose("End of main function");
        Utils.threadSleep(10000);
    }

    void temp() {
        final ApiFactory api = new ApiFactory();
        MeetupApi meetup = api.meetup();
        GeoNames geoNames = api.geoNames();

        double hydLat = 17.4126274;
        double hydLng = 78.2679609;
        Observable<Cities> cities = meetup.listCities(hydLat, hydLng);
        Observable<City> cityObs = cities.concatMapIterable(Cities::getResults);

        Observable<String> citiesNamesObs = cityObs
                .map(City::getCity);

        Observable<Long> totalPopulation = citiesNamesObs
                .flatMap(geoNames::populationOf)
                .reduce(0L, (x, y) -> x + y);

        totalPopulation.subscribe(x -> {
            Utils.printVerbose(String.valueOf(x));
        }, y -> {
            Utils.printVerbose(y.toString());
        });

        Utils.threadSleep(10000);
    }
}
