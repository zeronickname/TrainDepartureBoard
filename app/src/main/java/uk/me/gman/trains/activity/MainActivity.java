package uk.me.gman.trains.activity;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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

    private String origin = "twy";
    static final Map<String, String> destinations = ImmutableMap.of(
            "did", "Didcot",
            "slo", "Slough",
            "rdg", "Reading",
            "pad", "Paddington",
            "hot", "Henley"
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        RecyclerView mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        data = genDummyData();
        mAdapter = new TrainsAdapter(data, R.layout.content_card_view, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        observables = new ArrayList<>();

        for (String dest : destinations.keySet() ) {
            Observable<Response<LocationInfo>> tmp = apiService.getDepartures(origin, dest);
            observables.add(tmp);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Timer autoUpdate = new Timer();
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
        final HashMap<String, List<TrainServices>> recdData = new HashMap<>();

        Observable.merge(observables)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Response<LocationInfo>>() {
                    @Override
                    public void onCompleted() {
                        data.clear();

                        // reorder items to what's defined in the hashmap
                        for (Map.Entry<String, String> entry : destinations.entrySet()) {
                            List<TrainServices> trainServices = recdData.get(entry.getKey());
                            String dest = entry.getValue();
                            if( trainServices != null ) {
                                data.add(new DataObject(dest, trainServices));
                            }
                        }
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
                        if( locationInfo != null ) {
                            List<TrainServices> trainServices = locationInfo.getTrainServices();
                            recdData.put(response.raw().request().url().pathSegments().get(3), trainServices);
                        }
                    }
                });
    }

    private ArrayList<DataObject> genDummyData() {
        TrainServices trains = new TrainServices("waiting", "waiting", "00:00", "On time");
        DataObject obj = new DataObject("waiting", Collections.singletonList(trains));
        return new ArrayList<>(Collections.singletonList(obj));
    }


    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        private GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
