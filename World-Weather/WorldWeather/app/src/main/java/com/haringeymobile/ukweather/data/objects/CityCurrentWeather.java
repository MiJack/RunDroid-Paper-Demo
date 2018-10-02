package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Current weather information.
 */
public class CityCurrentWeather implements WeatherInformation {

    @SerializedName("clouds")
    private Clouds clouds;

    @SerializedName("coord")
    private Coordinates coordinates;

    @SerializedName("dt")
    private long date;

    @SerializedName("id")
    private int cityId;

    @SerializedName("main")
    private NumericParameters numericParameters;

    @SerializedName("name")
    private String cityName;

    @SerializedName("rain")
    private Rain rain;

    @SerializedName("sys")
    private SystemParameters systemParameters;

    @SerializedName("weather")
    private List<Weather> weather;

    @SerializedName("wind")
    private Wind wind;

    @Override
    public int getWeatherConditionsId() {
        com.mijack.Xlog.logMethodEnter("int com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getWeatherConditionsId()",this);try{com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getWeatherConditionsId()",this);return weather.get(0).getId();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getWeatherConditionsId()",this,throwable);throw throwable;}
    }

    @Override
    public String getType() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getType()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getType()",this);return weather.get(0).getType();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getType()",this,throwable);throw throwable;}
    }

    @Override
    public String getIconName() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getIconName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getIconName()",this);return weather.get(0).getIcon();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getIconName()",this,throwable);throw throwable;}
    }

    @Override
    public double getDayTemperature(TemperatureScale temperatureScale) {
        com.mijack.Xlog.logMethodEnter("double com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getDayTemperature(TemperatureScale)",this,temperatureScale);try{com.mijack.Xlog.logMethodExit("double com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getDayTemperature(TemperatureScale)",this);return numericParameters.getTemperature(temperatureScale);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getDayTemperature(TemperatureScale)",this,throwable);throw throwable;}
    }

    @Override
    public double getHumidity() {
        com.mijack.Xlog.logMethodEnter("double com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getHumidity()",this);try{com.mijack.Xlog.logMethodExit("double com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getHumidity()",this);return numericParameters.getHumidity();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getHumidity()",this,throwable);throw throwable;}
    }

    @Override
    public double getPressure() {
        com.mijack.Xlog.logMethodEnter("double com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getPressure()",this);try{com.mijack.Xlog.logMethodExit("double com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getPressure()",this);return numericParameters.getPressure();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getPressure()",this,throwable);throw throwable;}
    }

    @Override
    public Wind getWind() {
        com.mijack.Xlog.logMethodEnter("com.haringeymobile.ukweather.data.objects.Wind com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getWind()",this);try{com.mijack.Xlog.logMethodExit("com.haringeymobile.ukweather.data.objects.Wind com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getWind()",this);return wind;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.haringeymobile.ukweather.data.objects.Wind com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getWind()",this,throwable);throw throwable;}
    }

    @Override
    public boolean isDayTemperatureProvided() {
        com.mijack.Xlog.logMethodEnter("boolean com.haringeymobile.ukweather.data.objects.CityCurrentWeather.isDayTemperatureProvided()",this);try{com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.data.objects.CityCurrentWeather.isDayTemperatureProvided()",this);return numericParameters != null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.haringeymobile.ukweather.data.objects.CityCurrentWeather.isDayTemperatureProvided()",this,throwable);throw throwable;}
    }

    @Override
    public boolean isPressureProvided() {
        com.mijack.Xlog.logMethodEnter("boolean com.haringeymobile.ukweather.data.objects.CityCurrentWeather.isPressureProvided()",this);try{com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.data.objects.CityCurrentWeather.isPressureProvided()",this);return numericParameters != null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.haringeymobile.ukweather.data.objects.CityCurrentWeather.isPressureProvided()",this,throwable);throw throwable;}
    }

    @Override
    public boolean isHumidityProvided() {
        com.mijack.Xlog.logMethodEnter("boolean com.haringeymobile.ukweather.data.objects.CityCurrentWeather.isHumidityProvided()",this);try{com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.data.objects.CityCurrentWeather.isHumidityProvided()",this);return numericParameters != null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.haringeymobile.ukweather.data.objects.CityCurrentWeather.isHumidityProvided()",this,throwable);throw throwable;}
    }

    public int getCityId() {
        com.mijack.Xlog.logMethodEnter("int com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getCityId()",this);try{com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getCityId()",this);return cityId;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getCityId()",this,throwable);throw throwable;}
    }

    public String getCityName() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getCityName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getCityName()",this);return cityName;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getCityName()",this,throwable);throw throwable;}
    }

    public SystemParameters getSystemParameters() {
        com.mijack.Xlog.logMethodEnter("com.haringeymobile.ukweather.data.objects.SystemParameters com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getSystemParameters()",this);try{com.mijack.Xlog.logMethodExit("com.haringeymobile.ukweather.data.objects.SystemParameters com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getSystemParameters()",this);return systemParameters;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.haringeymobile.ukweather.data.objects.SystemParameters com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getSystemParameters()",this,throwable);throw throwable;}
    }

    public Coordinates getCoordinates() {
        com.mijack.Xlog.logMethodEnter("com.haringeymobile.ukweather.data.objects.Coordinates com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getCoordinates()",this);try{com.mijack.Xlog.logMethodExit("com.haringeymobile.ukweather.data.objects.Coordinates com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getCoordinates()",this);return coordinates;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.haringeymobile.ukweather.data.objects.Coordinates com.haringeymobile.ukweather.data.objects.CityCurrentWeather.getCoordinates()",this,throwable);throw throwable;}
    }

}
