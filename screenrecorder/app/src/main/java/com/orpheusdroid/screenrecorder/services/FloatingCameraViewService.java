/*
 * Copyright (c) 2016-2018. Vijai Chandra Prasad R.
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

package com.orpheusdroid.screenrecorder.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.cameraview.CameraView;
import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.R;

/**
 * Todo: Add class description here
 *
 * @author Vijai Chandra Prasad .R
 */
public class FloatingCameraViewService extends Service implements View.OnClickListener {
    private static FloatingCameraViewService context;
    private WindowManager mWindowManager;
    private LinearLayout mFloatingView;
    private View mCurrentView;
    private LinearLayout hidenCameraView;
    private ImageButton resizeOverlay;
    private CameraView cameraView;
    private boolean isCameraViewHidden;
    private Values values;
    private WindowManager.LayoutParams params;
    private SharedPreferences prefs;
    private OverlayResize overlayResize = OverlayResize.MINWINDOW;
    private IBinder binder = new ServiceBinder();

    public FloatingCameraViewService() {
        context = this;
    }

    @Override
    public IBinder onBind(Intent intent) {
        com.mijack.Xlog.logMethodEnter("android.os.IBinder com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.onBind(android.content.Intent)",this,intent);try{Log.d(Const.TAG, "Binding successful!");
        {com.mijack.Xlog.logMethodExit("android.os.IBinder com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.onBind(android.content.Intent)",this);return binder;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.os.IBinder com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.onBind(android.content.Intent)",this,throwable);throw throwable;}
    }

    @Override
    public boolean onUnbind(Intent intent) {
        com.mijack.Xlog.logMethodEnter("boolean com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.onUnbind(android.content.Intent)",this,intent);try{Log.d(Const.TAG, "Unbinding and stopping service");
        stopSelf();
        {com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.onUnbind(android.content.Intent)",this);return super.onUnbind(intent);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.onUnbind(android.content.Intent)",this,throwable);throw throwable;}
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        com.mijack.Xlog.logMethodEnter("int com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.onStartCommand(android.content.Intent,int,int)",this,intent,flags,startId);try{LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mFloatingView = (LinearLayout) li.inflate(R.layout.layout_floating_camera_view, null);
        hidenCameraView = (LinearLayout) li.inflate(R.layout.layout_floating_camera_view_hide, null);

        cameraView = mFloatingView.findViewById(R.id.cameraView);
        ImageButton hideCameraBtn = mFloatingView.findViewById(R.id.hide_camera);
        ImageButton switchCameraBtn = mFloatingView.findViewById(R.id.switch_camera);
        resizeOverlay = mFloatingView.findViewById(R.id.overlayResize);

        hidenCameraView = hidenCameraView.findViewById(R.id.rootOverlayExpandBtn);
        values = new Values();

        hideCameraBtn.setOnClickListener(this);
        switchCameraBtn.setOnClickListener(this);
        resizeOverlay.setOnClickListener(this);

        mCurrentView = mFloatingView;

        int xPos = getXPos();
        int yPos = getYPos();
        int layoutType;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            {layoutType = WindowManager.LayoutParams.TYPE_PHONE;}
        else
            {layoutType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;}

        /*//Add the view to the window.*/
        params = new WindowManager.LayoutParams(
                values.smallCameraX,
                values.smallCameraY,
                layoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        /*//Specify the view position*/
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = xPos;
        params.y = yPos;

        /*//Add the view to the window*/
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mCurrentView, params);

        cameraView.start();
        setupDragListener();

        {com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.onStartCommand(android.content.Intent,int,int)",this);return START_STICKY;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.onStartCommand(android.content.Intent,int,int)",this,throwable);throw throwable;}
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.onConfigurationChanged(android.content.res.Configuration)",this,newConfig);try{super.onConfigurationChanged(newConfig);
        changeCameraOrientation();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.onConfigurationChanged(android.content.res.Configuration)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.onConfigurationChanged(android.content.res.Configuration)",this,throwable);throw throwable;}
    }

    private void changeCameraOrientation() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.changeCameraOrientation()",this);try{values.buildValues();
        int x = overlayResize == OverlayResize.MAXWINDOW ? values.bigCameraX : values.smallCameraX;
        int y = overlayResize == OverlayResize.MAXWINDOW ? values.bigCameraY : values.smallCameraY;
        if (!isCameraViewHidden) {
            params.height = y;
            params.width = x;
            mWindowManager.updateViewLayout(mCurrentView, params);
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.changeCameraOrientation()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.changeCameraOrientation()",this,throwable);throw throwable;}
    }

    private void setupDragListener() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.setupDragListener()",this);try{mCurrentView.setOnTouchListener(new View.OnTouchListener() {
            boolean isMoving = false;
            private WindowManager.LayoutParams paramsF = params;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                com.mijack.Xlog.logMethodEnter("boolean com.orpheusdroid.screenrecorder.services.FloatingCameraViewService$1.onTouch(android.view.View,android.view.MotionEvent)",this,v,event);try{switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isMoving = false;
                        initialX = paramsF.x;
                        initialY = paramsF.y;
                        /*//get the touch location*/
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        {com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.setupDragListener()",this);{com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.services.FloatingCameraViewService$1.onTouch(android.view.View,android.view.MotionEvent)",this);return true;}}
                    case MotionEvent.ACTION_UP:
                        if (!isMoving && mCurrentView.equals(hidenCameraView)) {
                            showCameraView();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        /*//Calculate the X and Y coordinates of the view.*/
                        int xDiff = (int) (event.getRawX() - initialTouchX);
                        int yDiff = (int) (event.getRawY() - initialTouchY);
                        paramsF.x = initialX + xDiff;
                        paramsF.y = initialY + yDiff;
                        /* Set an offset of 10 pixels to determine controls moving. Else, normal touches
                         * could react as moving the control window around */
                        if (Math.abs(xDiff) > 10 || Math.abs(yDiff) > 10)
                            {isMoving = true;}
                        mWindowManager.updateViewLayout(mCurrentView, paramsF);
                        persistCoordinates(initialX + xDiff, initialY + yDiff);
                        {com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.setupDragListener()",this);{com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.services.FloatingCameraViewService$1.onTouch(android.view.View,android.view.MotionEvent)",this);return true;}}
                }
                {com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.setupDragListener()",this);{com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.services.FloatingCameraViewService$1.onTouch(android.view.View,android.view.MotionEvent)",this);return false;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.orpheusdroid.screenrecorder.services.FloatingCameraViewService$1.onTouch(android.view.View,android.view.MotionEvent)",this,throwable);throw throwable;}
            }
        });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.setupDragListener()",this,throwable);throw throwable;}
    }

    private int getXPos() {
        com.mijack.Xlog.logMethodEnter("int com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.getXPos()",this);try{String pos = getDefaultPrefs().getString(Const.PREFS_CAMERA_OVERLAY_POS, "0X100");
        {com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.getXPos()",this);return Integer.parseInt(pos.split("X")[0]);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.getXPos()",this,throwable);throw throwable;}
    }

    private int getYPos() {
        com.mijack.Xlog.logMethodEnter("int com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.getYPos()",this);try{String pos = getDefaultPrefs().getString(Const.PREFS_CAMERA_OVERLAY_POS, "0X100");
        {com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.getYPos()",this);return Integer.parseInt(pos.split("X")[1]);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.getYPos()",this,throwable);throw throwable;}
    }

    private void persistCoordinates(int x, int y) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.persistCoordinates(int,int)",this,x,y);try{getDefaultPrefs().edit()
                .putString(Const.PREFS_CAMERA_OVERLAY_POS, String.valueOf(x) + "X" + String.valueOf(y))
                .apply();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.persistCoordinates(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.persistCoordinates(int,int)",this,throwable);throw throwable;}
    }

    private SharedPreferences getDefaultPrefs() {
        com.mijack.Xlog.logMethodEnter("android.content.SharedPreferences com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.getDefaultPrefs()",this);try{if (prefs == null)
            {prefs = PreferenceManager.getDefaultSharedPreferences(this);}
        {com.mijack.Xlog.logMethodExit("android.content.SharedPreferences com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.getDefaultPrefs()",this);return prefs;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.content.SharedPreferences com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.getDefaultPrefs()",this,throwable);throw throwable;}
    }

    @Override
    public void onDestroy() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.onDestroy()",this);try{super.onDestroy();
        if (mFloatingView != null) {mWindowManager.removeView(mCurrentView);}
        cameraView.stop();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.onDestroy()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.onDestroy()",this,throwable);throw throwable;}
    }

    @Override
    public void onClick(View view) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.onClick(android.view.View)",this,view);try{switch (view.getId()) {
            case R.id.switch_camera:
                if (cameraView.getFacing() == CameraView.FACING_BACK) {
                    cameraView.setFacing(CameraView.FACING_FRONT);
                    cameraView.setAutoFocus(true);
                } else {
                    cameraView.setFacing(CameraView.FACING_BACK);
                    cameraView.setAutoFocus(true);
                }
                break;
            case R.id.hide_camera:
                Log.d(Const.TAG, "hide camera");
                if (mCurrentView.equals(mFloatingView)) {
                    mWindowManager.removeViewImmediate(mCurrentView);
                    mCurrentView = hidenCameraView;
                    params.width = values.cameraHideX;
                    params.height = values.cameraHideY;
                    mWindowManager.addView(mCurrentView, params);
                }
                setupDragListener();
                break;
            case R.id.overlayResize:
                updateCameraView();
                break;
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.onClick(android.view.View)",this,throwable);throw throwable;}
    }

    private void showCameraView() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.showCameraView()",this);try{if (mCurrentView.equals(hidenCameraView)) {
            mWindowManager.removeViewImmediate(mCurrentView);
            mCurrentView = mFloatingView;
            if (overlayResize == OverlayResize.MINWINDOW)
                {overlayResize = OverlayResize.MAXWINDOW;}
            else
                {overlayResize = OverlayResize.MINWINDOW;}
            mWindowManager.addView(mCurrentView, params);
            isCameraViewHidden = false;
            updateCameraView();
            setupDragListener();
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.showCameraView()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.showCameraView()",this,throwable);throw throwable;}
    }

    private void updateCameraView() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.updateCameraView()",this);try{if (overlayResize == OverlayResize.MINWINDOW) {
            params.width = values.bigCameraX;
            params.height = values.bigCameraY;
            overlayResize = OverlayResize.MAXWINDOW;
            resizeOverlay.setImageResource(R.drawable.ic_bigscreen_exit);
        } else {
            params.width = values.smallCameraX;
            params.height = values.smallCameraY;
            overlayResize = OverlayResize.MINWINDOW;
            resizeOverlay.setImageResource(R.drawable.ic_bigscreen);
        }
        mWindowManager.updateViewLayout(mCurrentView, params);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.updateCameraView()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService.updateCameraView()",this,throwable);throw throwable;}
    }

    private enum OverlayResize {
        MAXWINDOW, MINWINDOW
    }

    private class Values {
        int smallCameraX;
        int smallCameraY;
        int bigCameraX;
        int bigCameraY;
        int cameraHideX;
        int cameraHideY;

        public Values() {
            buildValues();
            cameraHideX = dpToPx(60);
            cameraHideY = dpToPx(60);
        }

        private int dpToPx(int dp) {
            com.mijack.Xlog.logMethodEnter("int com.orpheusdroid.screenrecorder.services.FloatingCameraViewService$Values.dpToPx(int)",this,dp);try{DisplayMetrics displayMetrics = FloatingCameraViewService.this.getResources().getDisplayMetrics();
            {com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.services.FloatingCameraViewService$Values.dpToPx(int)",this);return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.orpheusdroid.screenrecorder.services.FloatingCameraViewService$Values.dpToPx(int)",this,throwable);throw throwable;}
        }

        void buildValues() {
            com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService$Values.buildValues()",this);try{int orientation = context.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                smallCameraX = dpToPx(160);
                smallCameraY = dpToPx(120);
                bigCameraX = dpToPx(200);
                bigCameraY = dpToPx(150);
            } else {
                smallCameraX = dpToPx(120);
                smallCameraY = dpToPx(160);
                bigCameraX = dpToPx(150);
                bigCameraY = dpToPx(200);
            }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService$Values.buildValues()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.services.FloatingCameraViewService$Values.buildValues()",this,throwable);throw throwable;}
        }
    }

    public class ServiceBinder extends Binder {
        FloatingCameraViewService getService() {
            com.mijack.Xlog.logMethodEnter("com.orpheusdroid.screenrecorder.services.FloatingCameraViewService com.orpheusdroid.screenrecorder.services.FloatingCameraViewService$ServiceBinder.getService()",this);try{com.mijack.Xlog.logMethodExit("com.orpheusdroid.screenrecorder.services.FloatingCameraViewService com.orpheusdroid.screenrecorder.services.FloatingCameraViewService$ServiceBinder.getService()",this);return FloatingCameraViewService.this;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.orpheusdroid.screenrecorder.services.FloatingCameraViewService com.orpheusdroid.screenrecorder.services.FloatingCameraViewService$ServiceBinder.getService()",this,throwable);throw throwable;}
        }
    }
}