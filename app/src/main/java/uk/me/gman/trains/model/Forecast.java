package uk.me.gman.trains.model;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    @SerializedName("icon")
    private String icon;

    @SerializedName("summary")
    private String summary;

    public String getIcon() {return icon;}
    public String getSummary() {return summary;}
}
