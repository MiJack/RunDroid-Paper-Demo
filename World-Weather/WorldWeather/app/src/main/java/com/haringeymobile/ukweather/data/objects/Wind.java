package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;
import com.haringeymobile.ukweather.R;

public class Wind {

    @SerializedName("deg")
    private int directionInDegrees;

    @SerializedName("speed")
    private double speed;

    public int getDirectionInDegrees() {
        com.mijack.Xlog.logMethodEnter("int com.haringeymobile.ukweather.data.objects.Wind.getDirectionInDegrees()",this);try{com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.Wind.getDirectionInDegrees()",this);return directionInDegrees;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.haringeymobile.ukweather.data.objects.Wind.getDirectionInDegrees()",this,throwable);throw throwable;}
    }

    public double getSpeed(WindSpeedMeasurementUnit windSpeedMeasurementUnit) {
        com.mijack.Xlog.logMethodEnter("double com.haringeymobile.ukweather.data.objects.Wind.getSpeed(WindSpeedMeasurementUnit)",this,windSpeedMeasurementUnit);try{com.mijack.Xlog.logMethodExit("double com.haringeymobile.ukweather.data.objects.Wind.getSpeed(WindSpeedMeasurementUnit)",this);return windSpeedMeasurementUnit.convertSpeed(speed);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.haringeymobile.ukweather.data.objects.Wind.getSpeed(WindSpeedMeasurementUnit)",this,throwable);throw throwable;}
    }

    public void setSpeed(double speed) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.data.objects.Wind.setSpeed(double)",this,speed);try{this.speed = speed;com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.data.objects.Wind.setSpeed(double)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.data.objects.Wind.setSpeed(double)",this,throwable);throw throwable;}
    }

    public void setDirectionInDegrees(int directionInDegrees) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.data.objects.Wind.setDirectionInDegrees(int)",this,directionInDegrees);try{this.directionInDegrees = directionInDegrees;com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.data.objects.Wind.setDirectionInDegrees(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.data.objects.Wind.setDirectionInDegrees(int)",this,throwable);throw throwable;}
    }

    public int getCardinalDirectionStringResource() {
        com.mijack.Xlog.logMethodEnter("int com.haringeymobile.ukweather.data.objects.Wind.getCardinalDirectionStringResource()",this);try{if (directionInDegrees <= 11 || directionInDegrees >= 349) {
            {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.Wind.getCardinalDirectionStringResource()",this);return R.string.direction_n;}
        } else if (directionInDegrees <= 33) {
            {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.Wind.getCardinalDirectionStringResource()",this);return R.string.direction_nne;}
        } else if (directionInDegrees <= 56) {
            {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.Wind.getCardinalDirectionStringResource()",this);return R.string.direction_ne;}
        } else if (directionInDegrees <= 78) {
            {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.Wind.getCardinalDirectionStringResource()",this);return R.string.direction_ene;}
        } else if (directionInDegrees <= 101) {
            {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.Wind.getCardinalDirectionStringResource()",this);return R.string.direction_e;}
        } else if (directionInDegrees <= 123) {
            {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.Wind.getCardinalDirectionStringResource()",this);return R.string.direction_ese;}
        } else if (directionInDegrees <= 146) {
            {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.Wind.getCardinalDirectionStringResource()",this);return R.string.direction_se;}
        } else if (directionInDegrees <= 168) {
            {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.Wind.getCardinalDirectionStringResource()",this);return R.string.direction_sse;}
        } else if (directionInDegrees <= 191) {
            {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.Wind.getCardinalDirectionStringResource()",this);return R.string.direction_s;}
        } else if (directionInDegrees <= 213) {
            {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.Wind.getCardinalDirectionStringResource()",this);return R.string.direction_ssw;}
        } else if (directionInDegrees <= 236) {
            {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.Wind.getCardinalDirectionStringResource()",this);return R.string.direction_sw;}
        } else if (directionInDegrees <= 258) {
            {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.Wind.getCardinalDirectionStringResource()",this);return R.string.direction_wsw;}
        } else if (directionInDegrees <= 281) {
            {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.Wind.getCardinalDirectionStringResource()",this);return R.string.direction_w;}
        } else if (directionInDegrees <= 303) {
            {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.Wind.getCardinalDirectionStringResource()",this);return R.string.direction_wnw;}
        } else if (directionInDegrees <= 326) {
            {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.Wind.getCardinalDirectionStringResource()",this);return R.string.direction_nw;}
        } else {
            {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.data.objects.Wind.getCardinalDirectionStringResource()",this);return R.string.direction_nnw;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.haringeymobile.ukweather.data.objects.Wind.getCardinalDirectionStringResource()",this,throwable);throw throwable;}
    }
}
