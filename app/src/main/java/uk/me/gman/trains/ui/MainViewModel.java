package uk.me.gman.trains.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import uk.me.gman.trains.data.TrainsRepository;
import uk.me.gman.trains.data.database.TrainEntry;
import uk.me.gman.trains.data.database.WeatherEntry;

public class MainViewModel extends ViewModel {

    private TrainsRepository trainsRepository;

    private final LiveData<List<TrainEntry>> trains;
    private final LiveData<Boolean> loading;
    private final LiveData<WeatherEntry> weather;

    @Inject
    public MainViewModel(TrainsRepository repository) {
        trainsRepository = repository;

        trains = trainsRepository.getObservableTrains();
        loading = trainsRepository.getTrainsLoading();
        weather = trainsRepository.getObservableWeather();
    }

    public LiveData<List<TrainEntry>> getTrains() {
        return trains;
    }

    public LiveData<Boolean> getTrainsLoading() {
        return loading;
    }

    public LiveData<WeatherEntry> getWeather() {
        return weather;
    }

    public void refreshTrains() {
        trainsRepository.startFetchTrainsService();
    }
}
