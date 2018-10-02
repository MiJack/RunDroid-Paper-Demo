package com.haringeymobile.ukweather.weather;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast;
import com.haringeymobile.ukweather.data.objects.Temperature;
import com.haringeymobile.ukweather.data.objects.TemperatureScale;
import com.haringeymobile.ukweather.data.objects.WeatherInformation;
import com.haringeymobile.ukweather.utils.MiscMethods;

import java.util.Date;

/**
 * A fragment displaying weather forecast for one day.
 */
public class WeatherDailyWeatherForecastChildFragment extends WeatherInfoFragment {

    private TextView extraTemperaturesTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("android.view.View com.haringeymobile.ukweather.weather.WeatherDailyWeatherForecastChildFragment.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this,inflater,container,savedInstanceState);try{View view = inflater.inflate(R.layout.fragment_daily_weather_forecast, container, false);
        getCommonViews(view);
        TextView nightMorningEveningTitleTextView = (TextView) view.findViewById(
                R.id.night_morning_evening_title);
        nightMorningEveningTitleTextView.setText(getNightMorningEveningTitle());
        extraTemperaturesTextView = (TextView) view.findViewById(
                R.id.night_morning_evening_temperatures_text_view);
        {com.mijack.Xlog.logMethodExit("android.view.View com.haringeymobile.ukweather.weather.WeatherDailyWeatherForecastChildFragment.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this);return view;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.haringeymobile.ukweather.weather.WeatherDailyWeatherForecastChildFragment.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this,throwable);throw throwable;}
    }

    private String getNightMorningEveningTitle() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.weather.WeatherDailyWeatherForecastChildFragment.getNightMorningEveningTitle()",this);try{Resources res = getResources();

        String title = res.getString(R.string.night);
        title += "\n";
        title += res.getString(R.string.morning);
        title += "\n";
        title += res.getString(R.string.evening);

        {com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.weather.WeatherDailyWeatherForecastChildFragment.getNightMorningEveningTitle()",this);return title;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.weather.WeatherDailyWeatherForecastChildFragment.getNightMorningEveningTitle()",this,throwable);throw throwable;}
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherDailyWeatherForecastChildFragment.onActivityCreated(android.os.Bundle)",this,savedInstanceState);try{super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        String jsonString = args.getString(JSON_STRING);
        Gson gson = new Gson();
        CityDailyWeatherForecast cityWeatherForecast = gson.fromJson(jsonString,
                CityDailyWeatherForecast.class);
        displayWeather(cityWeatherForecast);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherDailyWeatherForecastChildFragment.onActivityCreated(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherDailyWeatherForecastChildFragment.onActivityCreated(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    protected void displayExtraInfo(WeatherInformation weatherInformation) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherDailyWeatherForecastChildFragment.displayExtraInfo(com.haringeymobile.ukweather.data.objects.WeatherInformation)",this,weatherInformation);try{CityDailyWeatherForecast cityDailyWeatherForecast =
                (CityDailyWeatherForecast) weatherInformation;

        String extraInfoText = getExtraInfoText(cityDailyWeatherForecast);
        extraInfoTextView.setText(extraInfoText);

        String temperatureInfo = getExtraTemperatureText(cityDailyWeatherForecast);
        extraTemperaturesTextView.setText(temperatureInfo);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherDailyWeatherForecastChildFragment.displayExtraInfo(com.haringeymobile.ukweather.data.objects.WeatherInformation)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherDailyWeatherForecastChildFragment.displayExtraInfo(com.haringeymobile.ukweather.data.objects.WeatherInformation)",this,throwable);throw throwable;}
    }

    /**
     * Obtains a text to be displayed in the extraInfoTextView.
     *
     * @param cityDailyWeatherForecast Java object, corresponding to the Open Weather Map JSON
     *                                 weather forecast data for one day
     * @return a weather forecast date, time, and location
     */
    private String getExtraInfoText(CityDailyWeatherForecast cityDailyWeatherForecast) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.weather.WeatherDailyWeatherForecastChildFragment.getExtraInfoText(com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast)",this,cityDailyWeatherForecast);try{Context context = getActivity();
        Date date = new Date(cityDailyWeatherForecast.getDate() * 1000);

        String weekdayName = MiscMethods.getAbbreviatedWeekdayName(date);
        String dateString = getDateString(context, date);
        String timeString = getTimeString(context, date);

        {com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.weather.WeatherDailyWeatherForecastChildFragment.getExtraInfoText(com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast)",this);return weekdayName + ", " + dateString + "\n" + timeString + "\n" +
                getArguments().getString(CITY_NAME);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.weather.WeatherDailyWeatherForecastChildFragment.getExtraInfoText(com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast)",this,throwable);throw throwable;}
    }

    /**
     * Obtains a text to be displayed in the extraTemperaturesTextView.
     *
     * @param cityDailyWeatherForecast Java object, corresponding to the Open Weather Map JSON
     *                                 weather forecast data for one day
     * @return the night, morning, and evening temperatures
     */
    private String getExtraTemperatureText(CityDailyWeatherForecast cityDailyWeatherForecast) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.weather.WeatherDailyWeatherForecastChildFragment.getExtraTemperatureText(com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast)",this,cityDailyWeatherForecast);try{Temperature temperature = cityDailyWeatherForecast.getTemperature();
        TemperatureScale temperatureScale = weatherInformationDisplayer.getTemperatureScale();
        String temperatureScaleDegree = getResources().getString(
                temperatureScale.getDisplayResourceId());
        String temperatureInfo = MiscMethods.formatDoubleValue(temperature
                .getNightTemperature(temperatureScale), 1) + temperatureScaleDegree;
        temperatureInfo += "\n" + MiscMethods.formatDoubleValue(temperature
                .getMorningTemperature(temperatureScale), 1) + temperatureScaleDegree;
        temperatureInfo += "\n" + MiscMethods.formatDoubleValue(temperature
                .getEveningTemperature(temperatureScale), 1) + temperatureScaleDegree;
        {com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.weather.WeatherDailyWeatherForecastChildFragment.getExtraTemperatureText(com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast)",this);return temperatureInfo;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.weather.WeatherDailyWeatherForecastChildFragment.getExtraTemperatureText(com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast)",this,throwable);throw throwable;}
    }

}