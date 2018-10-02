package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * An object corresponding to the JSON data for the Open Weather Map three
 * hourly weather forecast query.
 */
public class SearchResponseForThreeHourlyForecastQuery {

    @SerializedName("cod")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("city")
    private CityInfo cityInfo;

    @SerializedName("cnt")
    private int forecastCount;

    @SerializedName("list")
    private List<CityThreeHourlyWeatherForecast> threeHourlyWeatherForecasts;

    public CityInfo getCityInfo() {
        com.mijack.Xlog.logMethodEnter("com.haringeymobile.ukweather.data.objects.CityInfo com.haringeymobile.ukweather.data.objects.SearchResponseForThreeHourlyForecastQuery.getCityInfo()",this);try{com.mijack.Xlog.logMethodExit("com.haringeymobile.ukweather.data.objects.CityInfo com.haringeymobile.ukweather.data.objects.SearchResponseForThreeHourlyForecastQuery.getCityInfo()",this);return cityInfo;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.haringeymobile.ukweather.data.objects.CityInfo com.haringeymobile.ukweather.data.objects.SearchResponseForThreeHourlyForecastQuery.getCityInfo()",this,throwable);throw throwable;}
    }

    public List<CityThreeHourlyWeatherForecast> getThreeHourlyWeatherForecasts() {
        com.mijack.Xlog.logMethodEnter("java.util.List com.haringeymobile.ukweather.data.objects.SearchResponseForThreeHourlyForecastQuery.getThreeHourlyWeatherForecasts()",this);try{com.mijack.Xlog.logMethodExit("java.util.List com.haringeymobile.ukweather.data.objects.SearchResponseForThreeHourlyForecastQuery.getThreeHourlyWeatherForecasts()",this);return threeHourlyWeatherForecasts;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.List com.haringeymobile.ukweather.data.objects.SearchResponseForThreeHourlyForecastQuery.getThreeHourlyWeatherForecasts()",this,throwable);throw throwable;}
    }
}
