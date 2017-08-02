package uk.me.gman.trains.rest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import uk.me.gman.trains.model.DarkSky;


public interface ForecastInterface {

    @GET("forecast/{api}/{location}")
    Call<DarkSky> getForecast(@Path("api") String api, @Path("location") String location );

}
