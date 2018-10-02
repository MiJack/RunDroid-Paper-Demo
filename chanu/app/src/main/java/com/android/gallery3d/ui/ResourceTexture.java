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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/*// ResourceTexture is a texture whose Bitmap is decoded from a resource.*/
/*// By default ResourceTexture is not opaque.*/
public class ResourceTexture extends UploadedTexture {

    private static final String TAG = ResourceTexture.class.getSimpleName();

    protected final Context mContext;
    protected final int mResId;

    public ResourceTexture(Context context, int resId) {
        mContext = Utils.checkNotNull(context);
        mResId = resId;
        setOpaque(false);
    }

    @Override
    protected Bitmap onGetBitmap() {
        com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.ui.ResourceTexture.onGetBitmap()",this);try{BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeResource(
                mContext.getResources(), mResId, options);
        }
        catch (OutOfMemoryError e) {
            Log.e(TAG, "Couldn't get memory allocated for bitmap resId=" + mResId, e);
        }
        {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.ui.ResourceTexture.onGetBitmap()",this);return b;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.ui.ResourceTexture.onGetBitmap()",this,throwable);throw throwable;}
    }

    @Override
    protected BitmapFactory.Options onGetBitmapBounds() {
        com.mijack.Xlog.logMethodEnter("BitmapFactory.Options com.android.gallery3d.ui.ResourceTexture.onGetBitmapBounds()",this);try{BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {
            BitmapFactory.decodeResource(
                    mContext.getResources(), mResId, options);
        }
        catch (OutOfMemoryError e) {
            Log.e(TAG, "Couldn't get memory allocated for bitmap bounds resId=" + mResId, e);
        }
        {com.mijack.Xlog.logMethodExit("BitmapFactory.Options com.android.gallery3d.ui.ResourceTexture.onGetBitmapBounds()",this);return options;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("BitmapFactory.Options com.android.gallery3d.ui.ResourceTexture.onGetBitmapBounds()",this,throwable);throw throwable;}
    }

    @Override
    protected void onFreeBitmap(Bitmap bitmap) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.ResourceTexture.onFreeBitmap(android.graphics.Bitmap)",this,bitmap);try{if (!inFinalizer()) {
            bitmap.recycle();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.ResourceTexture.onFreeBitmap(android.graphics.Bitmap)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.ResourceTexture.onFreeBitmap(android.graphics.Bitmap)",this,throwable);throw throwable;}
    }
}
