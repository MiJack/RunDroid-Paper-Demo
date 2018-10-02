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

import android.content.Context;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

public class ScrollerHelper {
    private OverScroller mScroller;
    private int mOverflingDistance;
    private boolean mOverflingEnabled;

    public ScrollerHelper(Context context) {
        mScroller = new OverScroller(context);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mOverflingDistance = configuration.getScaledOverflingDistance();
    }

    public void setOverfling(boolean enabled) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.ScrollerHelper.setOverfling(boolean)",this,enabled);try{mOverflingEnabled = enabled;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.ScrollerHelper.setOverfling(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.ScrollerHelper.setOverfling(boolean)",this,throwable);throw throwable;}
    }

    /**
     * Call this when you want to know the new location. The position will be
     * updated and can be obtained by getPosition(). Returns true if  the
     * animation is not yet finished.
     */
    public boolean advanceAnimation(long currentTimeMillis) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.ScrollerHelper.advanceAnimation(long)",this,currentTimeMillis);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.ScrollerHelper.advanceAnimation(long)",this);return mScroller.computeScrollOffset();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.ScrollerHelper.advanceAnimation(long)",this,throwable);throw throwable;}
    }

    public boolean isFinished() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.ScrollerHelper.isFinished()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.ScrollerHelper.isFinished()",this);return mScroller.isFinished();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.ScrollerHelper.isFinished()",this,throwable);throw throwable;}
    }

    public void forceFinished() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.ScrollerHelper.forceFinished()",this);try{mScroller.forceFinished(true);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.ScrollerHelper.forceFinished()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.ScrollerHelper.forceFinished()",this,throwable);throw throwable;}
    }

    public int getPosition() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.ScrollerHelper.getPosition()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.ScrollerHelper.getPosition()",this);return mScroller.getCurrX();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.ScrollerHelper.getPosition()",this,throwable);throw throwable;}
    }

    public float getCurrVelocity() {
        com.mijack.Xlog.logMethodEnter("float com.android.gallery3d.ui.ScrollerHelper.getCurrVelocity()",this);try{com.mijack.Xlog.logMethodExit("float com.android.gallery3d.ui.ScrollerHelper.getCurrVelocity()",this);return mScroller.getCurrVelocity();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("float com.android.gallery3d.ui.ScrollerHelper.getCurrVelocity()",this,throwable);throw throwable;}
    }

    public void setPosition(int position) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.ScrollerHelper.setPosition(int)",this,position);try{mScroller.startScroll(
                position, 0,    /*// startX, startY*/
                0, 0, 0);       /*// dx, dy, duration*/

        /*// This forces the scroller to reach the final position.*/
        mScroller.abortAnimation();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.ScrollerHelper.setPosition(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.ScrollerHelper.setPosition(int)",this,throwable);throw throwable;}
    }

    public void fling(int velocity, int min, int max) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.ScrollerHelper.fling(int,int,int)",this,velocity,min,max);try{int currX = getPosition();
        mScroller.fling(
                currX, 0,      /*// startX, startY*/
                velocity, 0,   /*// velocityX, velocityY*/
                min, max,      /*// minX, maxX*/
                0, 0,          /*// minY, maxY*/
                mOverflingEnabled ? mOverflingDistance : 0, 0);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.ScrollerHelper.fling(int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.ScrollerHelper.fling(int,int,int)",this,throwable);throw throwable;}
    }

    /*// Returns the distance that over the scroll limit.*/
    public int startScroll(int distance, int min, int max) {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.ScrollerHelper.startScroll(int,int,int)",this,distance,min,max);try{int currPosition = mScroller.getCurrX();
        int finalPosition = mScroller.getFinalX();
        int newPosition = Utils.clamp(finalPosition + distance, min, max);
        if (newPosition != currPosition) {
            mScroller.startScroll(
                currPosition, 0,                    /*// startX, startY*/
                newPosition - currPosition, 0, 0);  /*// dx, dy, duration*/
        }
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.ScrollerHelper.startScroll(int,int,int)",this);return finalPosition + distance - newPosition;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.ScrollerHelper.startScroll(int,int,int)",this,throwable);throw throwable;}
    }
}
