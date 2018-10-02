package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Weather forecast for one three hour period.
 */
public class CityThreeHourlyWeatherForecast implements WeatherInformation {

    @SerializedName("dt")
    private long date;

    @SerializedName("main")
    private NumericParameters numericParameters;

    @SerializedName("weather")
    private List<Weather> weather;

    @SerializedName("clouds")
    private Clouds clouds;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("dt_txt")
    private String dateText;

    @Override
    public int getWeatherConditionsId() {
        com.mijack.Xlog.logMethodEnter("int com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getWeatherConditionsId()",this);try{com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getWeatherConditionsId()",this);return weather.get(0).getId();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getWeatherConditionsId()",this,throwable);throw throwable;}
    }

    @Override
    public String getType() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getType()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getType()",this);return weather.get(0).getType();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getType()",this,throwable);throw throwable;}
    }

    @Override
    public String getIconName() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getIconName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getIconName()",this);return weather.get(0).getIcon();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getIconName()",this,throwable);throw throwable;}
    }

    @Override
    public double getDayTemperature(TemperatureScale temperatureScale) {
        com.mijack.Xlog.logMethodEnter("double com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getDayTemperature(TemperatureScale)",this,temperatureScale);try{com.mijack.Xlog.logMethodExit("double com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getDayTemperature(TemperatureScale)",this);return numericParameters.getTemperature(temperatureScale);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getDayTemperature(TemperatureScale)",this,throwable);throw throwable;}
    }

    @Override
    public double getHumidity() {
        com.mijack.Xlog.logMethodEnter("double com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getHumidity()",this);try{com.mijack.Xlog.logMethodExit("double com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getHumidity()",this);return numericParameters.getHumidity();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getHumidity()",this,throwable);throw throwable;}
    }

    @Override
    public double getPressure() {
        com.mijack.Xlog.logMethodEnter("double com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getPressure()",this);try{com.mijack.Xlog.logMethodExit("double com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getPressure()",this);return numericParameters.getPressure();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getPressure()",this,throwable);throw throwable;}
    }

    @Override
    public Wind getWind() {
        com.mijack.Xlog.logMethodEnter("com.haringeymobile.ukweather.data.objects.Wind com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getWind()",this);try{com.mijack.Xlog.logMethodExit("com.haringeymobile.ukweather.data.objects.Wind com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getWind()",this);return wind;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.haringeymobile.ukweather.data.objects.Wind com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getWind()",this,throwable);throw throwable;}
    }

    @Override
    public boolean isDayTemperatureProvided() {
        com.mijack.Xlog.logMethodEnter("boolean com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.isDayTemperatureProvided()",this);try{com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.isDayTemperatureProvided()",this);return numericParameters != null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.isDayTemperatureProvided()",this,throwable);throw throwable;}
    }

    @Override
    public boolean isPressureProvided() {
        com.mijack.Xlog.logMethodEnter("boolean com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.isPressureProvided()",this);try{com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.isPressureProvided()",this);return numericParameters != null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.isPressureProvided()",this,throwable);throw throwable;}
    }

    @Override
    public boolean isHumidityProvided() {
        com.mijack.Xlog.logMethodEnter("boolean com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.isHumidityProvided()",this);try{com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.isHumidityProvided()",this);return numericParameters != null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.isHumidityProvided()",this,throwable);throw throwable;}
    }

    public long getDate() {
        com.mijack.Xlog.logMethodEnter("long com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getDate()",this);try{com.mijack.Xlog.logMethodExit("long com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getDate()",this);return date;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast.getDate()",this,throwable);throw throwable;}
    }

}