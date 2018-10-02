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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * A view that tracks touch motions as paths and paints them as doodles.
 */
class DoodleView extends FullscreenToolView {

    /**
     * Listener of doodle paths.
     */
    public interface OnDoodleChangeListener {

        void onDoodleInPhotoBounds();

        void onDoodleFinished(Path path, int color);
    }

    private final Path normalizedPath = new Path();
    private final Path drawingPath = new Path();
    private final Paint doodlePaint = new DoodlePaint();
    private final Paint bitmapPaint = new Paint(Paint.DITHER_FLAG);
    private final PointF lastPoint = new PointF();
    private final Matrix pathMatrix = new Matrix();
    private final Matrix displayMatrix = new Matrix();

    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private OnDoodleChangeListener listener;

    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnDoodleChangeListener(OnDoodleChangeListener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.DoodleView.setOnDoodleChangeListener(OnDoodleChangeListener)",this,listener);try{this.listener = listener;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.DoodleView.setOnDoodleChangeListener(OnDoodleChangeListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.DoodleView.setOnDoodleChangeListener(OnDoodleChangeListener)",this,throwable);throw throwable;}
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.DoodleView.onSizeChanged(int,int,int,int)",this,w,h,oldw,oldh);try{super.onSizeChanged(w, h, oldw, oldh);

        RectF r = new RectF(0, 0, getPhotoWidth(), getPhotoHeight());
        if ((bitmap == null) && !r.isEmpty()) {
            bitmap = Bitmap.createBitmap((int) r.width(), (int) r.height(),
                    Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(0x00000000);
            bitmapCanvas = new Canvas(bitmap);

            /*// Set up a matrix that maps back normalized paths to be drawn on the bitmap or canvas.*/
            pathMatrix.setRectToRect(new RectF(0, 0, 1, 1), r, Matrix.ScaleToFit.FILL);
        }
        displayMatrix.setRectToRect(r, displayBounds, Matrix.ScaleToFit.FILL);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.DoodleView.onSizeChanged(int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.DoodleView.onSizeChanged(int,int,int,int)",this,throwable);throw throwable;}
    }

    private void drawDoodle(Canvas canvas) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.DoodleView.drawDoodle(android.graphics.Canvas)",this,canvas);try{if ((canvas != null) && !normalizedPath.isEmpty()) {
            drawingPath.set(normalizedPath);
            drawingPath.transform(pathMatrix);
            canvas.drawPath(drawingPath, doodlePaint);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.DoodleView.drawDoodle(android.graphics.Canvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.DoodleView.drawDoodle(android.graphics.Canvas)",this,throwable);throw throwable;}
    }

    public void setColor(int color) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.DoodleView.setColor(int)",this,color);try{/*// Reset path to draw in a new color.*/
        finishCurrentPath();
        normalizedPath.moveTo(lastPoint.x, lastPoint.y);
        doodlePaint.setColor(Color.argb(192, Color.red(color), Color.green(color),
                Color.blue(color)));com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.DoodleView.setColor(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.DoodleView.setColor(int)",this,throwable);throw throwable;}
    }

    private void finishCurrentPath() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.DoodleView.finishCurrentPath()",this);try{if (!normalizedPath.isEmpty()) {
            /*// Update the finished path to the bitmap.*/
            drawDoodle(bitmapCanvas);
            if (listener != null) {
                listener.onDoodleFinished(new Path(normalizedPath), doodlePaint.getColor());
            }
            normalizedPath.rewind();
            invalidate();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.DoodleView.finishCurrentPath()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.DoodleView.finishCurrentPath()",this,throwable);throw throwable;}
    }

    private void checkCurrentPathInBounds() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.DoodleView.checkCurrentPathInBounds()",this);try{if ((listener != null) && !normalizedPath.isEmpty()) {
            RectF r = new RectF();
            normalizedPath.computeBounds(r, false);
            if (r.intersects(0, 0, 1, 1)) {
                listener.onDoodleInPhotoBounds();
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.DoodleView.checkCurrentPathInBounds()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.DoodleView.checkCurrentPathInBounds()",this,throwable);throw throwable;}
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.photoeditor.actions.DoodleView.onTouchEvent(android.view.MotionEvent)",this,event);try{super.onTouchEvent(event);

        if (isEnabled()) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mapPhotoPoint(x, y, lastPoint);
                    normalizedPath.moveTo(lastPoint.x, lastPoint.y);
                    break;

                case MotionEvent.ACTION_MOVE:
                    float lastX = lastPoint.x;
                    float lastY = lastPoint.y;
                    mapPhotoPoint(x, y, lastPoint);
                    normalizedPath.quadTo(lastX, lastY, (lastX + lastPoint.x) / 2,
                            (lastY + lastPoint.y) / 2);
                    checkCurrentPathInBounds();
                    invalidate();
                    break;

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    /*// Line to last position with offset to draw at least dots for single clicks.*/
                    mapPhotoPoint(x + 1, y + 1, lastPoint);
                    normalizedPath.lineTo(lastPoint.x, lastPoint.y);
                    checkCurrentPathInBounds();
                    finishCurrentPath();
                    break;
            }
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.photoeditor.actions.DoodleView.onTouchEvent(android.view.MotionEvent)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.photoeditor.actions.DoodleView.onTouchEvent(android.view.MotionEvent)",this,throwable);throw throwable;}
    }

    @Override
    protected void onDraw(Canvas canvas) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.actions.DoodleView.onDraw(android.graphics.Canvas)",this,canvas);try{super.onDraw(canvas);

        canvas.save();
        canvas.clipRect(displayBounds);
        canvas.concat(displayMatrix);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
        }
        drawDoodle(canvas);
        canvas.restore();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.actions.DoodleView.onDraw(android.graphics.Canvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.actions.DoodleView.onDraw(android.graphics.Canvas)",this,throwable);throw throwable;}
    }
}
