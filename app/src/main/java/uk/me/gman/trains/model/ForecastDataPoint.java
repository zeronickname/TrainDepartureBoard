package uk.me.gman.trains.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastDataPoint {
    @SerializedName("data")
    private List<Forecast> data;

    @SerializedName("icon")
    private String icon;

    @SerializedName("summary")
    private String summary;

    public List<Forecast> getData() {return data;}
}
