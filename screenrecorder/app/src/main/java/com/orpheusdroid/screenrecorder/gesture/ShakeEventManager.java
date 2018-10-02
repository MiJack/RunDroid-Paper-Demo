/*
 * Copyright (c) 2016-2017. Vijai Chandra Prasad R.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses
 */

package com.orpheusdroid.screenrecorder.gesture;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.orpheusdroid.screenrecorder.Const;

/**
 * Created by vijai on 08-10-2017.
 */

public class ShakeEventManager implements SensorEventListener {

    private static final int MOV_COUNTS = 5;
    private static final int MOV_THRESHOLD = 4;
    private static final float ALPHA = 0.8F;
    private static final int SHAKE_WINDOW_TIME_INTERVAL = 1000; /*// milliseconds*/
    private SensorManager sManager;
    private Sensor s;
    /*// Gravity force on x,y,z axis*/
    private float gravity[] = new float[3];

    private int counter;
    private long firstMovTime;
    private long lastMoveTime = 0;
    private ShakeListener listener;

    public ShakeEventManager( ShakeListener listener) {
        this.listener = listener;
    }

    public void init(Context ctx) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.init(android.content.Context)",this,ctx);try{sManager = (SensorManager)  ctx.getSystemService(Context.SENSOR_SERVICE);
        s = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        register();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.init(android.content.Context)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.init(android.content.Context)",this,throwable);throw throwable;}
    }

    public void register() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.register()",this);try{sManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.register()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.register()",this,throwable);throw throwable;}
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.onSensorChanged(android.hardware.SensorEvent)",this,sensorEvent);try{float maxAcc = calcMaxAcceleration(sensorEvent);
        Log.d("SwA", "Max Acc ["+maxAcc+"]");
        if (maxAcc >= MOV_THRESHOLD) {
            if (counter == 0) {
                counter++;
                firstMovTime = System.currentTimeMillis();
                Log.d("SwA", "First mov..");
            } else {
                long now = System.currentTimeMillis();
                if ((now - firstMovTime) < SHAKE_WINDOW_TIME_INTERVAL)
                    {counter++;}
                else {
                    resetAllData();
                    /*//counter++;*/
                    {com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.onSensorChanged(android.hardware.SensorEvent)",this);return;}
                }
                Log.d(Const.TAG, "Mov counter ["+counter+"]");

                if (counter == MOV_COUNTS && (System.currentTimeMillis() - lastMoveTime) > 5000 )
                    {if (listener != null) {
                        resetAllData();
                        Log.d(Const.TAG, "Shaked. count: " + counter);
                        listener.onShake();
                        lastMoveTime = System.currentTimeMillis();
                    }}
            }
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.onSensorChanged(android.hardware.SensorEvent)",this,throwable);throw throwable;}

    }

    {com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.onAccuracyChanged(android.hardware.Sensor,int)",this,sensor,i);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.onAccuracyChanged(android.hardware.Sensor,int)",this);}

    public void stop()  {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.stop()",this);try{sManager.unregisterListener(this);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.stop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.stop()",this,throwable);throw throwable;}
    }


    private float calcMaxAcceleration(SensorEvent event) {
        com.mijack.Xlog.logMethodEnter("float com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.calcMaxAcceleration(android.hardware.SensorEvent)",this,event);try{gravity[0] = calcGravityForce(event.values[0], 0);
        gravity[1] = calcGravityForce(event.values[1], 1);
        gravity[2] = calcGravityForce(event.values[2], 2);

        float accX = event.values[0] - gravity[0];
        float accY = event.values[1] - gravity[1];
        float accZ = event.values[2] - gravity[2];

        float max1 = Math.max(accX, accY);
        {com.mijack.Xlog.logMethodExit("float com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.calcMaxAcceleration(android.hardware.SensorEvent)",this);return Math.max(max1, accZ);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("float com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.calcMaxAcceleration(android.hardware.SensorEvent)",this,throwable);throw throwable;}
    }

    /*// Low pass filter*/
    private float calcGravityForce(float currentVal, int index) {
        com.mijack.Xlog.logMethodEnter("float com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.calcGravityForce(float,int)",this,currentVal,index);try{com.mijack.Xlog.logMethodExit("float com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.calcGravityForce(float,int)",this);return  ALPHA * gravity[index] + (1 - ALPHA) * currentVal;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("float com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.calcGravityForce(float,int)",this,throwable);throw throwable;}
    }


    private void resetAllData() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.resetAllData()",this);try{Log.d("SwA", "Reset all data");
        counter = 0;
        firstMovTime = System.currentTimeMillis();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.resetAllData()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.gesture.ShakeEventManager.resetAllData()",this,throwable);throw throwable;}
    }


    public interface ShakeListener {
        void onShake();
    }

}
