package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * An object corresponding to the JSON data for the Open Weather Map 'find cities' query.
 */
public class SearchResponseForFindQuery {

    @SerializedName("cod")
    private int code;

    @SerializedName("count")
    private int count;

    @SerializedName("list")
    private List<CityCurrentWeather> cities;

    @SerializedName("message")
    private String message;

    public List<CityCurrentWeather> getCities() {
        com.mijack.Xlog.logMethodEnter("java.util.List com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery.getCities()",this);try{com.mijack.Xlog.logMethodExit("java.util.List com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery.getCities()",this);return cities;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.List com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery.getCities()",this,throwable);throw throwable;}
    }

    public int getCode() {
        com.mijack.Xlog.logMethodEnter("int com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery.getCode()",this);try{com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery.getCode()",this);return code;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery.getCode()",this,throwable);throw throwable;}
    }

    public int getCount() {
        com.mijack.Xlog.logMethodEnter("int com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery.getCount()",this);try{com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery.getCount()",this);return count;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery.getCount()",this,throwable);throw throwable;}
    }

}