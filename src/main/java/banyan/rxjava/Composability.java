package banyan.rxjava;

import java.util.concurrent.TimeUnit;

import helpers.ApiFactory;
import helpers.Cities;
import helpers.City;
import helpers.GeoNames;
import helpers.Geoname;
import helpers.MeetupApi;
import helpers.SearchResult;
import helpers.Utils;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class Composability {

    public static void testEntry() {
        //composeFunctionsWithoutFlatMap();
        composeFunctionsFlatMap();
        //mergeStreams();
        //zipStreams();
        //findPopulationSample1();
        //citiesAroundLatLngSample1();
        //cityAroundLatLngSample1();
        Utils.threadSleep(10000);
    }

    /**
     * Composing streams without flatmap, one of the most important operator you would use with Rx
     */
    private static void composeFunctionsWithoutFlatMap() {
        double hydLat = 17.4126274;
        double hydLng = 78.2679609;
        cityAroundLatLng(hydLat, hydLng).
                map(x -> populationOf(x)).
                subscribe(population -> Utils.printVerbose("composeFunctionsFlatMap", String.valueOf(population)));
        Utils.printVerbose("End of main function");
        Utils.threadSleep(10000);
    }

    /**
     * Flatmap usage
     */
    private static void composeFunctionsFlatMap() {
        double hydLat = 17.4126274;
        double hydLng = 78.2679609;
        cityAroundLatLng(hydLat, hydLng).
                flatMap(city -> populationOf(city)).
                subscribe(population -> Utils.printVerbose("composeFunctionsFlatMap", String.valueOf(population)));
        Utils.printVerbose("End of main function");
    }

    private static void mergeStreams() {
        Observable<Long> a = Observable.interval(1, TimeUnit.SECONDS);
        Observable<Long> b = Observable.interval(2, TimeUnit.SECONDS);
        a.mergeWith(b)
                .subscribe(value -> Utils.printVerbose("mergeStreams:" + value));
    }

    static public class Pair<T1, T2>{
        public T1 first;
        public T2 second;
        public Pair(T1 f, T2 b) {
            this.first = f;
            this.second = b;
        }
    }

    /**
     * Two streams, one streaming every 1 second and other every 2 seconds
     */
    private static void zipStreams() {
        Observable<Long> a = Observable.interval(1, TimeUnit.SECONDS);
        Observable<Long> b = Observable.interval(2, TimeUnit.SECONDS);
        a.zipWith(b, new Func2<Long, Long, Pair<Long, Long>>() {
            @Override
            public Pair<Long, Long> call(Long a_val, Long b_bal) {
                return new Pair<Long, Long>(a_val, b_bal);
            }
        }).subscribe(value -> Utils.printVerbose("mergeStreams:" + value.first + ", " + value.second));
    }

    private static void citiesAroundLatLngSample1() {
        double hydLat = 17.4126274;
        double hydLng = 78.2679609;
        citiesAroundLatLng(hydLat, hydLng).
                subscribe(cityName -> {
                    Utils.printVerbose(String.valueOf(cityName));
                }, y -> {
                    Utils.printVerbose(y.toString());
                });

        Utils.printVerbose("End of main function");
        Utils.threadSleep(10000);
    }

    private static void cityAroundLatLngSample1() {
        double hydLat = 17.4126274;
        double hydLng = 78.2679609;
        cityAroundLatLng(hydLat, hydLng).
                subscribe(cityName -> {
                    Utils.printVerbose(String.valueOf(cityName));
                }, y -> {
                    Utils.printVerbose(y.toString());
                });

        Utils.printVerbose("End of main function");
        Utils.threadSleep(10000);
    }

    // Discuss map variants here with different parameters, parallelization
    private static void composeFunctionsExample2() {
        double hydLat = 17.4126274;
        double hydLng = 78.2679609;
        citiesAroundLatLng(hydLat, hydLng).
                flatMap(city -> populationOf(city).subscribeOn(Schedulers.io()), 1).
                subscribeOn(Schedulers.io()).
                subscribe(population -> Utils.printVerbose("composeFunctionsFlatMap", String.valueOf(population)));
        Utils.printVerbose("End of main function");
        Utils.threadSleep(25000);
    }

    private static Observable<Integer> populationOf(String query) {
        final ApiFactory api = new ApiFactory();
        GeoNames geoNames = api.geoNames();
        return geoNames.search(query)
                .doOnNext(X -> Utils.printVerbose("populationOf", query))
                .concatMapIterable(SearchResult::getGeonames)
                .map(Geoname::getPopulation)
                .filter(p -> p != null)
                .singleOrDefault(0)
                .onErrorReturn(th -> 0);
    }

    private static Observable<String> citiesAroundLatLng(double lat, double lng) {
        final ApiFactory api = new ApiFactory();
        MeetupApi meetup = api.meetup();
        return meetup.listCities(lat, lng).
                doOnNext(X -> Utils.printVerbose("citiesAroundLatLng", lat + "," + lng)).
                concatMapIterable(Cities::getResults).
                map(City::getCity);
    }

    private static Observable<String> cityAroundLatLng(double lat, double lng) {
        final ApiFactory api = new ApiFactory();
        MeetupApi meetup = api.meetup();
        return meetup.listCities(lat, lng).
                concatMapIterable(Cities::getResults).
                map(City::getCity).take(1).subscribeOn(Schedulers.io());
    }
}
