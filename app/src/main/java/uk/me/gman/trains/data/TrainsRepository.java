package uk.me.gman.trains.data;

import android.arch.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;
import uk.me.gman.trains.AppExecutors;
import uk.me.gman.trains.data.database.TrainEntry;
import uk.me.gman.trains.data.database.TrainsDao;
import uk.me.gman.trains.data.database.WeatherEntry;
import uk.me.gman.trains.data.network.TrainsNetworkDataSource;

@Singleton
public class TrainsRepository {

    private final TrainsDao trainsDao;
    private final TrainsNetworkDataSource trainsNetworkDataSource;
    private final AppExecutors appExecutors;

    private boolean initialised = false;

    @Inject
    public TrainsRepository(TrainsDao dao, TrainsNetworkDataSource networkDataSource,
                            AppExecutors executors) {
        trainsDao = dao;
        trainsNetworkDataSource = networkDataSource;
        appExecutors = executors;

        LiveData<List<TrainEntry>> trains = trainsNetworkDataSource.getTrains();
        trains.observeForever(data -> appExecutors.diskIO().execute(() -> {
            if(data != null)
                 trainsDao.addTrains(data);
        }));

        LiveData<WeatherEntry> weather = trainsNetworkDataSource.getWeather();
        weather.observeForever(data -> appExecutors.diskIO().execute(() -> {
            if (data != null) {
                if (trainsDao.getWeatherSize() > 0) {
                    trainsDao.deleteWeather();
                    trainsDao.addWeather(data);
                } else {
                    trainsDao.addWeather(data);
                }
            }
        }));
    }

    private synchronized void initialiseData() {
        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (initialised) return;
        initialised = true;

        trainsNetworkDataSource.scheduleRecurringFetchTrains();
        trainsNetworkDataSource.scheduleRecurringFetchWeather();

        appExecutors.diskIO().execute(() -> {
            startFetchTrainsService();
            startFetchWeatherService();
        });
    }

    public LiveData<List<TrainEntry>> getObservableTrains() {
        initialiseData();

        return trainsDao.getObservableTrains();
    }

    public LiveData<WeatherEntry> getObservableWeather() {
        initialiseData();

        return trainsDao.getObservableWeather();
    }

    public LiveData<Boolean> getTrainsLoading() {
        return trainsNetworkDataSource.getTrainsLoading();
    }

    public LiveData<Boolean> getWeatherLoading() {
        return trainsNetworkDataSource.getWeatherLoading();
    }

    public void startFetchTrainsService() {
        trainsNetworkDataSource.startFetchTrainsService();
    }

    public void startFetchWeatherService() {
        trainsNetworkDataSource.startFetchWeatherService();
    }
}
