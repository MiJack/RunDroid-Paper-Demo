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

import com.android.gallery3d.common.BitmapUtils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;

import java.util.ArrayList;

public class BitmapTileProvider implements TileImageView.Model {
    private final Bitmap mBackup;
    private final Bitmap[] mMipmaps;
    private final Config mConfig;
    private final int mImageWidth;
    private final int mImageHeight;

    private boolean mRecycled = false;

    public BitmapTileProvider(Bitmap bitmap, int maxBackupSize) {
        mImageWidth = bitmap.getWidth();
        mImageHeight = bitmap.getHeight();
        ArrayList<Bitmap> list = new ArrayList<Bitmap>();
        list.add(bitmap);
        while (bitmap.getWidth() > maxBackupSize
                || bitmap.getHeight() > maxBackupSize) {
            bitmap = BitmapUtils.resizeBitmapByScale(bitmap, 0.5f, false);
            list.add(bitmap);
        }

        mBackup = list.remove(list.size() - 1);
        mMipmaps = list.toArray(new Bitmap[list.size()]);
        mConfig = Config.ARGB_8888;
    }

    public Bitmap getBackupImage() {
        com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.ui.BitmapTileProvider.getBackupImage()",this);try{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.ui.BitmapTileProvider.getBackupImage()",this);return mBackup;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.ui.BitmapTileProvider.getBackupImage()",this,throwable);throw throwable;}
    }

    public int getImageHeight() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.BitmapTileProvider.getImageHeight()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.BitmapTileProvider.getImageHeight()",this);return mImageHeight;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.BitmapTileProvider.getImageHeight()",this,throwable);throw throwable;}
    }

    public int getImageWidth() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.BitmapTileProvider.getImageWidth()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.BitmapTileProvider.getImageWidth()",this);return mImageWidth;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.BitmapTileProvider.getImageWidth()",this,throwable);throw throwable;}
    }

    public int getLevelCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.BitmapTileProvider.getLevelCount()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.BitmapTileProvider.getLevelCount()",this);return mMipmaps.length;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.BitmapTileProvider.getLevelCount()",this,throwable);throw throwable;}
    }

    public Bitmap getTile(int level, int x, int y, int tileSize) {
        com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.ui.BitmapTileProvider.getTile(int,int,int,int)",this,level,x,y,tileSize);try{Bitmap result = Bitmap.createBitmap(tileSize, tileSize, mConfig);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(mMipmaps[level], -(x >> level), -(y >> level), null);
        {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.ui.BitmapTileProvider.getTile(int,int,int,int)",this);return result;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.ui.BitmapTileProvider.getTile(int,int,int,int)",this,throwable);throw throwable;}
    }

    public void recycle() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.BitmapTileProvider.recycle()",this);try{if (mRecycled) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.BitmapTileProvider.recycle()",this);return;}}
        mRecycled = true;
        for (Bitmap bitmap : mMipmaps) {
            BitmapUtils.recycleSilently(bitmap);
        }
        BitmapUtils.recycleSilently(mBackup);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.BitmapTileProvider.recycle()",this,throwable);throw throwable;}
    }

    public int getRotation() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.ui.BitmapTileProvider.getRotation()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.ui.BitmapTileProvider.getRotation()",this);return 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.ui.BitmapTileProvider.getRotation()",this,throwable);throw throwable;}
    }

    public boolean isFailedToLoad() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.BitmapTileProvider.isFailedToLoad()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.BitmapTileProvider.isFailedToLoad()",this);return false;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.BitmapTileProvider.isFailedToLoad()",this,throwable);throw throwable;}
    }
}
