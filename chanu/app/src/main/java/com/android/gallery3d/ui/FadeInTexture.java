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

import android.os.SystemClock;

/*// FadeInTexture is a texture which begins with a color, then gradually animates*/
/*// into a given texture.*/
public class FadeInTexture implements Texture {
    private static final String TAG = "FadeInTexture";

    /*// The duration of the animation in milliseconds*/
    private static final int DURATION = 180;

    private final BasicTexture mTexture;
    private final int mColor;
    private final long mStartTime;
    private final int mWidth;
    private final int mHeight;
    private final boolean mIsOpaque;
    private boolean mIsAnimating;

    public FadeInTexture(int color, BasicTexture texture) {
        mColor = color;
        mTexture = texture;
        mWidth = mTexture.getWidth();
        mHeight = mTexture.getHeight();
        mIsOpaque = mTexture.isOpaque();
        mStartTime = now();
        mIsAnimating = true;
    }

    public void draw(GLCanvas canvas, int x, int y) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FadeInTexture.draw(GLCanvas,int,int)",this,canvas,x,y);try{draw(canvas, x, y, mWidth, mHeight);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FadeInTexture.draw(GLCanvas,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FadeInTexture.draw(GLCanvas,int,int)",this,throwable);throw throwable;}
    }

    public void draw(GLCanvas canvas, int x, int y, int w, int h) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.FadeInTexture.draw(GLCanvas,int,int,int,int)",this,canvas,x,y,w,h);try{if (isAnimating()) {
            canvas.drawMixed(mTexture, mColor, getRatio(), x, y, w, h);
        } else {
            mTexture.draw(canvas, x, y, w, h);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.FadeInTexture.draw(GLCanvas,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.FadeInTexture.draw(GLCanvas,int,int,int,int)",this,throwable);throw throwable;}
    }

    public boolean isOpaque() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.FadeInTexture.isOpaque()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.FadeInTexture.isOpaque()",this);return mIsOpaque;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.FadeInTexture.isOpaque()",this,throwable);throw throwable;}
    }

    public int getWidth() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.FadeInTexture.getWidth()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.FadeInTexture.getWidth()",this);return mWidth;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.FadeInTexture.getWidth()",this,throwable);throw throwable;}
    }

    public int getHeight() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.FadeInTexture.getHeight()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.FadeInTexture.getHeight()",this);return mHeight;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.FadeInTexture.getHeight()",this,throwable);throw throwable;}
    }

    public boolean isAnimating() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.FadeInTexture.isAnimating()",this);try{if (mIsAnimating) {
            if (now() - mStartTime >= DURATION) {
                mIsAnimating = false;
            }
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.FadeInTexture.isAnimating()",this);return mIsAnimating;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.FadeInTexture.isAnimating()",this,throwable);throw throwable;}
    }

    private float getRatio() {
        com.mijack.Xlog.logMethodEnter("float com.android.gallery3d.ui.FadeInTexture.getRatio()",this);try{float r = (float)(now() - mStartTime) / DURATION;
        {com.mijack.Xlog.logMethodExit("float com.android.gallery3d.ui.FadeInTexture.getRatio()",this);return Utils.clamp(1.0f - r, 0.0f, 1.0f);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("float com.android.gallery3d.ui.FadeInTexture.getRatio()",this,throwable);throw throwable;}
    }

    private long now() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.ui.FadeInTexture.now()",this);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.ui.FadeInTexture.now()",this);return SystemClock.uptimeMillis();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.ui.FadeInTexture.now()",this,throwable);throw throwable;}
    }
}
