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

public class FloatAnimation extends Animation {

    private final float mFrom;
    private final float mTo;
    private float mCurrent;

    public FloatAnimation(float from, float to, int duration) {
        mFrom = from;
        mTo = to;
        mCurrent = from;
        setDuration(duration);
    }

    @Override
    protected void onCalculate(float progress) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.anim.FloatAnimation.onCalculate(float)",this,progress);try{mCurrent = mFrom + (mTo - mFrom) * progress;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.anim.FloatAnimation.onCalculate(float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.anim.FloatAnimation.onCalculate(float)",this,throwable);throw throwable;}
    }

    public float get() {
        com.mijack.Xlog.logMethodEnter("float com.android.gallery3d.anim.FloatAnimation.get()",this);try{com.mijack.Xlog.logMethodExit("float com.android.gallery3d.anim.FloatAnimation.get()",this);return mCurrent;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("float com.android.gallery3d.anim.FloatAnimation.get()",this,throwable);throw throwable;}
    }
}
