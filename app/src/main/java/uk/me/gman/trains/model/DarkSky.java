package uk.me.gman.trains.model;

import com.google.gson.annotations.SerializedName;

public class DarkSky {
    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("timezone")
    private String timezone;

    @SerializedName("daily")
    private ForecastDataPoint daily;

    public String getIcon() { return daily.getData().get(0).getIcon(); }
    public String getSummary() {return daily.getData().get(0).getSummary(); }
}
