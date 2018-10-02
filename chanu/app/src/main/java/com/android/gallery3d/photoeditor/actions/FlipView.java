/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.gallery3d.photoeditor.actions;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * View that handles touch-events to track flipping directions and angles.
 */
class FlipView extends FullscreenToolView {

    /**
     * Listens to flip changes.
     */
    public interface OnFlipChangeListener {

        void onAngleChanged(float horizontalDegrees, float verticalDegrees, boolean fromUser);

        void onStartTrackingTouch();

        void onStopTrackingTouch();
    }

    private static final float FIXED_DIRECTION_THRESHOLD = 20;

    private OnFlipChangeListener listener;
    private float maxFlipSpan;
    private float touchStartX;
    private float touchStartY;
    private float currentHorizontalDegrees;
    private float currentVerticalDegrees;
    private float lastHorizontalDegrees;
    private float lastVerticalDegrees;
    private boolean fixedDirection;
    private boolean fixedDirectionHorizontal;

    public FlipView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnFlipChangeListener(OnFlipChangeListener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.FlipView.setOnFlipChangeListener(OnFlipChangeListener)",this,listener);try{this.listener = listener;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.FlipView.setOnFlipChangeListener(OnFlipChangeListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.FlipView.setOnFlipChangeListener(OnFlipChangeListener)",this,throwable);throw throwable;}
    }

    public void setFlippedAngles(float horizontalDegrees, float verticalDegrees) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.FlipView.setFlippedAngles(float,float)",this,horizontalDegrees,verticalDegrees);try{refreshAngle(horizontalDegrees, verticalDegrees, false);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.FlipView.setFlippedAngles(float,float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.FlipView.setFlippedAngles(float,float)",this,throwable);throw throwable;}
    }

    /**
     * Sets allowed degrees for every flip before flipping the view.
     */
    public void setFlipSpan(float degrees) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.FlipView.setFlipSpan(float)",this,degrees);try{/*// Flip-span limits allowed flipping degrees of every flip for usability purpose; the max.*/
        /*// flipped angles could be accumulated and larger than allowed flip-span.*/
        maxFlipSpan = degrees;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.FlipView.setFlipSpan(float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.FlipView.setFlipSpan(float)",this,throwable);throw throwable;}
    }

    private float calculateAngle(boolean flipHorizontal, float x, float y) {
        com.mijack.Xlog.logMethodEnter("float com.android.gallery3d.photoeditor.actions.FlipView.calculateAngle(boolean,float,float)",this,flipHorizontal,x,y);try{/*// Use partial length along the moving direction to calculate the flip angle.*/
        float maxDistance = (flipHorizontal ? getWidth() : getHeight()) * 0.35f;
        float moveDistance = flipHorizontal ? (x - touchStartX) : (touchStartY - y);

        if (Math.abs(moveDistance) > maxDistance) {
            moveDistance = (moveDistance > 0) ? maxDistance : -maxDistance;

            if (flipHorizontal) {
                touchStartX = x - moveDistance;
            } else {
                touchStartY = moveDistance + y;
            }
        }
        {com.mijack.Xlog.logMethodExit("float com.android.gallery3d.photoeditor.actions.FlipView.calculateAngle(boolean,float,float)",this);return (moveDistance / maxDistance) * maxFlipSpan;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("float com.android.gallery3d.photoeditor.actions.FlipView.calculateAngle(boolean,float,float)",this,throwable);throw throwable;}
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.photoeditor.actions.FlipView.onTouchEvent(android.view.MotionEvent)",this,ev);try{super.onTouchEvent(ev);

        if (isEnabled()) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    fixedDirection = false;
                    lastHorizontalDegrees = currentHorizontalDegrees;
                    lastVerticalDegrees = currentVerticalDegrees;
                    touchStartX = ev.getX();
                    touchStartY = ev.getY();

                    if (listener != null) {
                        listener.onStartTrackingTouch();
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    /*// Allow only one direction for flipping during movements, and make the*/
                    /*// direction fixed once it exceeds threshold.*/
                    float x = ev.getX();
                    float y = ev.getY();
                    boolean flipHorizontal = fixedDirection ? fixedDirectionHorizontal
                            : (Math.abs(x - touchStartX) >= Math.abs(y - touchStartY));
                    float degrees = calculateAngle(flipHorizontal, x, y);
                    if (!fixedDirection && (Math.abs(degrees) > FIXED_DIRECTION_THRESHOLD)) {
                        fixedDirection = true;
                        fixedDirectionHorizontal = flipHorizontal;
                    }

                    if (flipHorizontal) {
                        refreshAngle(lastHorizontalDegrees + degrees, lastVerticalDegrees, true);
                    } else {
                        refreshAngle(lastHorizontalDegrees, lastVerticalDegrees + degrees, true);
                    }
                   break;

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (listener != null) {
                        listener.onStopTrackingTouch();
                    }
                    break;
            }
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.photoeditor.actions.FlipView.onTouchEvent(android.view.MotionEvent)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.photoeditor.actions.FlipView.onTouchEvent(android.view.MotionEvent)",this,throwable);throw throwable;}
    }

    private void refreshAngle(float horizontalDegrees, float verticalDegrees, boolean fromUser) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.FlipView.refreshAngle(float,float,boolean)",this,horizontalDegrees,verticalDegrees,fromUser);try{currentHorizontalDegrees = horizontalDegrees;
        currentVerticalDegrees = verticalDegrees;
        if (listener != null) {
            listener.onAngleChanged(horizontalDegrees, verticalDegrees, fromUser);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.FlipView.refreshAngle(float,float,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.FlipView.refreshAngle(float,float,boolean)",this,throwable);throw throwable;}
    }
}
