package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

public class Weather {

    public static final String ICON_URL_PREFIX = "https://openweathermap.org/img/w/";
    public static final String ICON_URL_SUFFIX = ".png";

    @SerializedName("description")
    private String description;

    @SerializedName("icon")
    private String icon;

    @SerializedName("id")
    private int id;

    @SerializedName("main")
    private String type;

    public String getDescription() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.data.objects.Weather.getDescription()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.data.objects.Weather.getDescription()",this);return description;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.data.objects.Weather.getDescription()",this,throwable);throw throwable;}
    }

    public String getIcon() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.data.objects.Weather.getIcon()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.data.objects.Weather.getIcon()",this);return icon;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.data.objects.Weather.getIcon()",this,throwable);throw throwable;}
    }

    public String getType() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.data.objects.Weather.getType()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.data.objects.Weather.getType()",this);return type;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.data.objects.Weather.getType()",this,throwable);throw throwable;}
    }

    public int getId() {
        com.mijack.Xlog.logMethodEnter("int com.haringeymobile.ukweather.data.objects.Weather.getId()",this);try{com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.Weather.getId()",this);return id;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.haringeymobile.ukweather.data.objects.Weather.getId()",this,throwable);throw throwable;}
    }

}
