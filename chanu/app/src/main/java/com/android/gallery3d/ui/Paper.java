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

import com.android.gallery3d.common.Utils;
import com.android.gallery3d.ui.PositionRepository.Position;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/*// This class does the overscroll effect.*/
class Paper {
    private static final String TAG = "Paper";
    private static final int ROTATE_FACTOR = 4;
    private EdgeAnimation mAnimationLeft = new EdgeAnimation();
    private EdgeAnimation mAnimationRight = new EdgeAnimation();
    private int mWidth, mHeight;
    private float[] mMatrix = new float[16];

    public void overScroll(float distance) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.Paper.overScroll(float)",this,distance);try{distance /= mWidth;  /*// make it relative to width*/
        if (distance < 0) {
            mAnimationLeft.onPull(-distance);
        } else {
            mAnimationRight.onPull(distance);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.Paper.overScroll(float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.Paper.overScroll(float)",this,throwable);throw throwable;}
    }

    public void edgeReached(float velocity) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.Paper.edgeReached(float)",this,velocity);try{velocity /= mWidth;  /*// make it relative to width*/
        if (velocity < 0) {
            mAnimationRight.onAbsorb(-velocity);
        } else {
            mAnimationLeft.onAbsorb(velocity);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.Paper.edgeReached(float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.Paper.edgeReached(float)",this,throwable);throw throwable;}
    }

    public void onRelease() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.Paper.onRelease()",this);try{mAnimationLeft.onRelease();
        mAnimationRight.onRelease();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.Paper.onRelease()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.Paper.onRelease()",this,throwable);throw throwable;}
    }

    public boolean advanceAnimation() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.Paper.advanceAnimation()",this);try{/*// Note that we use "|" because we want both animations get updated.*/
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.Paper.advanceAnimation()",this);return mAnimationLeft.update() | mAnimationRight.update();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.Paper.advanceAnimation()",this,throwable);throw throwable;}
    }

    public void setSize(int width, int height) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.Paper.setSize(int,int)",this,width,height);try{mWidth = width;
        mHeight = height;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.Paper.setSize(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.Paper.setSize(int,int)",this,throwable);throw throwable;}
    }

    public float[] getTransform(Position target, Position base,
            float scrollX, float scrollY) {
        com.mijack.Xlog.logMethodEnter("[float com.android.gallery3d.ui.Paper.getTransform(com.android.gallery3d.ui.PositionRepository.Position,com.android.gallery3d.ui.PositionRepository.Position,float,float)",this,target,base,scrollX,scrollY);try{float left = mAnimationLeft.getValue();
        float right = mAnimationRight.getValue();
        float screenX = target.x - scrollX;
        /*// We linearly interpolate the value [left, right] for the screenX*/
        /*// range int [-1/4, 5/4]*mWidth. So if part of the thumbnail is outside*/
        /*// the screen, we still get some transform.*/
        float x = screenX + mWidth / 4;
        int range = 3 * mWidth / 2;
        float t = ((range - x) * left - x * right) / range;
        /*// compress t to the range (-1, 1) by the function*/
        /*// f(t) = (1 / (1 + e^-t) - 0.5) * 2*/
        /*// then multiply by 90 to make the range (-45, 45)*/
        float degrees =
                (1 / (1 + (float) Math.exp(-t * ROTATE_FACTOR)) - 0.5f) * 2 * -45;
        Matrix.setIdentityM(mMatrix, 0);
        Matrix.translateM(mMatrix, 0, mMatrix, 0, base.x, base.y, base.z);
        Matrix.rotateM(mMatrix, 0, degrees, 0, 1, 0);
        Matrix.translateM(mMatrix, 0, mMatrix, 0,
                target.x - base.x, target.y - base.y, target.z - base.z);
        {com.mijack.Xlog.logMethodExit("[float com.android.gallery3d.ui.Paper.getTransform(com.android.gallery3d.ui.PositionRepository.Position,com.android.gallery3d.ui.PositionRepository.Position,float,float)",this);return mMatrix;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[float com.android.gallery3d.ui.Paper.getTransform(com.android.gallery3d.ui.PositionRepository.Position,com.android.gallery3d.ui.PositionRepository.Position,float,float)",this,throwable);throw throwable;}
    }
}

/*// This class follows the structure of frameworks's EdgeEffect class.*/
class EdgeAnimation {
    private static final String TAG = "EdgeAnimation";

    private static final int STATE_IDLE = 0;
    private static final int STATE_PULL = 1;
    private static final int STATE_ABSORB = 2;
    private static final int STATE_RELEASE = 3;

    /*// Time it will take the effect to fully done in ms*/
    private static final int ABSORB_TIME = 200;
    private static final int RELEASE_TIME = 500;

    private static final float VELOCITY_FACTOR = 0.1f;

    private final Interpolator mInterpolator;

    private int mState;
    private long mAnimationStartTime;
    private float mValue;

    private float mValueStart;
    private float mValueFinish;
    private long mStartTime;
    private long mDuration;

    public EdgeAnimation() {
        mInterpolator = new DecelerateInterpolator();
        mState = STATE_IDLE;
    }

    private void startAnimation(float start, float finish, long duration,
            int newState) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.EdgeAnimation.startAnimation(float,float,long,int)",this,start,finish,duration,newState);try{mValueStart = start;
        mValueFinish = finish;
        mDuration = duration;
        mStartTime = now();
        mState = newState;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.EdgeAnimation.startAnimation(float,float,long,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.EdgeAnimation.startAnimation(float,float,long,int)",this,throwable);throw throwable;}
    }

    /*// The deltaDistance's magnitude is in the range of -1 (no change) to 1.*/
    /*// The value 1 is the full length of the view. Negative values means the*/
    /*// movement is in the opposite direction.*/
    public void onPull(float deltaDistance) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.EdgeAnimation.onPull(float)",this,deltaDistance);try{if (mState == STATE_ABSORB) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.EdgeAnimation.onPull(float)",this);return;}}
        mValue = Utils.clamp(mValue + deltaDistance, -1.0f, 1.0f);
        mState = STATE_PULL;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.EdgeAnimation.onPull(float)",this,throwable);throw throwable;}
    }

    public void onRelease() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.EdgeAnimation.onRelease()",this);try{if (mState == STATE_IDLE || mState == STATE_ABSORB) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.EdgeAnimation.onRelease()",this);return;}}
        startAnimation(mValue, 0, RELEASE_TIME, STATE_RELEASE);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.EdgeAnimation.onRelease()",this,throwable);throw throwable;}
    }

    public void onAbsorb(float velocity) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.EdgeAnimation.onAbsorb(float)",this,velocity);try{float finish = Utils.clamp(mValue + velocity * VELOCITY_FACTOR,
                -1.0f, 1.0f);
        startAnimation(mValue, finish, ABSORB_TIME, STATE_ABSORB);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.EdgeAnimation.onAbsorb(float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.EdgeAnimation.onAbsorb(float)",this,throwable);throw throwable;}
    }

    public boolean update() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.EdgeAnimation.update()",this);try{if (mState == STATE_IDLE) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.EdgeAnimation.update()",this);return false;}}
        if (mState == STATE_PULL) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.EdgeAnimation.update()",this);return true;}}

        float t = Utils.clamp((float)(now() - mStartTime) / mDuration, 0.0f, 1.0f);
        /* Use linear interpolation for absorb, quadratic for others */
        float interp = (mState == STATE_ABSORB)
                ? t : mInterpolator.getInterpolation(t);

        mValue = mValueStart + (mValueFinish - mValueStart) * interp;

        if (t >= 1.0f) {
            switch (mState) {
                case STATE_ABSORB:
                    startAnimation(mValue, 0, RELEASE_TIME, STATE_RELEASE);
                    break;
                case STATE_RELEASE:
                    mState = STATE_IDLE;
                    break;
            }
        }

        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.EdgeAnimation.update()",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.EdgeAnimation.update()",this,throwable);throw throwable;}
    }

    public float getValue() {
        com.mijack.Xlog.logMethodEnter("float com.android.gallery3d.ui.EdgeAnimation.getValue()",this);try{com.mijack.Xlog.logMethodExit("float com.android.gallery3d.ui.EdgeAnimation.getValue()",this);return mValue;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("float com.android.gallery3d.ui.EdgeAnimation.getValue()",this,throwable);throw throwable;}
    }

    private long now() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.ui.EdgeAnimation.now()",this);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.ui.EdgeAnimation.now()",this);return SystemClock.uptimeMillis();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.ui.EdgeAnimation.now()",this,throwable);throw throwable;}
    }
}
