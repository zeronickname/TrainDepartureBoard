package uk.me.gman.trains.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {TrainEntry.class, WeatherEntry.class},
    version = 1, exportSchema = false)
public abstract class TrainsDatabase extends RoomDatabase {

    public static final String NAME = "TrainsDb";

    public abstract TrainsDao trainsDao();
}
