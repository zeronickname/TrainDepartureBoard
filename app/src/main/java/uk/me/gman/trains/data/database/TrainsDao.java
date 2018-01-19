package uk.me.gman.trains.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;
import android.database.sqlite.SQLiteException;

import java.util.List;

@Dao
public interface TrainsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addTrain(TrainEntry data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addTrains(List<TrainEntry> data);

    @Query("SELECT * FROM trains")
    LiveData<List<TrainEntry>> getObservableTrains();

    @Query("SELECT * FROM trains")
    List<TrainEntry> getTrains();

    @Query("SELECT COUNT(*) FROM trains")
    int getTrainsSize();

    @Update
    void updateTrain(TrainEntry data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addWeather(WeatherEntry data);

    @Query("SELECT * FROM weather")
    LiveData<WeatherEntry> getObservableWeather();

    @Query("SELECT COUNT(*) FROM weather")
    int getWeatherSize();

    @Query("DELETE FROM weather")
     void deleteWeather();
    /*
    @Transaction
    void saveWeather(WeatherEntry data) {
        if (getWeatherSize() > 0) {
            deleteWeather();
        }
        addWeather(data);
    }*/
}
