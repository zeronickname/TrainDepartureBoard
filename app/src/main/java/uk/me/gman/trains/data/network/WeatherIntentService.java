package uk.me.gman.trains.data.network;

import android.content.Intent;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import dagger.android.DaggerIntentService;

public class WeatherIntentService extends DaggerIntentService {

    @Inject
    TrainsNetworkDataSource networkDataSource;

    public WeatherIntentService() {
        super("WeatherIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        networkDataSource.fetchWeather();
    }
}
