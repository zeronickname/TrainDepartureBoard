package uk.me.gman.trains.model;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;


public class TrainServices {
    @SerializedName("origin")
    private List<CRSInfo> origin;

    @SerializedName("destination")
    private List<CRSInfo> destination;

    @SerializedName("std")
    private String std;

    @SerializedName("etd")
    private String etd;


    public TrainServices(String origin, String destination, String std, String etd) {
        CRSInfo orCrs = new CRSInfo(origin);
        this.origin = Collections.singletonList(orCrs);
        CRSInfo dstCrs = new CRSInfo(destination);
        this.destination = Collections.singletonList(dstCrs);
        this.std = std;
        this.etd = etd;
    }

    public String getOrigin() {
        return origin.get(0).getCRS();
    }
    public void setOrigin( String origin ) {
        CRSInfo crs = new CRSInfo(origin);
        this.origin.set(0, crs);
    }

    public String getDestination() {
        return destination.get(0).getlocation();
    }
    public void setDestination( String destination ) {
        CRSInfo crs = new CRSInfo(destination);
        this.destination.set(0, crs);

    }

    public String getStd() {
        return std;
    }
    public void setStd( String std ) {
        this.std = std;
    }

    public String getEtd() {
        return etd;
    }
    public void setEtd( String etd ) {
        this.etd = etd;
    }
}
