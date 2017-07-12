package uk.me.gman.trains.model;

import android.content.Context;
import android.content.res.Resources;

import java.util.List;

import uk.me.gman.trains.R;


public class DataObject {
    private LocationInfo trains;
    private String destination;

    public DataObject( String destination, List<TrainServices> trains ) {
        this.trains = new LocationInfo(trains);
        this.destination = destination;
    }

    public LocationInfo getLocInfo() { return trains; }
    public String getDestination() {return destination; }

    public String getEtd(){ return trains.getTrainServices().get(0).getEtd(); }

    public String getTitle( Context context ) {
        Resources res = context.getResources();
        return res.getString(R.string.to, destination, trains.getTrainServices().get(0).getEtd());
    }
    public String getDescription( Context context ) {
        Resources res = context.getResources();
        int id = R.string.descriptionDelayed;
        if( trains.getTrainServices().get(0).getEtd().equals("On time")) {
            id = R.string.descriptionOnTime;
        }
        return res.getString(id, trains.getTrainServices().get(0).getStd(), trains.getTrainServices().get(0).getEtd());
    }
}
