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

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.Const.RecordingState;
import com.orpheusdroid.screenrecorder.R;

import androidx.annotation.Nullable;

/**
 * Service to handle floating controls
 * <p>
 *     A service class to manage floating controls (start/pause/stop)
 *     <br /> Pause is not available on all the devices
 * </p>
 *
 * @author Vijai Chandra Prasad .R
 */
public class FloatingControlService extends Service implements View.OnClickListener {

    /**
     * WindowManager instance for handling floating controls
     */
    private WindowManager windowManager;

    /**
     * Floating controls layout
     */
    private LinearLayout floatingControls;

    /**
     * View which holds the floating controls
     */
    private View controls;

    /**
     * pause button for floating controls
     */
    private ImageButton pauseIB;

    /**
     * Resume button for floating controls
     */
    private ImageButton resumeIB;

    /**
     * ServiceBinder instance
     *
     * @see ServiceBinder
     */
    private IBinder binder = new ServiceBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        com.mijack.Xlog.logMethodEnter("int com.orpheusdroid.screenrecorder.services.FloatingControlService.onStartCommand(android.content.Intent,int,int)",this,intent,flags,startId);try{windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        /*//Inflate the layout using LayoutInflater*/
        LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        floatingControls = (LinearLayout) li.inflate(R.layout.layout_floating_controls, null);
        controls = floatingControls.findViewById(R.id.controls);

        /*//Initialize imageButtons*/
        ImageButton stopIB = controls.findViewById(R.id.stop);
        pauseIB = controls.findViewById(R.id.pause);
        resumeIB = controls.findViewById(R.id.resume);
        resumeIB.setEnabled(false);

        stopIB.setOnClickListener(this);

        /*//Get floating control icon size from sharedpreference*/
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        /*//Pause/Resume doesnt work below SDK version 24. Remove them*/
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            pauseIB.setVisibility(View.GONE);
            resumeIB.setVisibility(View.GONE);
            controls.findViewById(R.id.divider1).setVisibility(View.GONE);
            controls.findViewById(R.id.divider2).setVisibility(View.GONE);
        } else {
            pauseIB.setOnClickListener(this);
            resumeIB.setOnClickListener(this);
        }

        /*//Set layout params to display the controls over any screen.*/
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                dpToPx(pref.getInt(getString(R.string.preference_floating_control_size_key), 100)),
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        /*// From API26, TYPE_PHONE depricated. Use TYPE_APPLICATION_OVERLAY for O*/
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            {params.type = WindowManager.LayoutParams.TYPE_PHONE;}

        /*//Initial position of the floating controls*/
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;

        /*//Add the controls view to windowmanager*/
        windowManager.addView(floatingControls, params);

        /*//Add touch listerner to floating controls view to move/close/expand the controls*/
        try {
            floatingControls.setOnTouchListener(new View.OnTouchListener() {
                boolean isMoving = false;
                private WindowManager.LayoutParams paramsF = params;
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    com.mijack.Xlog.logMethodEnter("boolean com.orpheusdroid.screenrecorder.services.FloatingControlService$1.onTouch(android.view.View,android.view.MotionEvent)",this,v,event);try{switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            isMoving = false;
                            initialX = paramsF.x;
                            initialY = paramsF.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            if (!isMoving) {
                                if (controls.getVisibility() == View.INVISIBLE) {
                                    expandFloatingControls();
                                } else {
                                    collapseFloatingControls();
                                }
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            int xDiff = (int) (event.getRawX() - initialTouchX);
                            int yDiff = (int) (event.getRawY() - initialTouchY);
                            paramsF.x = initialX + xDiff;
                            paramsF.y = initialY + yDiff;
                            /* Set an offset of 10 pixels to determine controls moving. Else, normal touches
                             * could react as moving the control window around */
                            if (Math.abs(xDiff) > 10 || Math.abs(yDiff) > 10)
                                {isMoving = true;}
                            windowManager.updateViewLayout(floatingControls, paramsF);
                            break;
                    }
                    {com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.services.FloatingControlService.onStartCommand(android.content.Intent,int,int)",this);{com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.services.FloatingControlService$1.onTouch(android.view.View,android.view.MotionEvent)",this);return false;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.orpheusdroid.screenrecorder.services.FloatingControlService$1.onTouch(android.view.View,android.view.MotionEvent)",this,throwable);throw throwable;}
                }
            });
        } catch (Exception e) {
            /*// TODO: handle exception*/
        }
        {com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.services.FloatingControlService.onStartCommand(android.content.Intent,int,int)",this);return START_STICKY;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.orpheusdroid.screenrecorder.services.FloatingControlService.onStartCommand(android.content.Intent,int,int)",this,throwable);throw throwable;}
    }

    /**
     * Expand the floating window on touch of the logo
     */
    private void expandFloatingControls() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingControlService.expandFloatingControls()",this);try{controls.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        controls.measure(widthSpec, heightSpec);

        /*//Animate the expanding floating window*/
        ValueAnimator mAnimator = slideAnimator(0, controls.getMeasuredWidth());
        mAnimator.start();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingControlService.expandFloatingControls()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.services.FloatingControlService.expandFloatingControls()",this,throwable);throw throwable;}
    }

    /**
     * Collapse the floating control if expanded
     */
    private void collapseFloatingControls() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingControlService.collapseFloatingControls()",this);try{int finalHeight = controls.getWidth();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            {com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingControlService$2.onAnimationStart(android.animation.Animator)",this,animation);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingControlService$2.onAnimationStart(android.animation.Animator)",this);}

            @Override
            public void onAnimationEnd(Animator animator) {
                com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingControlService$2.onAnimationEnd(android.animation.Animator)",this,animator);try{/*//Height=0, but it set visibility to INVISIBLE at the end of animation*/
                controls.setVisibility(View.INVISIBLE);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingControlService$2.onAnimationEnd(android.animation.Animator)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.services.FloatingControlService$2.onAnimationEnd(android.animation.Animator)",this,throwable);throw throwable;}
            }

            {com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingControlService$2.onAnimationCancel(android.animation.Animator)",this,animation);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingControlService$2.onAnimationCancel(android.animation.Animator)",this);}

            {com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingControlService$2.onAnimationRepeat(android.animation.Animator)",this,animation);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingControlService$2.onAnimationRepeat(android.animation.Animator)",this);}

        });
        mAnimator.start();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingControlService.collapseFloatingControls()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.services.FloatingControlService.collapseFloatingControls()",this,throwable);throw throwable;}
    }

    private ValueAnimator slideAnimator(int start, int end) {

        com.mijack.Xlog.logMethodEnter("android.animation.ValueAnimator com.orpheusdroid.screenrecorder.services.FloatingControlService.slideAnimator(int,int)",this,start,end);try{ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingControlService$3.onAnimationUpdate(android.animation.ValueAnimator)",this,valueAnimator);try{/*//Update width*/
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = controls.getLayoutParams();
                layoutParams.width = value;
                controls.setLayoutParams(layoutParams);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingControlService$3.onAnimationUpdate(android.animation.ValueAnimator)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.services.FloatingControlService$3.onAnimationUpdate(android.animation.ValueAnimator)",this,throwable);throw throwable;}
            }
        });
        {com.mijack.Xlog.logMethodExit("android.animation.ValueAnimator com.orpheusdroid.screenrecorder.services.FloatingControlService.slideAnimator(int,int)",this);return animator;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.animation.ValueAnimator com.orpheusdroid.screenrecorder.services.FloatingControlService.slideAnimator(int,int)",this,throwable);throw throwable;}
    }

    /*//Onclick override to handle button clicks*/
    @Override
    public void onClick(View view) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingControlService.onClick(android.view.View)",this,view);try{switch (view.getId()) {
            case R.id.stop:
                stopScreenSharing();
                break;
            case R.id.pause:
                pauseScreenRecording();
                break;
            case R.id.resume:
                resumeScreenRecording();
                break;
        }

        /*//Provide an haptic feedback on button press*/
        Vibrator vibrate = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        vibrate.vibrate(100);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingControlService.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.services.FloatingControlService.onClick(android.view.View)",this,throwable);throw throwable;}
    }

    /**
     * Set resume intent and start the recording service
     * NOTE: A service can be started only once. Any subsequent startService only passes the intent
     * if any by calling onStartCommand
     */
    private void resumeScreenRecording() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingControlService.resumeScreenRecording()",this);try{Intent resumeIntent = new Intent(this, RecorderService.class);
        resumeIntent.setAction(Const.SCREEN_RECORDING_RESUME);
        startService(resumeIntent);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingControlService.resumeScreenRecording()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.services.FloatingControlService.resumeScreenRecording()",this,throwable);throw throwable;}
    }

    /**
     * Set pause intent and start the recording service
     */
    private void pauseScreenRecording() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingControlService.pauseScreenRecording()",this);try{Intent pauseIntent = new Intent(this, RecorderService.class);
        pauseIntent.setAction(Const.SCREEN_RECORDING_PAUSE);
        startService(pauseIntent);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingControlService.pauseScreenRecording()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.services.FloatingControlService.pauseScreenRecording()",this,throwable);throw throwable;}
    }

    /**
     * Set stop intent and start the recording service
     */
    private void stopScreenSharing() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingControlService.stopScreenSharing()",this);try{Intent stopIntent = new Intent(this, RecorderService.class);
        stopIntent.setAction(Const.SCREEN_RECORDING_STOP);
        startService(stopIntent);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingControlService.stopScreenSharing()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.services.FloatingControlService.stopScreenSharing()",this,throwable);throw throwable;}
    }

    /**
     * Enable/disable pause/resume ImageButton depending on the current recording state
     * @param state {@link RecordingState}
     */
    public void setRecordingState(RecordingState state) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingControlService.setRecordingState(com.orpheusdroid.screenrecorder.Const.RecordingState)",this,state);try{switch (state) {
            case PAUSED:
                pauseIB.setEnabled(false);
                resumeIB.setEnabled(true);
                break;
            case RECORDING:
                pauseIB.setEnabled(true);
                resumeIB.setEnabled(false);
                break;
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingControlService.setRecordingState(com.orpheusdroid.screenrecorder.Const.RecordingState)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.services.FloatingControlService.setRecordingState(com.orpheusdroid.screenrecorder.Const.RecordingState)",this,throwable);throw throwable;}
    }

    @Override
    public void onDestroy() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.services.FloatingControlService.onDestroy()",this);try{if (floatingControls != null) {windowManager.removeView(floatingControls);}
        super.onDestroy();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.services.FloatingControlService.onDestroy()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.services.FloatingControlService.onDestroy()",this,throwable);throw throwable;}
    }

    /*//Return ServiceBinder instance on successful binding*/
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        com.mijack.Xlog.logMethodEnter("android.os.IBinder com.orpheusdroid.screenrecorder.services.FloatingControlService.onBind(android.content.Intent)",this,intent);try{Log.d(Const.TAG, "Binding successful!");
        {com.mijack.Xlog.logMethodExit("android.os.IBinder com.orpheusdroid.screenrecorder.services.FloatingControlService.onBind(android.content.Intent)",this);return binder;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.os.IBinder com.orpheusdroid.screenrecorder.services.FloatingControlService.onBind(android.content.Intent)",this,throwable);throw throwable;}
    }

    /*//Stop the service once the service is unbinded from recording service*/
    @Override
    public boolean onUnbind(Intent intent) {
        com.mijack.Xlog.logMethodEnter("boolean com.orpheusdroid.screenrecorder.services.FloatingControlService.onUnbind(android.content.Intent)",this,intent);try{Log.d(Const.TAG, "Unbinding and stopping service");
        stopSelf();
        {com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.services.FloatingControlService.onUnbind(android.content.Intent)",this);return super.onUnbind(intent);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.orpheusdroid.screenrecorder.services.FloatingControlService.onUnbind(android.content.Intent)",this,throwable);throw throwable;}
    }

    /**
     * Method to convert dp to px
     * @param dp int
     * @return int
     */
    private int dpToPx(int dp) {
        com.mijack.Xlog.logMethodEnter("int com.orpheusdroid.screenrecorder.services.FloatingControlService.dpToPx(int)",this,dp);try{DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        {com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.services.FloatingControlService.dpToPx(int)",this);return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.orpheusdroid.screenrecorder.services.FloatingControlService.dpToPx(int)",this,throwable);throw throwable;}
    }

    /**
     * Binder class for binding to recording service
     */
    public class ServiceBinder extends Binder {
        FloatingControlService getService() {
            com.mijack.Xlog.logMethodEnter("com.orpheusdroid.screenrecorder.services.FloatingControlService com.orpheusdroid.screenrecorder.services.FloatingControlService$ServiceBinder.getService()",this);try{com.mijack.Xlog.logMethodExit("com.orpheusdroid.screenrecorder.services.FloatingControlService com.orpheusdroid.screenrecorder.services.FloatingControlService$ServiceBinder.getService()",this);return FloatingControlService.this;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.orpheusdroid.screenrecorder.services.FloatingControlService com.orpheusdroid.screenrecorder.services.FloatingControlService$ServiceBinder.getService()",this,throwable);throw throwable;}
        }
    }
}
