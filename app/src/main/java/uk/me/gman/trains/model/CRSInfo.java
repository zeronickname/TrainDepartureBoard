package uk.me.gman.trains.model;

import com.google.gson.annotations.SerializedName;

public class CRSInfo {
    @SerializedName("locationName")
    private String locationName;

    @SerializedName("crs")
    private String crs;

    CRSInfo(String locationName ) {
        this.locationName = locationName;
    }

    public String getlocation() {
        return locationName;
    }
    public void setLocation( String locationName ) {
        this.locationName = locationName;
    }

    public String getCRS() {
        return crs;
    }
    public void setDestination( String crs ) {
        this.crs = crs;
    }

}
