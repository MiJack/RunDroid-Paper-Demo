package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

public class NumericParameters {

    @SerializedName("humidity")
    private double humidity;

    @SerializedName("pressure")
    private double pressure;

    @SerializedName("temp")
    private double temperature;

    @SerializedName("temp_max")
    private double maxTemperature;

    @SerializedName("temp_min")
    private double minTemperature;

    public double getHumidity() {
        com.mijack.Xlog.logMethodEnter("double com.haringeymobile.ukweather.data.objects.NumericParameters.getHumidity()",this);try{com.mijack.Xlog.logMethodExit("double com.haringeymobile.ukweather.data.objects.NumericParameters.getHumidity()",this);return humidity;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.haringeymobile.ukweather.data.objects.NumericParameters.getHumidity()",this,throwable);throw throwable;}
    }

    public double getPressure() {
        com.mijack.Xlog.logMethodEnter("double com.haringeymobile.ukweather.data.objects.NumericParameters.getPressure()",this);try{com.mijack.Xlog.logMethodExit("double com.haringeymobile.ukweather.data.objects.NumericParameters.getPressure()",this);return pressure;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.haringeymobile.ukweather.data.objects.NumericParameters.getPressure()",this,throwable);throw throwable;}
    }

    public double getTemperature(TemperatureScale temperatureScale) {
        com.mijack.Xlog.logMethodEnter("double com.haringeymobile.ukweather.data.objects.NumericParameters.getTemperature(TemperatureScale)",this,temperatureScale);try{com.mijack.Xlog.logMethodExit("double com.haringeymobile.ukweather.data.objects.NumericParameters.getTemperature(TemperatureScale)",this);return temperatureScale.convertTemperature(temperature);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.haringeymobile.ukweather.data.objects.NumericParameters.getTemperature(TemperatureScale)",this,throwable);throw throwable;}
    }
}
