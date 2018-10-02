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
import com.android.gallery3d.ui.GLCanvas;

public class AlphaAnimation extends CanvasAnimation {
    private final float mStartAlpha;
    private final float mEndAlpha;
    private float mCurrentAlpha;

    public AlphaAnimation(float from, float to) {
        mStartAlpha = from;
        mEndAlpha = to;
        mCurrentAlpha = from;
    }

    @Override
    public void apply(GLCanvas canvas) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.anim.AlphaAnimation.apply(com.android.gallery3d.ui.GLCanvas)",this,canvas);try{canvas.multiplyAlpha(mCurrentAlpha);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.anim.AlphaAnimation.apply(com.android.gallery3d.ui.GLCanvas)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.anim.AlphaAnimation.apply(com.android.gallery3d.ui.GLCanvas)",this,throwable);throw throwable;}
    }

    @Override
    public int getCanvasSaveFlags() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.anim.AlphaAnimation.getCanvasSaveFlags()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.anim.AlphaAnimation.getCanvasSaveFlags()",this);return GLCanvas.SAVE_FLAG_ALPHA;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.anim.AlphaAnimation.getCanvasSaveFlags()",this,throwable);throw throwable;}
    }

    @Override
    protected void onCalculate(float progress) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.anim.AlphaAnimation.onCalculate(float)",this,progress);try{mCurrentAlpha = Utils.clamp(mStartAlpha
                + (mEndAlpha - mStartAlpha) * progress, 0f, 1f);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.anim.AlphaAnimation.onCalculate(float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.anim.AlphaAnimation.onCalculate(float)",this,throwable);throw throwable;}
    }
}
