package uk.me.gman.trains.data.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import uk.me.gman.trains.model.LocationInfo;

@Entity(tableName = "trains")
@TypeConverters(Converters.class)
public class TrainEntry {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "crs")
    private final String crs;

    @ColumnInfo(name = "name")
    private final String name;

    @ColumnInfo(name = "generated_time")
    private final String generatedTime;

    @ColumnInfo(name = "std")
    private final String std;

    @ColumnInfo(name = "etd")
    private final String etd;

    @ColumnInfo(name = "messages")
    private final String messages;

    public TrainEntry(@NonNull String crs, String name, String generatedTime, String std,
                      String etd, String messages) {
        this.crs = crs;
        this.name = name;
        this.generatedTime = generatedTime;
        this.std = std;
        this.etd = etd;
        this.messages = messages;
    }

    @Ignore
    public TrainEntry(LocationInfo info) {
        crs = info.getFiltercrs();
        name = info.getFilterLocationName();
        generatedTime = info.getGeneratedTime();
        std = info.getTrainServices().get(0).getStd();
        etd = info.getTrainServices().get(0).getEtd();
        if (info.getNrccMessages() != null) {
            messages = info.getNrccMessages().get(0).getValue();
        } else {
            messages = "";
        }
    }

    @NonNull
    public String getCrs() {
        return crs;
    }

    public String getName() {
        return name;
    }

    public String getGeneratedTime() {
        return generatedTime;
    }

    public String getStd() {
        return std;
    }

    public String getEtd() {
        return etd;
    }

    public String getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return "TrainEntry{"
                + "crs=" + crs
                + ", name=" + name
                + ", generatedTime=" + generatedTime
                + ", std=" + std
                + ", etd=" + etd
                + ", messages=" + messages
                + '}';
    }
}
