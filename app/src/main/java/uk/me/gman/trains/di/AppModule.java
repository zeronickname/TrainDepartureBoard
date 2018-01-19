package uk.me.gman.trains.di;

import android.app.Application;
import android.arch.persistence.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.me.gman.trains.AppExecutors;
import uk.me.gman.trains.data.TrainsRepository;
import uk.me.gman.trains.data.database.TrainsDao;
import uk.me.gman.trains.data.database.TrainsDatabase;
import uk.me.gman.trains.data.network.TrainsNetworkDataSource;
import uk.me.gman.trains.rest.ApiInterface;
import uk.me.gman.trains.rest.ForecastInterface;

@Module
public class AppModule {

    @Singleton @Provides
    TrainsRepository provideRepository(TrainsDao dao, TrainsNetworkDataSource networkDataSource,
                                       AppExecutors executors) {
        return new TrainsRepository(dao, networkDataSource, executors);
    }

    @Singleton @Provides
    ApiInterface provideApiInterface() {
        return new Retrofit.Builder()
                .baseUrl("http://nationalrail-3.apphb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface.class);
    }

    @Singleton @Provides
    ForecastInterface provideForecastInterface() {
        return new Retrofit.Builder()
                .baseUrl("https://api.darksky.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ForecastInterface.class);
    }

    @Singleton @Provides
    TrainsNetworkDataSource provideNetworkDataSource(Application app, AppExecutors executors,
                                                     ApiInterface api, ForecastInterface forecast) {
        return new TrainsNetworkDataSource(app.getApplicationContext(), executors, api, forecast);
    }

    @Singleton @Provides
    TrainsDatabase provideDatabase(Application app) {
        return Room.databaseBuilder(app, TrainsDatabase.class, TrainsDatabase.NAME)
                .fallbackToDestructiveMigration().build();
    }

    @Singleton @Provides
    TrainsDao provideDao(TrainsDatabase database) {
        return database.trainsDao();
    }
}
