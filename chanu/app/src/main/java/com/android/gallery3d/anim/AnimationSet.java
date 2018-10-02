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

import com.android.gallery3d.ui.GLCanvas;

import java.util.ArrayList;

public class AnimationSet extends CanvasAnimation {

    private final ArrayList<CanvasAnimation> mAnimations =
            new ArrayList<CanvasAnimation>();
    private int mSaveFlags = 0;


    public void addAnimation(CanvasAnimation anim) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.anim.AnimationSet.addAnimation(com.android.gallery3d.anim.CanvasAnimation)",this,anim);try{mAnimations.add(anim);
        mSaveFlags |= anim.getCanvasSaveFlags();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.anim.AnimationSet.addAnimation(com.android.gallery3d.anim.CanvasAnimation)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.anim.AnimationSet.addAnimation(com.android.gallery3d.anim.CanvasAnimation)",this,throwable);throw throwable;}
    }

    @Override
    public void apply(GLCanvas canvas) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.anim.AnimationSet.apply(com.android.gallery3d.ui.GLCanvas)",this,canvas);try{for (int i = 0, n = mAnimations.size(); i < n; i++) {
            mAnimations.get(i).apply(canvas);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.anim.AnimationSet.apply(com.android.gallery3d.ui.GLCanvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.anim.AnimationSet.apply(com.android.gallery3d.ui.GLCanvas)",this,throwable);throw throwable;}
    }

    @Override
    public int getCanvasSaveFlags() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.anim.AnimationSet.getCanvasSaveFlags()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.anim.AnimationSet.getCanvasSaveFlags()",this);return mSaveFlags;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.anim.AnimationSet.getCanvasSaveFlags()",this,throwable);throw throwable;}
    }

    @Override
    protected void onCalculate(float progress) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.anim.AnimationSet.onCalculate(float)",this,progress);try{/*// DO NOTHING*/com.mijack.Xlog.logMethodExit("void com.android.gallery3d.anim.AnimationSet.onCalculate(float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.anim.AnimationSet.onCalculate(float)",this,throwable);throw throwable;}
    }

    @Override
    public boolean calculate(long currentTimeMillis) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.anim.AnimationSet.calculate(long)",this,currentTimeMillis);try{boolean more = false;
        for (CanvasAnimation anim : mAnimations) {
            more |= anim.calculate(currentTimeMillis);
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.anim.AnimationSet.calculate(long)",this);return more;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.anim.AnimationSet.calculate(long)",this,throwable);throw throwable;}
    }

    @Override
    public void start() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.anim.AnimationSet.start()",this);try{for (CanvasAnimation anim : mAnimations) {
            anim.start();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.anim.AnimationSet.start()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.anim.AnimationSet.start()",this,throwable);throw throwable;}
    }

    @Override
    public boolean isActive() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.anim.AnimationSet.isActive()",this);try{for (CanvasAnimation anim : mAnimations) {
            if (anim.isActive()) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.anim.AnimationSet.isActive()",this);return true;}}
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.anim.AnimationSet.isActive()",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.anim.AnimationSet.isActive()",this,throwable);throw throwable;}
    }

}
