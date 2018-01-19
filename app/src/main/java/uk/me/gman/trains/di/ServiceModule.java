package uk.me.gman.trains.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import uk.me.gman.trains.data.network.TrainsIntentService;
import uk.me.gman.trains.data.network.TrainsJobService;
import uk.me.gman.trains.data.network.WeatherIntentService;
import uk.me.gman.trains.data.network.WeatherJobService;

@Module
public abstract class ServiceModule {

    @ContributesAndroidInjector
    abstract TrainsIntentService contributeTrainsIntentService();

    @ContributesAndroidInjector
    abstract TrainsJobService contributeTrainsJobService();

    @ContributesAndroidInjector
    abstract WeatherIntentService contributeWeatherIntentService();

    @ContributesAndroidInjector
    abstract WeatherJobService contributeWeatherJobService();
}
