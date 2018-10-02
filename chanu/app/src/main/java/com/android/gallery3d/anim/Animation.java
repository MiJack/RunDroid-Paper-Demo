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

package com.android.gallery3d.anim;

import com.android.gallery3d.common.Utils;

import android.view.animation.Interpolator;

/*// Animation calculates a value according to the current input time.*/
/*//*/
/*// 1. First we need to use setDuration(int) to set the duration of the*/
/*//    animation. The duration is in milliseconds.*/
/*// 2. Then we should call start(). The actual start time is the first value*/
/*//    passed to calculate(long).*/
/*// 3. Each time we want to get an animation value, we call*/
/*//    calculate(long currentTimeMillis) to ask the Animation to calculate it.*/
/*//    The parameter passed to calculate(long) should be nonnegative.*/
/*// 4. Use get() to get that value.*/
/*//*/
/*// In step 3, onCalculate(float progress) is called so subclasses can calculate*/
/*// the value according to progress (progress is a value in [0,1]).*/
/*//*/
/*// Before onCalculate(float) is called, There is an optional interpolator which*/
/*// can change the progress value. The interpolator can be set by*/
/*// setInterpolator(Interpolator). If the interpolator is used, the value passed*/
/*// to onCalculate may be (for example, the overshoot effect).*/
/*//*/
/*// The isActive() method returns true after the animation start() is called and*/
/*// before calculate is passed a value which reaches the duration of the*/
/*// animation.*/
/*//*/
/*// The start() method can be called again to restart the Animation.*/
/*//*/
abstract public class Animation {
    private static final long ANIMATION_START = -1;
    private static final long NO_ANIMATION = -2;

    private long mStartTime = NO_ANIMATION;
    private int mDuration;
    private Interpolator mInterpolator;

    public void setInterpolator(Interpolator interpolator) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.anim.Animation.setInterpolator(android.view.animation.Interpolator)",this,interpolator);try{mInterpolator = interpolator;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.anim.Animation.setInterpolator(android.view.animation.Interpolator)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.anim.Animation.setInterpolator(android.view.animation.Interpolator)",this,throwable);throw throwable;}
    }

    public void setDuration(int duration) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.anim.Animation.setDuration(int)",this,duration);try{mDuration = duration;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.anim.Animation.setDuration(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.anim.Animation.setDuration(int)",this,throwable);throw throwable;}
    }

    public void start() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.anim.Animation.start()",this);try{mStartTime = ANIMATION_START;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.anim.Animation.start()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.anim.Animation.start()",this,throwable);throw throwable;}
    }

    public void setStartTime(long time) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.anim.Animation.setStartTime(long)",this,time);try{mStartTime = time;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.anim.Animation.setStartTime(long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.anim.Animation.setStartTime(long)",this,throwable);throw throwable;}
    }

    public boolean isActive() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.anim.Animation.isActive()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.anim.Animation.isActive()",this);return mStartTime != NO_ANIMATION;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.anim.Animation.isActive()",this,throwable);throw throwable;}
    }

    public void forceStop() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.anim.Animation.forceStop()",this);try{mStartTime = NO_ANIMATION;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.anim.Animation.forceStop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.anim.Animation.forceStop()",this,throwable);throw throwable;}
    }

    public boolean calculate(long currentTimeMillis) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.anim.Animation.calculate(long)",this,currentTimeMillis);try{if (mStartTime == NO_ANIMATION) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.anim.Animation.calculate(long)",this);return false;}}
        if (mStartTime == ANIMATION_START) {mStartTime = currentTimeMillis;}
        int elapse = (int) (currentTimeMillis - mStartTime);
        float x = Utils.clamp((float) elapse / mDuration, 0f, 1f);
        Interpolator i = mInterpolator;
        onCalculate(i != null ? i.getInterpolation(x) : x);
        if (elapse >= mDuration) {mStartTime = NO_ANIMATION;}
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.anim.Animation.calculate(long)",this);return mStartTime != NO_ANIMATION;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.anim.Animation.calculate(long)",this,throwable);throw throwable;}
    }

    abstract protected void onCalculate(float progress);
}
