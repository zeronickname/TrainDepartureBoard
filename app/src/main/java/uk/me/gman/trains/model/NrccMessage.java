package uk.me.gman.trains.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NrccMessage {

    @SerializedName("value")
    @Expose
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
