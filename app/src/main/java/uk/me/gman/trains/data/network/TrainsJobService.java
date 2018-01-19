package uk.me.gman.trains.data.network;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import timber.log.Timber;

public class TrainsJobService extends JobService {

    @Inject
    TrainsNetworkDataSource networkDataSource;

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Override
    public boolean onStartJob(JobParameters job) {
        networkDataSource.fetchTrains();
        jobFinished(job, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Timber.d("Trains job stopped");
        return true;
    }
}
