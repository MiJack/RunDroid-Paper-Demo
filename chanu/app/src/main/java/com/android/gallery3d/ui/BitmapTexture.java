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

import android.graphics.BitmapFactory;
import com.android.gallery3d.common.Utils;

import android.graphics.Bitmap;

/*// BitmapTexture is a texture whose content is specified by a fixed Bitmap.*/
/*//*/
/*// The texture does not own the Bitmap. The user should make sure the Bitmap*/
/*// is valid during the texture's lifetime. When the texture is recycled, it*/
/*// does not free the Bitmap.*/
public class BitmapTexture extends UploadedTexture {
    protected Bitmap mContentBitmap;

    public BitmapTexture(Bitmap bitmap) {
        this(bitmap, false);
    }

    public BitmapTexture(Bitmap bitmap, boolean hasBorder) {
        super(hasBorder);
        Utils.assertTrue(bitmap != null && !bitmap.isRecycled());
        mContentBitmap = bitmap;
    }

    @Override
    protected void onFreeBitmap(Bitmap bitmap) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.BitmapTexture.onFreeBitmap(android.graphics.Bitmap)",this,bitmap);try{/*// Do nothing.*/com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.BitmapTexture.onFreeBitmap(android.graphics.Bitmap)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.BitmapTexture.onFreeBitmap(android.graphics.Bitmap)",this,throwable);throw throwable;}
    }

    @Override
    protected Bitmap onGetBitmap() {
        com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.ui.BitmapTexture.onGetBitmap()",this);try{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.ui.BitmapTexture.onGetBitmap()",this);return mContentBitmap;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.ui.BitmapTexture.onGetBitmap()",this,throwable);throw throwable;}
    }

    @Override
    protected BitmapFactory.Options onGetBitmapBounds() {
        com.mijack.Xlog.logMethodEnter("BitmapFactory.Options com.android.gallery3d.ui.BitmapTexture.onGetBitmapBounds()",this);try{BitmapFactory.Options options = new BitmapFactory.Options();
        if (mContentBitmap != null) {
            options.outWidth = mContentBitmap.getWidth();
            options.outHeight = mContentBitmap.getHeight();
        }
        else {
            options.outWidth = 0;
            options.outHeight = 0;
        }
        {com.mijack.Xlog.logMethodExit("BitmapFactory.Options com.android.gallery3d.ui.BitmapTexture.onGetBitmapBounds()",this);return options;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("BitmapFactory.Options com.android.gallery3d.ui.BitmapTexture.onGetBitmapBounds()",this,throwable);throw throwable;}
    }

    public Bitmap getBitmap() {
        com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.ui.BitmapTexture.getBitmap()",this);try{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.ui.BitmapTexture.getBitmap()",this);return mContentBitmap;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.ui.BitmapTexture.getBitmap()",this,throwable);throw throwable;}
    }
}
