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

package com.android.gallery3d.ui;

import com.android.gallery3d.anim.CanvasAnimation;
import com.android.gallery3d.anim.FloatAnimation;

import android.graphics.Bitmap;
import android.graphics.PointF;

import java.util.Random;
import javax.microedition.khronos.opengles.GL11;

public class SlideshowView extends GLView {
    @SuppressWarnings("unused")
    private static final String TAG = "SlideshowView";

    private static final int SLIDESHOW_DURATION = 3500;
    private static final int TRANSITION_DURATION = 1000;

    private static final float SCALE_SPEED = 0.20f ;
    private static final float MOVE_SPEED = SCALE_SPEED;

    private int mCurrentRotation;
    private BitmapTexture mCurrentTexture;
    private SlideshowAnimation mCurrentAnimation;

    private int mPrevRotation;
    private BitmapTexture mPrevTexture;
    private SlideshowAnimation mPrevAnimation;

    private final FloatAnimation mTransitionAnimation =
            new FloatAnimation(0, 1, TRANSITION_DURATION);

    private Random mRandom = new Random();

    public void next(Bitmap bitmap, int rotation) {

        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.SlideshowView.next(android.graphics.Bitmap,int)",this,bitmap,rotation);try{mTransitionAnimation.start();

        if (mPrevTexture != null) {
            mPrevTexture.getBitmap().recycle();
            mPrevTexture.recycle();
        }

        mPrevTexture = mCurrentTexture;
        mPrevAnimation = mCurrentAnimation;
        mPrevRotation = mCurrentRotation;

        mCurrentRotation = rotation;
        mCurrentTexture = new BitmapTexture(bitmap);
        if (((rotation / 90) & 0x01) == 0) {
            mCurrentAnimation = new SlideshowAnimation(
                    mCurrentTexture.getWidth(), mCurrentTexture.getHeight(),
                    mRandom);
        } else {
            mCurrentAnimation = new SlideshowAnimation(
                    mCurrentTexture.getHeight(), mCurrentTexture.getWidth(),
                    mRandom);
        }
        mCurrentAnimation.start();

        invalidate();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.SlideshowView.next(android.graphics.Bitmap,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.SlideshowView.next(android.graphics.Bitmap,int)",this,throwable);throw throwable;}
    }

    public void release() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.SlideshowView.release()",this);try{if (mPrevTexture != null) {
            mPrevTexture.recycle();
            mPrevTexture = null;
        }
        if (mCurrentTexture != null) {
            mCurrentTexture.recycle();
            mCurrentTexture = null;
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.SlideshowView.release()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.SlideshowView.release()",this,throwable);throw throwable;}
    }

    @Override
    protected void render(GLCanvas canvas) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.SlideshowView.render(GLCanvas)",this,canvas);try{long currentTimeMillis = canvas.currentAnimationTimeMillis();
        boolean requestRender = mTransitionAnimation.calculate(currentTimeMillis);
        GL11 gl = canvas.getGLInstance();
        gl.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
        float alpha = mPrevTexture == null ? 1f : mTransitionAnimation.get();

        if (mPrevTexture != null && alpha != 1f) {
            requestRender |= mPrevAnimation.calculate(currentTimeMillis);
            canvas.save(GLCanvas.SAVE_FLAG_ALPHA | GLCanvas.SAVE_FLAG_MATRIX);
            canvas.setAlpha(1f - alpha);
            mPrevAnimation.apply(canvas);
            canvas.rotate(mPrevRotation, 0, 0, 1);
            mPrevTexture.draw(canvas, -mPrevTexture.getWidth() / 2,
                    -mPrevTexture.getHeight() / 2);
            canvas.restore();
        }
        if (mCurrentTexture != null) {
            requestRender |= mCurrentAnimation.calculate(currentTimeMillis);
            canvas.save(GLCanvas.SAVE_FLAG_ALPHA | GLCanvas.SAVE_FLAG_MATRIX);
            canvas.setAlpha(alpha);
            mCurrentAnimation.apply(canvas);
            canvas.rotate(mCurrentRotation, 0, 0, 1);
            mCurrentTexture.draw(canvas, -mCurrentTexture.getWidth() / 2,
                    -mCurrentTexture.getHeight() / 2);
            canvas.restore();
        }
        if (requestRender) {invalidate();}
        gl.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.SlideshowView.render(GLCanvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.SlideshowView.render(GLCanvas)",this,throwable);throw throwable;}
    }

    private class SlideshowAnimation extends CanvasAnimation {
        private final int mWidth;
        private final int mHeight;

        private final PointF mMovingVector;
        private float mProgress;

        public SlideshowAnimation(int width, int height, Random random) {
            mWidth = width;
            mHeight = height;
            mMovingVector = new PointF(
                    MOVE_SPEED * mWidth * (random.nextFloat() - 0.5f),
                    MOVE_SPEED * mHeight * (random.nextFloat() - 0.5f));
            setDuration(SLIDESHOW_DURATION);
        }

        @Override
        public void apply(GLCanvas canvas) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.SlideshowView$SlideshowAnimation.apply(GLCanvas)",this,canvas);try{int viewWidth = getWidth();
            int viewHeight = getHeight();

            float initScale = Math.min(2f, Math.min((float)
                    viewWidth / mWidth, (float) viewHeight / mHeight));
            float scale = initScale * (1 + SCALE_SPEED * mProgress);

            float centerX = viewWidth / 2 + mMovingVector.x * mProgress;
            float centerY = viewHeight / 2 + mMovingVector.y * mProgress;

            canvas.translate(centerX, centerY, 0);
            canvas.scale(scale, scale, 0);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.SlideshowView$SlideshowAnimation.apply(GLCanvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.SlideshowView$SlideshowAnimation.apply(GLCanvas)",this,throwable);throw throwable;}
        }

        @Override
        public int getCanvasSaveFlags() {
            com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.SlideshowView$SlideshowAnimation.getCanvasSaveFlags()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.SlideshowView$SlideshowAnimation.getCanvasSaveFlags()",this);return GLCanvas.SAVE_FLAG_MATRIX;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.SlideshowView$SlideshowAnimation.getCanvasSaveFlags()",this,throwable);throw throwable;}
        }

        @Override
        protected void onCalculate(float progress) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.SlideshowView$SlideshowAnimation.onCalculate(float)",this,progress);try{mProgress = progress;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.SlideshowView$SlideshowAnimation.onCalculate(float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.SlideshowView$SlideshowAnimation.onCalculate(float)",this,throwable);throw throwable;}
        }
    }
}
