package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

public class SystemParameters {

    @SerializedName("country")
    private String country;

    @SerializedName("sunrise")
    private long sunriseTime;

    @SerializedName("sunset")
    private long sunsetTime;

    public String getCountry() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.data.objects.SystemParameters.getCountry()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.data.objects.SystemParameters.getCountry()",this);return country;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.data.objects.SystemParameters.getCountry()",this,throwable);throw throwable;}
    }

    public long getSunriseTime() {
        com.mijack.Xlog.logMethodEnter("long com.haringeymobile.ukweather.data.objects.SystemParameters.getSunriseTime()",this);try{com.mijack.Xlog.logMethodExit("long com.haringeymobile.ukweather.data.objects.SystemParameters.getSunriseTime()",this);return sunriseTime;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.haringeymobile.ukweather.data.objects.SystemParameters.getSunriseTime()",this,throwable);throw throwable;}
    }

    public long getSunsetTime() {
        com.mijack.Xlog.logMethodEnter("long com.haringeymobile.ukweather.data.objects.SystemParameters.getSunsetTime()",this);try{com.mijack.Xlog.logMethodExit("long com.haringeymobile.ukweather.data.objects.SystemParameters.getSunsetTime()",this);return sunsetTime;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.haringeymobile.ukweather.data.objects.SystemParameters.getSunsetTime()",this,throwable);throw throwable;}
    }

}