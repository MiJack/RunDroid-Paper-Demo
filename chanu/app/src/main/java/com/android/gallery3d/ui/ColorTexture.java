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

/*// ColorTexture is a texture which fills the rectangle with the specified color.*/
public class ColorTexture implements Texture {

    private final int mColor;
    private int mWidth;
    private int mHeight;

    public ColorTexture(int color) {
        mColor = color;
        mWidth = 1;
        mHeight = 1;
    }

    public void draw(GLCanvas canvas, int x, int y) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.ColorTexture.draw(GLCanvas,int,int)",this,canvas,x,y);try{draw(canvas, x, y, mWidth, mHeight);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.ColorTexture.draw(GLCanvas,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.ColorTexture.draw(GLCanvas,int,int)",this,throwable);throw throwable;}
    }

    public void draw(GLCanvas canvas, int x, int y, int w, int h) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.ColorTexture.draw(GLCanvas,int,int,int,int)",this,canvas,x,y,w,h);try{canvas.fillRect(x, y, w, h, mColor);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.ColorTexture.draw(GLCanvas,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.ColorTexture.draw(GLCanvas,int,int,int,int)",this,throwable);throw throwable;}
    }

    public boolean isOpaque() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.ColorTexture.isOpaque()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.ColorTexture.isOpaque()",this);return Utils.isOpaque(mColor);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.ColorTexture.isOpaque()",this,throwable);throw throwable;}
    }

    public void setSize(int width, int height) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.ColorTexture.setSize(int,int)",this,width,height);try{mWidth = width;
        mHeight = height;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.ColorTexture.setSize(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.ColorTexture.setSize(int,int)",this,throwable);throw throwable;}
    }

    public int getWidth() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.ColorTexture.getWidth()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.ColorTexture.getWidth()",this);return mWidth;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.ColorTexture.getWidth()",this,throwable);throw throwable;}
    }

    public int getHeight() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.ColorTexture.getHeight()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.ColorTexture.getHeight()",this);return mHeight;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.ColorTexture.getHeight()",this,throwable);throw throwable;}
    }
}
