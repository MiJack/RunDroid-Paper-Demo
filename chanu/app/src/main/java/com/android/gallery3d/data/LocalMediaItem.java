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

import com.android.gallery3d.util.GalleryUtils;

import android.database.Cursor;

import java.text.DateFormat;
import java.util.Date;

/*//*/
/*// LocalMediaItem is an abstract class captures those common fields*/
/*// in LocalImage and LocalVideo.*/
/*//*/
public abstract class LocalMediaItem extends MediaItem {

    @SuppressWarnings("unused")
    private static final String TAG = "LocalMediaItem";

    /*// database fields*/
    public int id;
    public String caption;
    public String mimeType;
    public long fileSize;
    public double latitude = INVALID_LATLNG;
    public double longitude = INVALID_LATLNG;
    public long dateTakenInMs;
    public long dateAddedInSec;
    public long dateModifiedInSec;
    public String filePath;
    public int bucketId;

    public LocalMediaItem(Path path, long version) {
        super(path, version);
    }

    @Override
    public long getDateInMs() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.LocalMediaItem.getDateInMs()",this);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.LocalMediaItem.getDateInMs()",this);return dateTakenInMs;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.LocalMediaItem.getDateInMs()",this,throwable);throw throwable;}
    }

    @Override
    public String getName() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.LocalMediaItem.getName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.LocalMediaItem.getName()",this);return caption;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.LocalMediaItem.getName()",this,throwable);throw throwable;}
    }

    @Override
    public void getLatLong(double[] latLong) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.LocalMediaItem.getLatLong([double)",this,latLong);try{latLong[0] = latitude;
        latLong[1] = longitude;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.LocalMediaItem.getLatLong([double)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.LocalMediaItem.getLatLong([double)",this,throwable);throw throwable;}
    }

    abstract protected boolean updateFromCursor(Cursor cursor);

    public int getBucketId() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.LocalMediaItem.getBucketId()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.LocalMediaItem.getBucketId()",this);return bucketId;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.LocalMediaItem.getBucketId()",this,throwable);throw throwable;}
    }

    protected void updateContent(Cursor cursor) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.LocalMediaItem.updateContent(android.database.Cursor)",this,cursor);try{if (updateFromCursor(cursor)) {
            mDataVersion = nextVersionNumber();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.LocalMediaItem.updateContent(android.database.Cursor)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.LocalMediaItem.updateContent(android.database.Cursor)",this,throwable);throw throwable;}
    }

    @Override
    public MediaDetails getDetails() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaDetails com.android.gallery3d.data.LocalMediaItem.getDetails()",this);try{MediaDetails details = super.getDetails();
        details.addDetail(MediaDetails.INDEX_PATH, filePath);
        details.addDetail(MediaDetails.INDEX_TITLE, caption);
        DateFormat formater = DateFormat.getDateTimeInstance();
        details.addDetail(MediaDetails.INDEX_DATETIME, formater.format(new Date(dateTakenInMs)));

        if (GalleryUtils.isValidLocation(latitude, longitude)) {
            details.addDetail(MediaDetails.INDEX_LOCATION, new double[] {latitude, longitude});
        }
        if (fileSize > 0) {details.addDetail(MediaDetails.INDEX_SIZE, fileSize);}
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaDetails com.android.gallery3d.data.LocalMediaItem.getDetails()",this);return details;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaDetails com.android.gallery3d.data.LocalMediaItem.getDetails()",this,throwable);throw throwable;}
    }

    @Override
    public String getMimeType() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.LocalMediaItem.getMimeType()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.LocalMediaItem.getMimeType()",this);return mimeType;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.LocalMediaItem.getMimeType()",this,throwable);throw throwable;}
    }

    public long getSize() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.LocalMediaItem.getSize()",this);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.LocalMediaItem.getSize()",this);return fileSize;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.LocalMediaItem.getSize()",this,throwable);throw throwable;}
    }
}
