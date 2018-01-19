package uk.me.gman.trains.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LocationInfo {

    @SerializedName("trainServices")
    @Expose
    private List<TrainServices> trainServices;

    @SerializedName("generatedAt")
    @Expose
    private String generatedTime;

    @SerializedName("locationName")
    @Expose
    private String locationName;

    @SerializedName("crs")
    @Expose
    private String crs;

    @SerializedName("filterLocationName")
    @Expose
    private String filterLocationName;

    @SerializedName("filtercrs")
    @Expose
    private String filtercrs;

    @SerializedName("nrccMessages")
    @Expose
    private List<NrccMessage> nrccMessages = null;

    public List<TrainServices> getTrainServices() { return trainServices; }
    public void setTrainServices(List<TrainServices> services ) {
        this.trainServices = services;
    }

    public String getGeneratedTime() {return generatedTime; }
    public void setGeneratedTime( String generatedTime ) {
        this.generatedTime = generatedTime;
    }

    public String getLocationName() { return locationName; }
    public void setLocationName( String locationName ) {
        this.locationName = locationName;
    }

    public String getCrs() { return crs; }
    public void setCrs( String crs ) {
        this.crs = crs;
    }

    public String getFilterLocationName() { return filterLocationName; }
    public void setFilterLocationName( String filterLocationName) {
        this.filterLocationName = filterLocationName;
    }

    public String getFiltercrs() { return filtercrs; }
    public void setFiltercrs(String filtercrs) {
        this.filtercrs = filtercrs;
    }

    public List<NrccMessage> getNrccMessages() { return nrccMessages; }
    public void setNrccMessages(List<NrccMessage> nrccMessages) {
        this.nrccMessages = nrccMessages;
    }
}
