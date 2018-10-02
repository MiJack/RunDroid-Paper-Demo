package com.haringeymobile.ukweather.weather;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import com.google.gson.Gson;
import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.data.objects.CityCurrentWeather;
import com.haringeymobile.ukweather.data.objects.SystemParameters;
import com.haringeymobile.ukweather.data.objects.WeatherInformation;

import java.util.Date;

/**
 * A fragment displaying current weather information.
 */
public class WeatherCurrentInfoFragment extends WeatherInfoFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherCurrentInfoFragment.onActivityCreated(android.os.Bundle)",this,savedInstanceState);try{super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        String jsonString = args.getString(JSON_STRING);
        Gson gson = new Gson();
        CityCurrentWeather cityCurrentWeather = gson.fromJson(jsonString, CityCurrentWeather.class);
        displayWeather(cityCurrentWeather);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherCurrentInfoFragment.onActivityCreated(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherCurrentInfoFragment.onActivityCreated(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    protected void displayExtraInfo(WeatherInformation weatherInformation) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherCurrentInfoFragment.displayExtraInfo(com.haringeymobile.ukweather.data.objects.WeatherInformation)",this,weatherInformation);try{CityCurrentWeather cityCurrentWeather = (CityCurrentWeather) weatherInformation;
        String extraInfo = cityCurrentWeather.getCityName();

        Context context = getContext();
        Resources res = getResources();
        SystemParameters systemParameters = cityCurrentWeather.getSystemParameters();
        long sunriseTime = systemParameters.getSunriseTime() * 1000;
        if (sunriseTime != 0) {
            extraInfo += "\n" + res.getString(R.string.sunrise_time) +
                    getTimeString(context, new Date(sunriseTime));
        }

        long sunsetTime = systemParameters.getSunsetTime() * 1000;
        if (sunsetTime != 0) {
            extraInfo += "\n" + res.getString(R.string.sunset_time) +
                    getTimeString(context, new Date(sunsetTime));
        }

        extraInfoTextView.setText(extraInfo);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherCurrentInfoFragment.displayExtraInfo(com.haringeymobile.ukweather.data.objects.WeatherInformation)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherCurrentInfoFragment.displayExtraInfo(com.haringeymobile.ukweather.data.objects.WeatherInformation)",this,throwable);throw throwable;}
    }

}