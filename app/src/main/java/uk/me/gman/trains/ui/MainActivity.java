package uk.me.gman.trains.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import timber.log.Timber;
import uk.me.gman.trains.R;
import uk.me.gman.trains.data.database.TrainEntry;
import uk.me.gman.trains.databinding.ActivityCardViewBinding;
import uk.me.gman.trains.helpers.MqttHelper;
import uk.me.gman.trains.model.DataObject;
import uk.me.gman.trains.model.TrainServices;

public class MainActivity extends DaggerAppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ActivityCardViewBinding binding;

    @Inject
    MainViewModelFactory viewModelFactory;

    MainViewModel viewModel;

    MqttHelper mqttHelper;
    private TrainsAdapter mAdapter;
    private ArrayList<DataObject> data;

    private String origin = "twy";
    static final ImmutableMultimap<String, String> destinations = ImmutableMultimap.of(
            "did", "Didcot",
            "slo", "Slough",
            "rdg", "Reading",
            "pad", "Paddington",
            "hot", "Henley"
    );
    private List<String> keys = new ArrayList<>(destinations.keys());

    final ImmutableMap<String, Integer> icon_lookup = ImmutableMap.<String, Integer>builder()
            .put("clear-day", R.string.wi_day_sunny)
            .put("clear-night", R.string.wi_night_clear)
            .put("rain", R.string.wi_rain)
            .put("snow", R.string.wi_snow)
            .put("sleet", R.string.wi_sleet)
            .put("wind", R.string.wi_strong_wind)
            .put("fog", R.string.wi_fog)
            .put("cloudy", R.string.wi_cloudy)
            .put("partly-cloudy-day", R.string.wi_day_cloudy)
            .put("partly-cloudy-night", R.string.wi_night_alt_cloudy)
            .put("hail", R.string.wi_hail)
            .put("thunderstorm", R.string.wi_thunderstorm)
            .put("tornado", R.string.wi_tornado)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_card_view);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        /*
        ScreenManager screenManager = new ScreenManager(Display.DEFAULT_DISPLAY);
        // Set brightness to a fixed value
        screenManager.setBrightnessMode(ScreenManager.BRIGHTNESS_MODE_MANUAL);
        screenManager.setBrightness(255); //Max it out.
        */

        binding.trainTimes.setHasFixedSize(true);
        //binding.trainTimes.setLayoutManager(new GridLayoutManager(this, 2));
        binding.trainTimes.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));

        data = genDummyData();
        mAdapter = new TrainsAdapter(data, R.layout.content_card_view, getApplicationContext());
        binding.trainTimes.setAdapter(mAdapter);

        binding.swipeRefresh.setOnRefreshListener(this);

        startMqtt();

        viewModel.getTrains().observe(this, trains -> {
            if (trains != null) {
                Timber.d("New train data " + trains.toString());
                for (TrainEntry t : trains) {
                    /*
                    String crx = response.raw().request().url().pathSegments().get(3);
                    List<TrainServices> trainServices = locationInfo.getTrainServices();
                    String dest = destinations.get(crx).asList().get(0);

                    data.set( keys.indexOf(crx), new DataObject(dest, trainServices));
                    mAdapter.notifyDataSetChanged();
                     */
                }
            }
        });

        viewModel.getTrainsLoading().observe(this, loading -> {
            if (loading != null)
                binding.swipeRefresh.setRefreshing(loading);
        });

        viewModel.getWeather().observe(this, weather -> {
            if (weather != null)
                binding.weatherIcon.setIconResource(getString(icon_lookup.get(weather.getIcon())));
        });
    }

    @Override
    public void onRefresh() {
        viewModel.refreshTrains();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mqttHelper.disconnect();
    }

    private ArrayList<DataObject> genDummyData() {
        data = new ArrayList<>();
        for (String dest : destinations.keySet() ) {
            TrainServices trains = new TrainServices("waiting", dest, "waiting", "On time");
            DataObject obj = new DataObject("waiting", Collections.singletonList(trains));
            data.add(obj);
        }
        return data;
    }


    private void startMqtt(){
        mqttHelper = new MqttHelper(this.getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) {
                Log.w("Debug",mqttMessage.toString());
                String text = topic.split("/")[1];
                Resources res = getApplicationContext().getResources();
                switch( text ) {
                    case "temp":
                        binding.temp.setText(res.getString(R.string.temp, mqttMessage.toString()));
                        break;
                    case "humi":
                        binding.relH.setText(res.getString(R.string.humi, mqttMessage.toString()));
                        break;
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
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
