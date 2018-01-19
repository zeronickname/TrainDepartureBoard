package uk.me.gman.trains.data.network;

import android.content.Intent;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import dagger.android.DaggerIntentService;

public class TrainsIntentService extends DaggerIntentService {

    @Inject
    TrainsNetworkDataSource networkDataSource;

    public TrainsIntentService() {
        super("TrainsIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        networkDataSource.fetchTrains();
    }
}
