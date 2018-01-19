package uk.me.gman.trains.model;

import android.content.Context;
import android.content.res.Resources;

import java.util.List;

import uk.me.gman.trains.R;


public class DataObject {
    private LocationInfo trains;
    private String destination;

    public DataObject( String destination, List<TrainServices> trains ) {
        LocationInfo info = new LocationInfo();
        info.setTrainServices(trains);
        this.trains = info;
        this.destination = destination;
    }

    public LocationInfo getLocInfo() { return trains; }
    public String getDestination() {return destination; }

    public String getEtd( int position ){ return trains.getTrainServices().get(position).getEtd(); }

    public String getTitle( Context context, int position ) {
        Resources res = context.getResources();
        String departureTime = trains.getTrainServices().get(position).getEtd();
        if( departureTime.equals("On time") ) {
            departureTime = trains.getTrainServices().get(position).getStd();
        }
        return res.getString(R.string.to, destination, departureTime);
    }
    public String getDescription( Context context, int position ) {
        Resources res = context.getResources();
        int id = R.string.descriptionDelayed;
        if( trains.getTrainServices().get(position).getEtd().equals("On time")) {
            id = R.string.descriptionOnTime;
        }
        return res.getString(id, trains.getTrainServices().get(position).getStd(), trains.getTrainServices().get(0).getEtd());
    }
}
