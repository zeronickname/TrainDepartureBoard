package uk.me.gman.trains.activity;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.google.common.collect.ImmutableMultimap;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.me.gman.trains.R;
import uk.me.gman.trains.adapter.TrainsAdapter;
import uk.me.gman.trains.helpers.ChartHelper;
import uk.me.gman.trains.helpers.MqttHelper;
import uk.me.gman.trains.model.DataObject;
import uk.me.gman.trains.model.LocationInfo;
import uk.me.gman.trains.model.TrainServices;
import uk.me.gman.trains.rest.ApiClient;
import uk.me.gman.trains.rest.ApiInterface;


public class MainActivity extends AppCompatActivity {

    TextView dispTemp;
    TextView dispRH;
    MqttHelper mqttHelper;
    ChartHelper mChart;
    LineChart chart;
    private TrainsAdapter mAdapter;
    private ArrayList<DataObject> data;
    private static String LOG_TAG = "MainActivity";

    private String origin = "twy";
    static final ImmutableMultimap<String, String> destinations = ImmutableMultimap.of(
            "did", "Didcot",
            "slo", "Slough",
            "rdg", "Reading",
            "pad", "Paddington",
            "hot", "Henley"
    );
    private List<String> keys = new ArrayList<>(destinations.keys());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        chart = findViewById(R.id.chart);
        mChart = new ChartHelper(chart);

        dispRH = findViewById(R.id.relH);
        dispTemp = findViewById(R.id.temp);

        RecyclerView mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,1));
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        data = genDummyData();
        mAdapter = new TrainsAdapter(data, R.layout.content_card_view, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        startMqtt();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mqttHelper.disconnect();
    }

    private void refreshData(){
        for (String dest : destinations.keySet() ) {
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<LocationInfo> api = apiService.getDepartures(origin, dest);
            api.enqueue(new Callback<LocationInfo>() {
                @Override
                public void onResponse(Call<LocationInfo> call, Response<LocationInfo> response) {

                    LocationInfo locationInfo = response.body();
                    if( locationInfo != null ) {
                        String crx = response.raw().request().url().pathSegments().get(3);
                        List<TrainServices> trainServices = locationInfo.getTrainServices();
                        String dest = destinations.get(crx).asList().get(0);

                        data.set( keys.indexOf(crx), new DataObject(dest, trainServices));
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<LocationInfo> call, Throwable t) {
                    Log.d("Error",t.getMessage());
                }
            });
        }
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
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug",mqttMessage.toString());
                String text = topic.split("/")[1];
                Resources res = getApplicationContext().getResources();
                switch( text ) {
                    case "temp":
                        dispTemp.setText(res.getString(R.string.temp, mqttMessage.toString()));
                        break;
                    case "humi":
                        String text1 = res.getString(R.string.humi, mqttMessage.toString());
                        dispRH.setText(text1);
                        break;
                }

                mChart.addEntry(text, Float.valueOf(mqttMessage.toString()));
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
