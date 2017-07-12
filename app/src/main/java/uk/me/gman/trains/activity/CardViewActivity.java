package uk.me.gman.trains.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Scheduler;
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


public class CardViewActivity extends AppCompatActivity {

    private TrainsAdapter mAdapter;
    private ArrayList<DataObject> data;
    private static String LOG_TAG = "CardViewActivity";
    private Timer autoUpdate;

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
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Observable<LocationInfo> did = apiService.getDepartures("twy", "did");
        Observable<LocationInfo> slo = apiService.getDepartures("twy", "slo");

        Observable.merge(slo, did)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<LocationInfo>() {
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
                    public void onNext(LocationInfo locationInfo) {
                        List<TrainServices> trainServices = locationInfo.getTrainServices();
                        data.add(new DataObject(trainServices));
                    }
                });
    }

    private ArrayList<DataObject> genDummyData() {
        TrainServices trains = new TrainServices("waiting", "waiting", "00:00", "On time");
        DataObject obj = new DataObject(Collections.singletonList(trains));
        return new ArrayList<>(Collections.singletonList(obj));
    }
}
