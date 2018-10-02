package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * An object corresponding to the JSON data for the Open Weather Map daily
 * weather forecast query.
 */
public class SearchResponseForDailyForecastQuery {

    @SerializedName("city")
    private CityInfo cityInfo;

    @SerializedName("cnt")
    private int dayCount;

    @SerializedName("cod")
    private int code;

    @SerializedName("list")
    private List<CityDailyWeatherForecast> dailyWeatherForecasts;

    @SerializedName("message")
    private String message;

    public CityInfo getCityInfo() {
        com.mijack.Xlog.logMethodEnter("com.haringeymobile.ukweather.data.objects.CityInfo com.haringeymobile.ukweather.data.objects.SearchResponseForDailyForecastQuery.getCityInfo()",this);try{com.mijack.Xlog.logMethodExit("com.haringeymobile.ukweather.data.objects.CityInfo com.haringeymobile.ukweather.data.objects.SearchResponseForDailyForecastQuery.getCityInfo()",this);return cityInfo;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.haringeymobile.ukweather.data.objects.CityInfo com.haringeymobile.ukweather.data.objects.SearchResponseForDailyForecastQuery.getCityInfo()",this,throwable);throw throwable;}
    }

    public List<CityDailyWeatherForecast> getDailyWeatherForecasts() {
        com.mijack.Xlog.logMethodEnter("java.util.List com.haringeymobile.ukweather.data.objects.SearchResponseForDailyForecastQuery.getDailyWeatherForecasts()",this);try{com.mijack.Xlog.logMethodExit("java.util.List com.haringeymobile.ukweather.data.objects.SearchResponseForDailyForecastQuery.getDailyWeatherForecasts()",this);return dailyWeatherForecasts;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.List com.haringeymobile.ukweather.data.objects.SearchResponseForDailyForecastQuery.getDailyWeatherForecasts()",this,throwable);throw throwable;}
    }
}
