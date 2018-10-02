package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Weather forecast for one day.
 */
public class CityDailyWeatherForecast implements WeatherInformation {

    @SerializedName("dt")
    private long date;

    @SerializedName("clouds")
    private int cloudinessPercentage;

    @SerializedName("deg")
    private int windDirectionInDegrees;

    @SerializedName("speed")
    private double windSpeed;

    @SerializedName("humidity")
    private double humidity;

    @SerializedName("pressure")
    private double pressure;

    @SerializedName("temp")
    private Temperature temperature;

    @SerializedName("weather")
    private List<Weather> weather;

    @Override
    public int getWeatherConditionsId() {
        com.mijack.Xlog.logMethodEnter("int com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getWeatherConditionsId()",this);try{com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getWeatherConditionsId()",this);return weather.get(0).getId();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getWeatherConditionsId()",this,throwable);throw throwable;}
    }

    @Override
    public String getType() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getType()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getType()",this);return weather.get(0).getType();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getType()",this,throwable);throw throwable;}
    }

    @Override
    public String getIconName() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getIconName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getIconName()",this);return weather.get(0).getIcon();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getIconName()",this,throwable);throw throwable;}
    }

    @Override
    public double getDayTemperature(TemperatureScale temperatureScale) {
        com.mijack.Xlog.logMethodEnter("double com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getDayTemperature(TemperatureScale)",this,temperatureScale);try{com.mijack.Xlog.logMethodExit("double com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getDayTemperature(TemperatureScale)",this);return temperature.getDayTemperature(temperatureScale);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getDayTemperature(TemperatureScale)",this,throwable);throw throwable;}
    }

    @Override
    public double getHumidity() {
        com.mijack.Xlog.logMethodEnter("double com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getHumidity()",this);try{com.mijack.Xlog.logMethodExit("double com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getHumidity()",this);return humidity;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getHumidity()",this,throwable);throw throwable;}
    }

    @Override
    public double getPressure() {
        com.mijack.Xlog.logMethodEnter("double com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getPressure()",this);try{com.mijack.Xlog.logMethodExit("double com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getPressure()",this);return pressure;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getPressure()",this,throwable);throw throwable;}
    }

    @Override
    public Wind getWind() {
        com.mijack.Xlog.logMethodEnter("com.haringeymobile.ukweather.data.objects.Wind com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getWind()",this);try{Wind wind = new Wind();
        wind.setDirectionInDegrees(windDirectionInDegrees);
        wind.setSpeed(windSpeed);
        {com.mijack.Xlog.logMethodExit("com.haringeymobile.ukweather.data.objects.Wind com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getWind()",this);return wind;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.haringeymobile.ukweather.data.objects.Wind com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getWind()",this,throwable);throw throwable;}
    }

    @Override
    public boolean isDayTemperatureProvided() {
        com.mijack.Xlog.logMethodEnter("boolean com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.isDayTemperatureProvided()",this);try{com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.isDayTemperatureProvided()",this);return temperature != null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.isDayTemperatureProvided()",this,throwable);throw throwable;}
    }

    @Override
    public boolean isPressureProvided() {
        com.mijack.Xlog.logMethodEnter("boolean com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.isPressureProvided()",this);try{com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.isPressureProvided()",this);return true;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.isPressureProvided()",this,throwable);throw throwable;}
    }

    @Override
    public boolean isHumidityProvided() {
        com.mijack.Xlog.logMethodEnter("boolean com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.isHumidityProvided()",this);try{com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.isHumidityProvided()",this);return true;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.isHumidityProvided()",this,throwable);throw throwable;}
    }

    public long getDate() {
        com.mijack.Xlog.logMethodEnter("long com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getDate()",this);try{com.mijack.Xlog.logMethodExit("long com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getDate()",this);return date;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getDate()",this,throwable);throw throwable;}
    }

    public Temperature getTemperature() {
        com.mijack.Xlog.logMethodEnter("com.haringeymobile.ukweather.data.objects.Temperature com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getTemperature()",this);try{com.mijack.Xlog.logMethodExit("com.haringeymobile.ukweather.data.objects.Temperature com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getTemperature()",this);return temperature;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.haringeymobile.ukweather.data.objects.Temperature com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast.getTemperature()",this,throwable);throw throwable;}
    }

}
