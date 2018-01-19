package uk.me.gman.trains.data.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import java.time.LocalDateTime;

import uk.me.gman.trains.model.DarkSky;

@Entity(tableName = "weather")
@TypeConverters(Converters.class)
public class WeatherEntry {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "time")
    private final LocalDateTime time;

    @ColumnInfo(name = "icon")
    private final String icon;

    @ColumnInfo(name = "summary")
    private final String summary;

    public WeatherEntry(@NonNull LocalDateTime time, String icon, String summary) {
        this.time = time;
        this.icon = icon;
        this.summary = summary;
    }

    @Ignore
    public WeatherEntry(DarkSky data) {
        time = LocalDateTime.now();
        icon = data.getIcon();
        summary = data.getSummary();
    }

    @NonNull
    public LocalDateTime getTime() {
        return time;
    }

    public String getIcon() {
        return icon;
    }

    public String getSummary() {
        return summary;
    }
}
