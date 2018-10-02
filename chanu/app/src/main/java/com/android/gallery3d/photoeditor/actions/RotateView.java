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
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.chanapps.four.gallery3d.R;

/**
 * View that shows grids and handles touch-events to adjust angle of rotation.
 */
class RotateView extends FullscreenToolView {

    /**
     * Listens to rotate changes.
     */
    public interface OnRotateChangeListener {

        void onAngleChanged(float degrees, boolean fromUser);

        void onStartTrackingTouch();

        void onStopTrackingTouch();
    }

    /*// All angles used are defined between PI and -PI.*/
    private static final float MATH_PI = (float) Math.PI;
    private static final float MATH_HALF_PI = MATH_PI / 2;
    private static final float RADIAN_TO_DEGREE = 180f / MATH_PI;

    private final Paint dashStrokePaint;
    private final Path grids = new Path();
    private final Path referenceLine = new Path();
    private final int gridsColor;
    private final int referenceColor;

    private OnRotateChangeListener listener;
    private boolean drawGrids;
    private int centerX;
    private int centerY;
    private float maxRotatedAngle;
    private float minRotatedAngle;
    private float currentRotatedAngle;
    private float lastRotatedAngle;
    private float touchStartAngle;

    public RotateView(Context context, AttributeSet attrs) {
        super(context, attrs);

        dashStrokePaint = new Paint();
        dashStrokePaint.setAntiAlias(true);
        dashStrokePaint.setStyle(Paint.Style.STROKE);
        dashStrokePaint.setPathEffect(new DashPathEffect(new float[] {15.0f, 5.0f}, 1.0f));
        dashStrokePaint.setStrokeWidth(2f);
        gridsColor = context.getResources().getColor(R.color.translucent_white);
        referenceColor = context.getResources().getColor(R.color.translucent_cyan);
    }

    public void setRotatedAngle(float degrees) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.RotateView.setRotatedAngle(float)",this,degrees);try{refreshAngle(degrees, false);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.RotateView.setRotatedAngle(float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.RotateView.setRotatedAngle(float)",this,throwable);throw throwable;}
    }

    /**
     * Sets allowed degrees for rotation span before rotating the view.
     */
    public void setRotateSpan(float degrees) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.RotateView.setRotateSpan(float)",this,degrees);try{if (degrees >= 360f) {
            maxRotatedAngle = Float.POSITIVE_INFINITY;
        } else {
            maxRotatedAngle = (degrees / RADIAN_TO_DEGREE) / 2;
        }
        minRotatedAngle = -maxRotatedAngle;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.RotateView.setRotateSpan(float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.RotateView.setRotateSpan(float)",this,throwable);throw throwable;}
    }

    public void setOnRotateChangeListener(OnRotateChangeListener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.RotateView.setOnRotateChangeListener(OnRotateChangeListener)",this,listener);try{this.listener = listener;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.RotateView.setOnRotateChangeListener(OnRotateChangeListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.RotateView.setOnRotateChangeListener(OnRotateChangeListener)",this,throwable);throw throwable;}
    }

    public void setDrawGrids(boolean drawGrids) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.RotateView.setDrawGrids(boolean)",this,drawGrids);try{this.drawGrids = drawGrids;
        invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.RotateView.setDrawGrids(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.RotateView.setDrawGrids(boolean)",this,throwable);throw throwable;}
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.RotateView.onSizeChanged(int,int,int,int)",this,w,h,oldw,oldh);try{super.onSizeChanged(w, h, oldw, oldh);

        centerX = w / 2;
        centerY = h / 2;

        /*// Make reference line long enough to cross the bounds diagonally after being rotated.*/
        referenceLine.reset();
        float radius = (float) Math.hypot(centerX, centerY);
        float delta = radius - centerX;
        referenceLine.moveTo(-delta, centerY);
        referenceLine.lineTo(getWidth() + delta, centerY);
        delta = radius - centerY;
        referenceLine.moveTo(centerX, -delta);
        referenceLine.lineTo(centerX, getHeight() + delta);

        /*// Set grids inside photo display bounds.*/
        grids.reset();
        delta = displayBounds.width() / 4.0f;
        for (float x = displayBounds.left + delta; x < displayBounds.right; x += delta) {
            grids.moveTo(x, displayBounds.top);
            grids.lineTo(x, displayBounds.bottom);
        }
        delta = displayBounds.height() / 4.0f;
        for (float y = displayBounds.top + delta; y < displayBounds.bottom; y += delta) {
            grids.moveTo(displayBounds.left, y);
            grids.lineTo(displayBounds.right, y);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.RotateView.onSizeChanged(int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.RotateView.onSizeChanged(int,int,int,int)",this,throwable);throw throwable;}
    }

    @Override
    protected void onDraw(Canvas canvas) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.RotateView.onDraw(android.graphics.Canvas)",this,canvas);try{super.onDraw(canvas);

        if (drawGrids) {
            canvas.save();
            canvas.clipRect(displayBounds);
            dashStrokePaint.setColor(gridsColor);
            canvas.drawPath(grids, dashStrokePaint);

            canvas.rotate(-currentRotatedAngle * RADIAN_TO_DEGREE, centerX, centerY);
            dashStrokePaint.setColor(referenceColor);
            canvas.drawPath(referenceLine, dashStrokePaint);
            canvas.restore();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.RotateView.onDraw(android.graphics.Canvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.RotateView.onDraw(android.graphics.Canvas)",this,throwable);throw throwable;}
    }

    private float calculateAngle(MotionEvent ev) {
        com.mijack.Xlog.logMethodEnter("float com.android.gallery3d.photoeditor.actions.RotateView.calculateAngle(android.view.MotionEvent)",this,ev);try{float x = ev.getX() - centerX;
        float y = centerY - ev.getY();

        float angle;
        if (x == 0) {
            angle = (y >= 0) ? MATH_HALF_PI : -MATH_HALF_PI;
        } else {
            angle = (float) Math.atan(y / x);
        }

        if ((angle >= 0) && (x < 0)) {
            angle = angle - MATH_PI;
        } else if ((angle < 0) && (x < 0)) {
            angle = MATH_PI + angle;
        }
        {com.mijack.Xlog.logMethodExit("float com.android.gallery3d.photoeditor.actions.RotateView.calculateAngle(android.view.MotionEvent)",this);return angle;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("float com.android.gallery3d.photoeditor.actions.RotateView.calculateAngle(android.view.MotionEvent)",this,throwable);throw throwable;}
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.photoeditor.actions.RotateView.onTouchEvent(android.view.MotionEvent)",this,ev);try{super.onTouchEvent(ev);

        if (isEnabled()) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastRotatedAngle = currentRotatedAngle;
                    touchStartAngle = calculateAngle(ev);

                    if (listener != null) {
                        listener.onStartTrackingTouch();
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    float touchAngle = calculateAngle(ev);
                    float rotatedAngle = touchAngle - touchStartAngle + lastRotatedAngle;

                    if ((rotatedAngle > maxRotatedAngle) || (rotatedAngle < minRotatedAngle)) {
                        /*// Angles are out of range; restart rotating.*/
                        /*// TODO: Fix discontinuity around boundary.*/
                        lastRotatedAngle = currentRotatedAngle;
                        touchStartAngle = touchAngle;
                    } else {
                        refreshAngle(-rotatedAngle * RADIAN_TO_DEGREE, true);
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
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.photoeditor.actions.RotateView.onTouchEvent(android.view.MotionEvent)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.photoeditor.actions.RotateView.onTouchEvent(android.view.MotionEvent)",this,throwable);throw throwable;}
    }

    private void refreshAngle(float degrees, boolean fromUser) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.RotateView.refreshAngle(float,boolean)",this,degrees,fromUser);try{currentRotatedAngle = -degrees / RADIAN_TO_DEGREE;
        if (listener != null) {
            listener.onAngleChanged(degrees, fromUser);
        }
        invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.RotateView.refreshAngle(float,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.RotateView.refreshAngle(float,boolean)",this,throwable);throw throwable;}
    }
}
