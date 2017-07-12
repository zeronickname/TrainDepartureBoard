package uk.me.gman.trains.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LocationInfo {
    @SerializedName("trainServices")
    private List<TrainServices> trainServices;

    @SerializedName("locationName")
    private String locationName;

    @SerializedName("generatedAt")
    private String generatedTime;

    @SerializedName("totalTrainsDelayed")
    private String totalTrainsDelayed;

    @SerializedName("totalDelayMinutes")
    private String totalDelayMinutes;

    public LocationInfo(List<TrainServices> trainServices) {
        this.trainServices = trainServices;
        locationName = "waiting";
        generatedTime = "waiting";
        totalTrainsDelayed = "waiting";
        totalDelayMinutes = "waiting";
    }


    public List<TrainServices> getTrainServices() { return trainServices; }
    public void setTrainServices(List<TrainServices> services ) {
        this.trainServices = services;
    }

    public String getLocationName() { return locationName; }
    public void setLocationName( String locationName ) {
        this.locationName = locationName;
    }

    public String getGeneratedTime() {return generatedTime; }
    public void setGeneratedTime( String generatedTime ) {
        this.generatedTime = generatedTime;
    }

    public String getTotalTrainsDelayed() {return totalTrainsDelayed; }
    public void setTotalTrainsDelayed( String totalTrainsDelayed ) {
        this.totalTrainsDelayed = totalTrainsDelayed;
    }

    public String getTotalDelayedMins() {return totalDelayMinutes; }
    public void setTotalDelayedMins( String totalDelayMinutes ) {
        this.totalDelayMinutes = totalDelayMinutes;
    }
}
