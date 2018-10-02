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

import android.net.Uri;

public abstract class MediaObject {
    @SuppressWarnings("unused")
    private static final String TAG = "MediaObject";
    public static final long INVALID_DATA_VERSION = -1;

    /*// These are the bits returned from getSupportedOperations():*/
    public static final int SUPPORT_DELETE = 1 << 0;
    public static final int SUPPORT_ROTATE = 1 << 1;
    public static final int SUPPORT_SHARE = 1 << 2;
    public static final int SUPPORT_CROP = 1 << 3;
    public static final int SUPPORT_SHOW_ON_MAP = 1 << 4;
    public static final int SUPPORT_SETAS = 1 << 5;
    public static final int SUPPORT_FULL_IMAGE = 1 << 6;
    public static final int SUPPORT_PLAY = 1 << 7;
    public static final int SUPPORT_CACHE = 1 << 8;
    public static final int SUPPORT_EDIT = 1 << 9;
    public static final int SUPPORT_INFO = 1 << 10;
    public static final int SUPPORT_IMPORT = 1 << 11;
    public static final int SUPPORT_ANIMATED_GIF = 1 << 12;
    public static final int SUPPORT_ALL = 0xffffffff;

    /*// These are the bits returned from getMediaType():*/
    public static final int MEDIA_TYPE_UNKNOWN = 1;
    public static final int MEDIA_TYPE_IMAGE = 2;
    public static final int MEDIA_TYPE_VIDEO = 4;
    public static final int MEDIA_TYPE_ALL = MEDIA_TYPE_IMAGE | MEDIA_TYPE_VIDEO;

    /*// These are flags for cache() and return values for getCacheFlag():*/
    public static final int CACHE_FLAG_NO = 0;
    public static final int CACHE_FLAG_SCREENNAIL = 1;
    public static final int CACHE_FLAG_FULL = 2;

    /*// These are return values for getCacheStatus():*/
    public static final int CACHE_STATUS_NOT_CACHED = 0;
    public static final int CACHE_STATUS_CACHING = 1;
    public static final int CACHE_STATUS_CACHED_SCREENNAIL = 2;
    public static final int CACHE_STATUS_CACHED_FULL = 3;

    private static long sVersionSerial = 0;

    protected long mDataVersion;

    protected final Path mPath;

    public MediaObject(Path path, long version) {
        path.setObject(this);
        mPath = path;
        mDataVersion = version;
    }

    public Path getPath() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.Path com.android.gallery3d.data.MediaObject.getPath()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.Path com.android.gallery3d.data.MediaObject.getPath()",this);return mPath;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.Path com.android.gallery3d.data.MediaObject.getPath()",this,throwable);throw throwable;}
    }

    public int getSupportedOperations() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.MediaObject.getSupportedOperations()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MediaObject.getSupportedOperations()",this);return 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.MediaObject.getSupportedOperations()",this,throwable);throw throwable;}
    }

    public void delete() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MediaObject.delete()",this);try{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MediaObject.delete()",this);throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MediaObject.delete()",this,throwable);throw throwable;}
    }

    public void rotate(int degrees) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MediaObject.rotate(int)",this,degrees);try{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MediaObject.rotate(int)",this);throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MediaObject.rotate(int)",this,throwable);throw throwable;}
    }

    public Uri getContentUri() {
        com.mijack.Xlog.logMethodEnter("android.net.Uri com.android.gallery3d.data.MediaObject.getContentUri()",this);try{com.mijack.Xlog.logMethodExit("android.net.Uri com.android.gallery3d.data.MediaObject.getContentUri()",this);throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.net.Uri com.android.gallery3d.data.MediaObject.getContentUri()",this,throwable);throw throwable;}
    }

    public Uri getPlayUri() {
        com.mijack.Xlog.logMethodEnter("android.net.Uri com.android.gallery3d.data.MediaObject.getPlayUri()",this);try{com.mijack.Xlog.logMethodExit("android.net.Uri com.android.gallery3d.data.MediaObject.getPlayUri()",this);throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.net.Uri com.android.gallery3d.data.MediaObject.getPlayUri()",this,throwable);throw throwable;}
    }

    public int getMediaType() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.MediaObject.getMediaType()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MediaObject.getMediaType()",this);return MEDIA_TYPE_UNKNOWN;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.MediaObject.getMediaType()",this,throwable);throw throwable;}
    }

    public boolean Import() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.data.MediaObject.Import()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.MediaObject.Import()",this);throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.data.MediaObject.Import()",this,throwable);throw throwable;}
    }

    public MediaDetails getDetails() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaDetails com.android.gallery3d.data.MediaObject.getDetails()",this);try{MediaDetails details = new MediaDetails();
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaDetails com.android.gallery3d.data.MediaObject.getDetails()",this);return details;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaDetails com.android.gallery3d.data.MediaObject.getDetails()",this,throwable);throw throwable;}
    }

    public long getDataVersion() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.MediaObject.getDataVersion()",this);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.MediaObject.getDataVersion()",this);return mDataVersion;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.MediaObject.getDataVersion()",this,throwable);throw throwable;}
    }

    public int getCacheFlag() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.MediaObject.getCacheFlag()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MediaObject.getCacheFlag()",this);return CACHE_FLAG_NO;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.MediaObject.getCacheFlag()",this,throwable);throw throwable;}
    }

    public int getCacheStatus() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.MediaObject.getCacheStatus()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.MediaObject.getCacheStatus()",this);throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.MediaObject.getCacheStatus()",this,throwable);throw throwable;}
    }

    public long getCacheSize() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.MediaObject.getCacheSize()",this);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.MediaObject.getCacheSize()",this);throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.MediaObject.getCacheSize()",this,throwable);throw throwable;}
    }

    public void cache(int flag) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.MediaObject.cache(int)",this,flag);try{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.MediaObject.cache(int)",this);throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.MediaObject.cache(int)",this,throwable);throw throwable;}
    }

    public static synchronized long nextVersionNumber() {
        com.mijack.Xlog.logStaticMethodEnter("long com.android.gallery3d.data.MediaObject.nextVersionNumber()");try{com.mijack.Xlog.logStaticMethodExit("long com.android.gallery3d.data.MediaObject.nextVersionNumber()");return ++MediaObject.sVersionSerial;}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("long com.android.gallery3d.data.MediaObject.nextVersionNumber()",throwable);throw throwable;}
    }
}
