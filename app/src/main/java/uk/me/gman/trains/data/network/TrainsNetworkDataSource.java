package uk.me.gman.trains.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import uk.me.gman.trains.AppExecutors;
import uk.me.gman.trains.BuildConfig;
import uk.me.gman.trains.data.database.TrainEntry;
import uk.me.gman.trains.data.database.WeatherEntry;
import uk.me.gman.trains.model.DarkSky;
import uk.me.gman.trains.model.LocationInfo;
import uk.me.gman.trains.rest.ApiInterface;
import uk.me.gman.trains.rest.ForecastInterface;

@Singleton
public class TrainsNetworkDataSource {

    private static final int TRAINS_SYNC_INTERVAL_SECONDS = 40;
    private static final int TRAINS_SYNC_FLEXTIME_SECONDS = TRAINS_SYNC_INTERVAL_SECONDS / 4;
    private static final int WEATHER_SYNC_INTERVAL_SECONDS = (int) TimeUnit.MINUTES.toSeconds(5);
    private static final int WEATHER_SYNC_FLEXTIME_SECONDS = WEATHER_SYNC_INTERVAL_SECONDS / 3;

    private static final String TRAINS_SYNC_TAG = "trains-sync";
    private static final String WEATHER_SYNC_TAG = "weather-sync";

    private static final String[] destinations = {"did", "slo", "rdg", "pad", "hot"};

    private final Context appContext;
    private final AppExecutors appExecutors;
    private final ApiInterface apiInterface;
    private final ForecastInterface forecastInterface;

    private final MutableLiveData<List<TrainEntry>> downloadedTrains;
    private final MutableLiveData<WeatherEntry> downloadedWeather;

    private final MutableLiveData<Boolean> trainsLoading;
    private final MutableLiveData<Boolean> weatherLoading;
    private int trainCount;
    private ArrayList<TrainEntry> trains;

    @Inject
    public TrainsNetworkDataSource(Context context, AppExecutors executors, ApiInterface api,
                                   ForecastInterface forecast) {
        appContext = context;
        appExecutors = executors;
        apiInterface = api;
        forecastInterface = forecast;

        downloadedTrains = new MutableLiveData<>();
        downloadedWeather = new MutableLiveData<>();

        trainsLoading = new MutableLiveData<>();
        weatherLoading = new MutableLiveData<>();
    }

    public LiveData<List<TrainEntry>> getTrains() {
        return downloadedTrains;
    }

    public LiveData<Boolean> getTrainsLoading() {
        return trainsLoading;
    }

    public LiveData<WeatherEntry> getWeather() {
        return downloadedWeather;
    }

    public LiveData<Boolean> getWeatherLoading() {
        return weatherLoading;
    }

    public void startFetchTrainsService() {
        Intent intentToFetch = new Intent(appContext, TrainsIntentService.class);
        appContext.startService(intentToFetch);
        Timber.d("Trains service created");
    }

    public void startFetchWeatherService() {
        Intent intentToFetch = new Intent(appContext, WeatherIntentService.class);
        appContext.startService(intentToFetch);
        Timber.d("Weather service created");
    }

    public void scheduleRecurringFetchTrains() {
        Driver driver = new GooglePlayDriver(appContext);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        // Create the Job to periodically sync train data
        Job syncTrainsJob = dispatcher.newJobBuilder()
                .setService(TrainsJobService.class)
                .setTag(TRAINS_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(TRAINS_SYNC_INTERVAL_SECONDS, TRAINS_SYNC_INTERVAL_SECONDS + TRAINS_SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        // Schedule the Job with the dispatcher
        dispatcher.schedule(syncTrainsJob);
        Timber.d("Trains job scheduled");
    }

    public void scheduleRecurringFetchWeather() {
        Driver driver = new GooglePlayDriver(appContext);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        // Create the Job to periodically sync train data
        Job syncTrainsJob = dispatcher.newJobBuilder()
                .setService(WeatherJobService.class)
                .setTag(WEATHER_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(WEATHER_SYNC_INTERVAL_SECONDS, WEATHER_SYNC_INTERVAL_SECONDS + WEATHER_SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        // Schedule the Job with the dispatcher
        dispatcher.schedule(syncTrainsJob);
        Timber.d("Weather job scheduled");
    }

    void fetchTrains() {
        trainsLoading.postValue(true);
        Timber.d("Fetch trains started");
        trains = new ArrayList<>();
        trainCount = 0;

        appExecutors.networkIO().execute(() -> {
            for (String to : destinations) {
                Call<LocationInfo> call = apiInterface.getDepartures("twy", to);
                call.enqueue(new Callback<LocationInfo>() {
                    @Override
                    public void onResponse(Call<LocationInfo> call, Response<LocationInfo> response) {
                        LocationInfo locationInfo = response.body();
                        if (locationInfo != null) {
                            trains.add(new TrainEntry(locationInfo));
                        }
                        checkLoading();
                    }

                    @Override
                    public void onFailure(Call<LocationInfo> call, Throwable t) {
                        Timber.w("Failed to fetch train info: " + t.getMessage());
                        checkLoading();
                    }
                });
            }
        });
    }

    private void checkLoading() {
        trainCount++;
        if (trainCount == destinations.length) {
            Timber.d("Finished fetching train data");
            if (trains.size() != destinations.length)
                Timber.w("Only " + trains.size() + " of " + destinations.length + " stations refreshed");
            downloadedTrains.postValue(trains);
            trainsLoading.postValue(false);
        }
    }

    void fetchWeather() {
        weatherLoading.postValue(true);
        Timber.d("Fetch weather started");
        appExecutors.networkIO().execute(() -> {
            Call<DarkSky> call = forecastInterface.getForecast(BuildConfig.API_ID, BuildConfig.LOCATION);
            call.enqueue(new Callback<DarkSky>() {
                @Override
                public void onResponse(Call<DarkSky> call, Response<DarkSky> response) {
                    DarkSky forecast = response.body();
                    if( forecast != null ) {
                        downloadedWeather.setValue(new WeatherEntry(forecast));
                    }
                }

                @Override
                public void onFailure(Call<DarkSky> call, Throwable t) {
                    Timber.w("Failed to fetch weather: " + t.getMessage());
                    weatherLoading.postValue(false);
                }
            });
        });
    }
}
