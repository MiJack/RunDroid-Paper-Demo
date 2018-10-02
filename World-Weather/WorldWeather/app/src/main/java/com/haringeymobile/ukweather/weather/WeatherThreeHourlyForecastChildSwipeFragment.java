package com.haringeymobile.ukweather.weather;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.google.gson.Gson;
import com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast;
import com.haringeymobile.ukweather.data.objects.WeatherInformation;
import com.haringeymobile.ukweather.utils.MiscMethods;

import java.util.Date;

/**
 * A fragment displaying weather forecast for a three hour period.
 */
public class WeatherThreeHourlyForecastChildSwipeFragment extends WeatherInfoFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildSwipeFragment.onActivityCreated(android.os.Bundle)",this,savedInstanceState);try{super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        String jsonString = args.getString(WeatherInfoFragment.JSON_STRING);
        Gson gson = new Gson();
        CityThreeHourlyWeatherForecast threeHourlyWeatherForecast = gson.fromJson(jsonString,
                CityThreeHourlyWeatherForecast.class);
        displayWeather(threeHourlyWeatherForecast);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildSwipeFragment.onActivityCreated(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildSwipeFragment.onActivityCreated(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void displayExtraInfo(WeatherInformation weatherInformation) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildSwipeFragment.displayExtraInfo(com.haringeymobile.ukweather.data.objects.WeatherInformation)",this,weatherInformation);try{CityThreeHourlyWeatherForecast threeHourlyWeatherForecast =
                (CityThreeHourlyWeatherForecast) weatherInformation;
        Context context = getActivity();
        Date date = new Date(threeHourlyWeatherForecast.getDate() * 1000);

        String weekdayName = MiscMethods.getAbbreviatedWeekdayName(date);
        String dateString = getDateString(context, date);
        String timeString = getTimeString(context, date);

        extraInfoTextView.setText(weekdayName + ", " + dateString + "\n" + timeString + "\n"
                + getArguments().getString(CITY_NAME));com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildSwipeFragment.displayExtraInfo(com.haringeymobile.ukweather.data.objects.WeatherInformation)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildSwipeFragment.displayExtraInfo(com.haringeymobile.ukweather.data.objects.WeatherInformation)",this,throwable);throw throwable;}
    }

}