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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

/*// DrawableTexture is a texture whose content is from a Drawable.*/
public class DrawableTexture extends CanvasTexture {

    private final Drawable mDrawable;

    public DrawableTexture(Drawable drawable, int width, int height) {
        super(width, height);
        mDrawable = drawable;
    }

    @Override
    protected void onDraw(Canvas canvas, Bitmap backing) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DrawableTexture.onDraw(android.graphics.Canvas,android.graphics.Bitmap)",this,canvas,backing);try{mDrawable.setBounds(0, 0, mWidth, mHeight);
        mDrawable.draw(canvas);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DrawableTexture.onDraw(android.graphics.Canvas,android.graphics.Bitmap)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DrawableTexture.onDraw(android.graphics.Canvas,android.graphics.Bitmap)",this,throwable);throw throwable;}
    }
}
