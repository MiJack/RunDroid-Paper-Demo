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

package com.android.gallery3d.data;

import com.android.gallery3d.util.ThreadPool.Job;

import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;

/*// MediaItem represents an image or a video item.*/
public abstract class MediaItem extends MediaObject {
    /*// NOTE: These type numbers are stored in the image cache, so it should not*/
    /*// not be changed without resetting the cache.*/
    public static final int TYPE_THUMBNAIL = 1;
    public static final int TYPE_MICROTHUMBNAIL = 2;

    public static final int IMAGE_READY = 0;
    public static final int IMAGE_WAIT = 1;
    public static final int IMAGE_ERROR = -1;

    /*// TODO: fix default value for latlng and change this.*/
    public static final double INVALID_LATLNG = 0f;

    public abstract Job<Bitmap> requestImage(int type);
    public abstract Job<BitmapRegionDecoder> requestLargeImage();

    public MediaItem(Path path, long version) {
        super(path, version);
    }

    public long getDateInMs() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.MediaItem.getDateInMs()",this);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.MediaItem.getDateInMs()",this);return 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.MediaItem.getDateInMs()",this,throwable);throw throwable;}
    }

    public String getName() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.MediaItem.getName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.MediaItem.getName()",this);return null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.MediaItem.getName()",this,throwable);throw throwable;}
    }

    public void getLatLong(double[] latLong) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MediaItem.getLatLong([double)",this,latLong);try{latLong[0] = INVALID_LATLNG;
        latLong[1] = INVALID_LATLNG;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MediaItem.getLatLong([double)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MediaItem.getLatLong([double)",this,throwable);throw throwable;}
    }

    public String[] getTags() {
        com.mijack.Xlog.logMethodEnter("[java.lang.String com.android.gallery3d.data.MediaItem.getTags()",this);try{com.mijack.Xlog.logMethodExit("[java.lang.String com.android.gallery3d.data.MediaItem.getTags()",this);return null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[java.lang.String com.android.gallery3d.data.MediaItem.getTags()",this,throwable);throw throwable;}
    }

    public Face[] getFaces() {
        com.mijack.Xlog.logMethodEnter("[com.android.gallery3d.data.Face com.android.gallery3d.data.MediaItem.getFaces()",this);try{com.mijack.Xlog.logMethodExit("[com.android.gallery3d.data.Face com.android.gallery3d.data.MediaItem.getFaces()",this);return null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[com.android.gallery3d.data.Face com.android.gallery3d.data.MediaItem.getFaces()",this,throwable);throw throwable;}
    }

    /*// The rotation of the full-resolution image. By default, it returns the value of*/
    /*// getRotation().*/
    public int getFullImageRotation() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.MediaItem.getFullImageRotation()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MediaItem.getFullImageRotation()",this);return getRotation();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.MediaItem.getFullImageRotation()",this,throwable);throw throwable;}
    }

    public int getRotation() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.MediaItem.getRotation()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MediaItem.getRotation()",this);return 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.MediaItem.getRotation()",this,throwable);throw throwable;}
    }

    public long getSize() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.MediaItem.getSize()",this);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.MediaItem.getSize()",this);return 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.MediaItem.getSize()",this,throwable);throw throwable;}
    }

    public abstract String getMimeType();

    /*// Returns width and height of the media item.*/
    /*// Returns 0, 0 if the information is not available.*/
    public abstract int getWidth();
    public abstract int getHeight();
}
