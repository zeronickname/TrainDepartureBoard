package uk.me.gman.trains.ui;

import android.databinding.BindingAdapter;
import android.view.View;

import com.github.pwittchen.weathericonview.WeatherIconView;

import uk.me.gman.trains.R;

public class BindingAdapters {

    @BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("weatherIcon")
    public static void convertIcon(WeatherIconView view, String iconId) {
        if (iconId != null) {
            switch (iconId) {
                case "clear-day": view.setText(R.string.wi_day_sunny); break;
                case "clear-night": view.setText(R.string.wi_night_clear); break;
                case "rain": view.setText(R.string.wi_rain); break;
                case "snow": view.setText(R.string.wi_snow); break;
                case "sleet": view.setText(R.string.wi_sleet); break;
                case "wind": view.setText(R.string.wi_strong_wind); break;
                case "fog": view.setText(R.string.wi_fog); break;
                case "cloudy": view.setText(R.string.wi_cloudy); break;
                case "partly-cloudy-day": view.setText(R.string.wi_day_cloudy); break;
                case "partly-cloudy-night": view.setText(R.string.wi_night_alt_cloudy); break;
                case "hail": view.setText(R.string.wi_hail); break;
                case "thunderstorm": view.setText(R.string.wi_thunderstorm); break;
                case "tornado": view.setText(R.string.wi_tornado); break;
                default: view.setText(R.string.wi_wu_unknown); break;
            }
        }
    }
}
