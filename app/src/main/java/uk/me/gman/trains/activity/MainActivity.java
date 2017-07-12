package uk.me.gman.trains.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.HttpUrl;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.me.gman.trains.R;
import uk.me.gman.trains.adapter.TrainsAdapter;
import uk.me.gman.trains.model.DataObject;
import uk.me.gman.trains.model.LocationInfo;
import uk.me.gman.trains.model.TrainServices;
import uk.me.gman.trains.rest.ApiClient;
import uk.me.gman.trains.rest.ApiInterface;


public class MainActivity extends AppCompatActivity {

    private TrainsAdapter mAdapter;
    private ArrayList<DataObject> data;
    private static String LOG_TAG = "MainActivity";
    private List<Observable<Response<LocationInfo>>> observables;
    private Timer autoUpdate;
    private ApiInterface apiService;

    private String origin = "twy";
    static final Map<String, String> destinations = ImmutableMap.of(
            "did", "Didcot",
            "slo", "Slough",
            "rdg", "Reading",
            "pad", "Paddington"
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        RecyclerView mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        data = genDummyData();
        mAdapter = new TrainsAdapter(data, R.layout.content_card_view, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        //refreshData();

        apiService = ApiClient.getClient().create(ApiInterface.class);
        observables = new ArrayList<>();

        for (String dest : destinations.keySet() ) {
            Observable<Response<LocationInfo>> tmp = apiService.getDepartures(origin, dest);
            observables.add(tmp);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        autoUpdate = new Timer();
        autoUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        refreshData();
                    }
                });
            }
        }, 0, 40000); // updates each 40 secs
    }

    private void refreshData(){
        data.clear();

        Observable.merge(observables)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Response<LocationInfo>>() {
                    @Override
                    public void onCompleted() {
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // Log error here since request failed
                        Log.e(LOG_TAG, e.toString());
                    }

                    @Override
                    public void onNext(/*LocationInfo locationInfo*/Response response) {
                        LocationInfo locationInfo = (LocationInfo) response.body();
                        List<TrainServices> trainServices = locationInfo.getTrainServices();
                        String dest = response.raw().request().url().pathSegments().get(3);
                        for (Map.Entry<String, String> entry : destinations.entrySet()) {
                            dest = dest.replace(entry.getKey(), entry.getValue());
                        }
                        data.add(new DataObject(dest, trainServices));
                        HttpUrl url = response.raw().request().url();
                        Log.e(LOG_TAG, url.encodedPath());
                    }
                });
    }

    private ArrayList<DataObject> genDummyData() {
        TrainServices trains = new TrainServices("waiting", "waiting", "00:00", "On time");
        DataObject obj = new DataObject("waiting", Collections.singletonList(trains));
        return new ArrayList<>(Collections.singletonList(obj));
    }
}
