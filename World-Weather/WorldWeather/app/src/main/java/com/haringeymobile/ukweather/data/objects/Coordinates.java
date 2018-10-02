package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

public class Coordinates {

    @SerializedName("lat")
    private double latitude;

    @SerializedName("lon")
    private double longitude;

    public double getLatitude() {
        com.mijack.Xlog.logMethodEnter("double com.haringeymobile.ukweather.data.objects.Coordinates.getLatitude()",this);try{com.mijack.Xlog.logMethodExit("double com.haringeymobile.ukweather.data.objects.Coordinates.getLatitude()",this);return latitude;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.haringeymobile.ukweather.data.objects.Coordinates.getLatitude()",this,throwable);throw throwable;}
    }

    public double getLongitude() {
        com.mijack.Xlog.logMethodEnter("double com.haringeymobile.ukweather.data.objects.Coordinates.getLongitude()",this);try{com.mijack.Xlog.logMethodExit("double com.haringeymobile.ukweather.data.objects.Coordinates.getLongitude()",this);return longitude;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.haringeymobile.ukweather.data.objects.Coordinates.getLongitude()",this,throwable);throw throwable;}
    }
}
