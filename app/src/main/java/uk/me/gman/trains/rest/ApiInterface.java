package uk.me.gman.trains.rest;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;
import uk.me.gman.trains.model.LocationInfo;

public interface ApiInterface {
    @GET("departures/{from}/to/{to}")
    Call<LocationInfo> getDepartures(@Path("from") String from, @Path("to") String to );

    @GET("delays/{from}/to/{to}/5")
    Call<LocationInfo> getDelays(@Path("from") String from, @Path("to") String to );
}
