/*
 * Copyright (C) 2011 The Android Open Source Project
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

/*// This is a customized version of Scroller, with a interface similar to*/
/*// android.widget.Scroller. It does fling only, not scroll.*/
/*//*/
/*// The differences between the this Scroller and the system one are:*/
/*//*/
/*// (1) The velocity does not change because of min/max limit.*/
/*// (2) The duration is different.*/
/*// (3) The deceleration curve is different.*/
class FlingScroller {
    private static final String TAG = "FlingController";

    /*// The fling duration (in milliseconds) when velocity is 1 pixel/second*/
    private static final float FLING_DURATION_PARAM = 50f;
    private static final int DECELERATED_FACTOR = 4;

    private int mStartX, mStartY;
    private int mMinX, mMinY, mMaxX, mMaxY;
    private double mSinAngle;
    private double mCosAngle;
    private int mDuration;
    private int mDistance;
    private int mFinalX, mFinalY;

    private int mCurrX, mCurrY;
    private double mCurrV;

    public int getFinalX() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.FlingScroller.getFinalX()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.FlingScroller.getFinalX()",this);return mFinalX;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.FlingScroller.getFinalX()",this,throwable);throw throwable;}
    }

    public int getFinalY() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.FlingScroller.getFinalY()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.FlingScroller.getFinalY()",this);return mFinalY;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.FlingScroller.getFinalY()",this,throwable);throw throwable;}
    }

    public int getDuration() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.FlingScroller.getDuration()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.FlingScroller.getDuration()",this);return mDuration;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.FlingScroller.getDuration()",this,throwable);throw throwable;}
    }

    public int getCurrX() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.FlingScroller.getCurrX()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.FlingScroller.getCurrX()",this);return mCurrX;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.FlingScroller.getCurrX()",this,throwable);throw throwable;}

    }

    public int getCurrY() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.FlingScroller.getCurrY()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.FlingScroller.getCurrY()",this);return mCurrY;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.FlingScroller.getCurrY()",this,throwable);throw throwable;}
    }

    public int getCurrVelocityX() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.FlingScroller.getCurrVelocityX()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.FlingScroller.getCurrVelocityX()",this);return (int)Math.round(mCurrV * mCosAngle);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.FlingScroller.getCurrVelocityX()",this,throwable);throw throwable;}
    }

    public int getCurrVelocityY() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.FlingScroller.getCurrVelocityY()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.FlingScroller.getCurrVelocityY()",this);return (int)Math.round(mCurrV * mSinAngle);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.FlingScroller.getCurrVelocityY()",this,throwable);throw throwable;}
    }

    public void fling(int startX, int startY, int velocityX, int velocityY,
            int minX, int maxX, int minY, int maxY) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FlingScroller.fling(int,int,int,int,int,int,int,int)",this,startX,startY,velocityX,velocityY,minX,maxX,minY,maxY);try{mStartX = startX;
        mStartY = startY;
        mMinX = minX;
        mMinY = minY;
        mMaxX = maxX;
        mMaxY = maxY;

        double velocity = Math.hypot(velocityX, velocityY);
        mSinAngle = velocityY / velocity;
        mCosAngle = velocityX / velocity;
        /*//*/
        /*// The position formula: x(t) = s + (e - s) * (1 - (1 - t / T) ^ d)*/
        /*//     velocity formula: v(t) = d * (e - s) * (1 - t / T) ^ (d - 1) / T*/
        /*// Thus,*/
        /*//     v0 = d * (e - s) / T => (e - s) = v0 * T / d*/
        /*//*/

        /*// Ta = T_ref * (Va / V_ref) ^ (1 / (d - 1)); V_ref = 1 pixel/second;*/
        mDuration = (int)Math.round(FLING_DURATION_PARAM
                * Math.pow(Math.abs(velocity), 1.0 / (DECELERATED_FACTOR - 1)));

        /*// (e - s) = v0 * T / d*/
        mDistance = (int)Math.round(
                velocity * mDuration / DECELERATED_FACTOR / 1000);

        mFinalX = getX(1.0f);
        mFinalY = getY(1.0f);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FlingScroller.fling(int,int,int,int,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FlingScroller.fling(int,int,int,int,int,int,int,int)",this,throwable);throw throwable;}
    }

    public void computeScrollOffset(float progress) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FlingScroller.computeScrollOffset(float)",this,progress);try{progress = Math.min(progress, 1);
        float f = 1 - progress;
        f = 1 - (float) Math.pow(f, DECELERATED_FACTOR);
        mCurrX = getX(f);
        mCurrY = getY(f);
        mCurrV = getV(progress);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FlingScroller.computeScrollOffset(float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FlingScroller.computeScrollOffset(float)",this,throwable);throw throwable;}
    }

    private int getX(float f) {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.FlingScroller.getX(float)",this,f);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.FlingScroller.getX(float)",this);return (int) Utils.clamp(
                Math.round(mStartX + f * mDistance * mCosAngle), mMinX, mMaxX);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.FlingScroller.getX(float)",this,throwable);throw throwable;}
    }

    private int getY(float f) {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.FlingScroller.getY(float)",this,f);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.FlingScroller.getY(float)",this);return (int) Utils.clamp(
                Math.round(mStartY + f * mDistance * mSinAngle), mMinY, mMaxY);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.FlingScroller.getY(float)",this,throwable);throw throwable;}
    }

    private double getV(float progress) {
        com.mijack.Xlog.logMethodEnter("double com.android.gallery3d.ui.FlingScroller.getV(float)",this,progress);try{/*// velocity formula: v(t) = d * (e - s) * (1 - t / T) ^ (d - 1) / T*/
        {com.mijack.Xlog.logMethodExit("double com.android.gallery3d.ui.FlingScroller.getV(float)",this);return DECELERATED_FACTOR * mDistance * 1000 *
                Math.pow(1 - progress, DECELERATED_FACTOR - 1) / mDuration;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("double com.android.gallery3d.ui.FlingScroller.getV(float)",this,throwable);throw throwable;}
    }
}
