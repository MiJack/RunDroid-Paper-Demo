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
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.android.gallery3d.common.Utils;

public class TileImageViewAdapter implements TileImageView.Model {
    private static final String TAG = "TileImageViewAdapter";
    protected BitmapRegionDecoder mRegionDecoder;
    protected int mImageWidth;
    protected int mImageHeight;
    protected Bitmap mBackupImage;
    protected int mLevelCount;
    protected boolean mFailedToLoad;

    private final Rect mIntersectRect = new Rect();
    private final Rect mRegionRect = new Rect();

    public TileImageViewAdapter() {
    }

    public TileImageViewAdapter(Bitmap backup, BitmapRegionDecoder regionDecoder) {
        mBackupImage = Utils.checkNotNull(backup);
        mRegionDecoder = regionDecoder;
        mImageWidth = regionDecoder.getWidth();
        mImageHeight = regionDecoder.getHeight();
        mLevelCount = calculateLevelCount();
    }

    public synchronized void clear() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageViewAdapter.clear()",this);try{mBackupImage = null;
        mImageWidth = 0;
        mImageHeight = 0;
        mLevelCount = 0;
        mRegionDecoder = null;
        mFailedToLoad = false;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageViewAdapter.clear()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageViewAdapter.clear()",this,throwable);throw throwable;}
    }

    public synchronized void setBackupImage(Bitmap backup, int width, int height) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageViewAdapter.setBackupImage(android.graphics.Bitmap,int,int)",this,backup,width,height);try{mBackupImage = Utils.checkNotNull(backup);
        mImageWidth = width;
        mImageHeight = height;
        mRegionDecoder = null;
        mLevelCount = 0;
        mFailedToLoad = false;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageViewAdapter.setBackupImage(android.graphics.Bitmap,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageViewAdapter.setBackupImage(android.graphics.Bitmap,int,int)",this,throwable);throw throwable;}
    }

    public synchronized void setRegionDecoder(BitmapRegionDecoder decoder) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageViewAdapter.setRegionDecoder(android.graphics.BitmapRegionDecoder)",this,decoder);try{mRegionDecoder = Utils.checkNotNull(decoder);
        mImageWidth = decoder.getWidth();
        mImageHeight = decoder.getHeight();
        mLevelCount = calculateLevelCount();
        mFailedToLoad = false;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageViewAdapter.setRegionDecoder(android.graphics.BitmapRegionDecoder)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageViewAdapter.setRegionDecoder(android.graphics.BitmapRegionDecoder)",this,throwable);throw throwable;}
    }

    private int calculateLevelCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.TileImageViewAdapter.calculateLevelCount()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.TileImageViewAdapter.calculateLevelCount()",this);return Math.max(0, Utils.ceilLog2(
                (float) mImageWidth / mBackupImage.getWidth()));}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.TileImageViewAdapter.calculateLevelCount()",this,throwable);throw throwable;}
    }

    @Override
    public synchronized Bitmap getTile(int level, int x, int y, int length) {
        com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.ui.TileImageViewAdapter.getTile(int,int,int,int)",this,level,x,y,length);try{if (mRegionDecoder == null) {{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.ui.TileImageViewAdapter.getTile(int,int,int,int)",this);return null;}}

        Rect region = mRegionRect;
        Rect intersectRect = mIntersectRect;
        region.set(x, y, x + (length << level), y + (length << level));
        intersectRect.set(0, 0, mImageWidth, mImageHeight);

        /*// Get the intersected rect of the requested region and the image.*/
        Utils.assertTrue(intersectRect.intersect(region));

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Config.ARGB_8888;
        options.inPreferQualityOverSpeed = true;
        options.inSampleSize =  (1 << level);

        Bitmap bitmap;

        /*// In CropImage, we may call the decodeRegion() concurrently.*/
        synchronized (mRegionDecoder) {
            bitmap = mRegionDecoder.decodeRegion(intersectRect, options);
        }

        /*// The returned region may not match with the targetLength.*/
        /*// If so, we fill black pixels on it.*/
        if (intersectRect.equals(region)) {{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.ui.TileImageViewAdapter.getTile(int,int,int,int)",this);return bitmap;}}

        if (bitmap == null) {
            Log.w(TAG, "fail in decoding region");
            {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.ui.TileImageViewAdapter.getTile(int,int,int,int)",this);return null;}
        }

        Bitmap tile = Bitmap.createBitmap(length, length, Config.ARGB_8888);
        Canvas canvas = new Canvas(tile);
        canvas.drawBitmap(bitmap,
                (intersectRect.left - region.left) >> level,
                (intersectRect.top - region.top) >> level, null);
        bitmap.recycle();
        {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.ui.TileImageViewAdapter.getTile(int,int,int,int)",this);return tile;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.ui.TileImageViewAdapter.getTile(int,int,int,int)",this,throwable);throw throwable;}
    }

    @Override
    public Bitmap getBackupImage() {
        com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.ui.TileImageViewAdapter.getBackupImage()",this);try{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.ui.TileImageViewAdapter.getBackupImage()",this);return mBackupImage;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.ui.TileImageViewAdapter.getBackupImage()",this,throwable);throw throwable;}
    }

    @Override
    public int getImageHeight() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.TileImageViewAdapter.getImageHeight()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.TileImageViewAdapter.getImageHeight()",this);return mImageHeight;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.TileImageViewAdapter.getImageHeight()",this,throwable);throw throwable;}
    }

    @Override
    public int getImageWidth() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.TileImageViewAdapter.getImageWidth()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.TileImageViewAdapter.getImageWidth()",this);return mImageWidth;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.TileImageViewAdapter.getImageWidth()",this,throwable);throw throwable;}
    }

    @Override
    public int getLevelCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.TileImageViewAdapter.getLevelCount()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.TileImageViewAdapter.getLevelCount()",this);return mLevelCount;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.TileImageViewAdapter.getLevelCount()",this,throwable);throw throwable;}
    }

    public void setFailedToLoad() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.TileImageViewAdapter.setFailedToLoad()",this);try{mFailedToLoad = true;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.TileImageViewAdapter.setFailedToLoad()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.TileImageViewAdapter.setFailedToLoad()",this,throwable);throw throwable;}
    }

    @Override
    public boolean isFailedToLoad() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.TileImageViewAdapter.isFailedToLoad()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.TileImageViewAdapter.isFailedToLoad()",this);return mFailedToLoad;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.TileImageViewAdapter.isFailedToLoad()",this,throwable);throw throwable;}
    }
}
