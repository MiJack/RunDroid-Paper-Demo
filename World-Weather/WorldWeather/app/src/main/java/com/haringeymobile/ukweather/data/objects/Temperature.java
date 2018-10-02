package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

public class Temperature {

    public static final double DIFFERENCE_BETWEEN_KELVIN_AND_CELCIUS = 273.15;

    @SerializedName("day")
    private double dayTemperature;

    @SerializedName("eve")
    private double eveningTemperature;

    @SerializedName("morn")
    private double morningTemperature;

    @SerializedName("night")
    private double nightTemperature;

    @SerializedName("max")
    private double maxTemperature;

    @SerializedName("min")
    private double minTemperature;

    public double getDayTemperature(TemperatureScale temperatureScale) {
        com.mijack.Xlog.logMethodEnter("double com.haringeymobile.ukweather.data.objects.Temperature.getDayTemperature(TemperatureScale)",this,temperatureScale);try{com.mijack.Xlog.logMethodExit("double com.haringeymobile.ukweather.data.objects.Temperature.getDayTemperature(TemperatureScale)",this);return temperatureScale.convertTemperature(dayTemperature);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.haringeymobile.ukweather.data.objects.Temperature.getDayTemperature(TemperatureScale)",this,throwable);throw throwable;}
    }

    public double getEveningTemperature(TemperatureScale temperatureScale) {
        com.mijack.Xlog.logMethodEnter("double com.haringeymobile.ukweather.data.objects.Temperature.getEveningTemperature(TemperatureScale)",this,temperatureScale);try{com.mijack.Xlog.logMethodExit("double com.haringeymobile.ukweather.data.objects.Temperature.getEveningTemperature(TemperatureScale)",this);return temperatureScale.convertTemperature(eveningTemperature);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.haringeymobile.ukweather.data.objects.Temperature.getEveningTemperature(TemperatureScale)",this,throwable);throw throwable;}
    }

    public double getMorningTemperature(TemperatureScale temperatureScale) {
        com.mijack.Xlog.logMethodEnter("double com.haringeymobile.ukweather.data.objects.Temperature.getMorningTemperature(TemperatureScale)",this,temperatureScale);try{com.mijack.Xlog.logMethodExit("double com.haringeymobile.ukweather.data.objects.Temperature.getMorningTemperature(TemperatureScale)",this);return temperatureScale.convertTemperature(morningTemperature);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.haringeymobile.ukweather.data.objects.Temperature.getMorningTemperature(TemperatureScale)",this,throwable);throw throwable;}
    }

    public double getNightTemperature(TemperatureScale temperatureScale) {
        com.mijack.Xlog.logMethodEnter("double com.haringeymobile.ukweather.data.objects.Temperature.getNightTemperature(TemperatureScale)",this,temperatureScale);try{com.mijack.Xlog.logMethodExit("double com.haringeymobile.ukweather.data.objects.Temperature.getNightTemperature(TemperatureScale)",this);return temperatureScale.convertTemperature(nightTemperature);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.haringeymobile.ukweather.data.objects.Temperature.getNightTemperature(TemperatureScale)",this,throwable);throw throwable;}
    }

}
