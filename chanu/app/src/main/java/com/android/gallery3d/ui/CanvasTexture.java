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
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;

/*// CanvasTexture is a texture whose content is the drawing on a Canvas.*/
/*// The subclasses should override onDraw() to draw on the bitmap.*/
/*// By default CanvasTexture is not opaque.*/
abstract class CanvasTexture extends UploadedTexture {
    protected Canvas mCanvas;
    private final Config mConfig;

    public CanvasTexture(int width, int height) {
        mConfig = Config.ARGB_8888;
        setSize(width, height);
        setOpaque(false);
    }

    @Override
    protected Bitmap onGetBitmap() {
        com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.ui.CanvasTexture.onGetBitmap()",this);try{Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, mConfig);
        mCanvas = new Canvas(bitmap);
        onDraw(mCanvas, bitmap);
        {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.ui.CanvasTexture.onGetBitmap()",this);return bitmap;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.ui.CanvasTexture.onGetBitmap()",this,throwable);throw throwable;}
    }

    @Override
    protected BitmapFactory.Options onGetBitmapBounds() {
        com.mijack.Xlog.logMethodEnter("BitmapFactory.Options com.android.gallery3d.ui.CanvasTexture.onGetBitmapBounds()",this);try{BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = mWidth;
        options.outHeight = mHeight;
        {com.mijack.Xlog.logMethodExit("BitmapFactory.Options com.android.gallery3d.ui.CanvasTexture.onGetBitmapBounds()",this);return options;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("BitmapFactory.Options com.android.gallery3d.ui.CanvasTexture.onGetBitmapBounds()",this,throwable);throw throwable;}
    }

    @Override
    protected void onFreeBitmap(Bitmap bitmap) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.CanvasTexture.onFreeBitmap(android.graphics.Bitmap)",this,bitmap);try{if (!inFinalizer()) {
            bitmap.recycle();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.CanvasTexture.onFreeBitmap(android.graphics.Bitmap)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.CanvasTexture.onFreeBitmap(android.graphics.Bitmap)",this,throwable);throw throwable;}
    }

    abstract protected void onDraw(Canvas canvas, Bitmap backing);
}
