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

package com.android.gallery3d.picasasource;

import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.MediaSource;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.data.PathMatcher;

import android.app.Activity;
import android.content.Context;
import android.media.ExifInterface;
import android.os.ParcelFileDescriptor;

import java.io.FileNotFoundException;

public class PicasaSource extends MediaSource {
    private static final String TAG = "PicasaSource";

    private static final int NO_MATCH = -1;
    private static final int IMAGE_MEDIA_ID = 1;

    private static final int PICASA_ALBUMSET = 0;
    private static final int MAP_BATCH_COUNT = 100;

    private GalleryApp mApplication;
    private PathMatcher mMatcher;

    public static final Path ALBUM_PATH = Path.fromString("/picasa/all");

    public PicasaSource(GalleryApp application) {
        super("picasa");
        mApplication = application;
        mMatcher = new PathMatcher();
        mMatcher.add("/picasa/all", PICASA_ALBUMSET);
        mMatcher.add("/picasa/image", PICASA_ALBUMSET);
        mMatcher.add("/picasa/video", PICASA_ALBUMSET);
    }

    private static class EmptyAlbumSet extends MediaSet {

        public EmptyAlbumSet(Path path, long version) {
            super(path, version);
        }

        @Override
        public String getName() {
            com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.picasasource.PicasaSource$EmptyAlbumSet.getName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.picasasource.PicasaSource$EmptyAlbumSet.getName()",this);return "picasa";}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.picasasource.PicasaSource$EmptyAlbumSet.getName()",this,throwable);throw throwable;}
        }

        @Override
        public long reload() {
            com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.picasasource.PicasaSource$EmptyAlbumSet.reload()",this);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.picasasource.PicasaSource$EmptyAlbumSet.reload()",this);return mDataVersion;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.picasasource.PicasaSource$EmptyAlbumSet.reload()",this,throwable);throw throwable;}
        }
    }

    @Override
    public MediaObject createMediaObject(Path path) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaObject com.android.gallery3d.picasasource.PicasaSource.createMediaObject(com.android.gallery3d.data.Path)",this,path);try{switch (mMatcher.match(path)) {
            case PICASA_ALBUMSET:
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaObject com.android.gallery3d.picasasource.PicasaSource.createMediaObject(com.android.gallery3d.data.Path)",this);return new EmptyAlbumSet(path, MediaObject.nextVersionNumber());}
            default:
                throw new RuntimeException("bad path: " + path);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaObject com.android.gallery3d.picasasource.PicasaSource.createMediaObject(com.android.gallery3d.data.Path)",this,throwable);throw throwable;}
    }

    public static boolean isPicasaImage(MediaObject object) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.android.gallery3d.picasasource.PicasaSource.isPicasaImage(com.android.gallery3d.data.MediaObject)",object);try{com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.picasasource.PicasaSource.isPicasaImage(com.android.gallery3d.data.MediaObject)");return false;}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.android.gallery3d.picasasource.PicasaSource.isPicasaImage(com.android.gallery3d.data.MediaObject)",throwable);throw throwable;}
    }

    public static String getImageTitle(MediaObject image) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.android.gallery3d.picasasource.PicasaSource.getImageTitle(com.android.gallery3d.data.MediaObject)",image);try{com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.picasasource.PicasaSource.getImageTitle(com.android.gallery3d.data.MediaObject)");throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.android.gallery3d.picasasource.PicasaSource.getImageTitle(com.android.gallery3d.data.MediaObject)",throwable);throw throwable;}
    }

    public static int getImageSize(MediaObject image) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.picasasource.PicasaSource.getImageSize(com.android.gallery3d.data.MediaObject)",image);try{com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.picasasource.PicasaSource.getImageSize(com.android.gallery3d.data.MediaObject)");throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.picasasource.PicasaSource.getImageSize(com.android.gallery3d.data.MediaObject)",throwable);throw throwable;}
    }

    public static String getContentType(MediaObject image) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.android.gallery3d.picasasource.PicasaSource.getContentType(com.android.gallery3d.data.MediaObject)",image);try{com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.picasasource.PicasaSource.getContentType(com.android.gallery3d.data.MediaObject)");throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.android.gallery3d.picasasource.PicasaSource.getContentType(com.android.gallery3d.data.MediaObject)",throwable);throw throwable;}
    }

    public static long getDateTaken(MediaObject image) {
        com.mijack.Xlog.logStaticMethodEnter("long com.android.gallery3d.picasasource.PicasaSource.getDateTaken(com.android.gallery3d.data.MediaObject)",image);try{com.mijack.Xlog.logStaticMethodExit("long com.android.gallery3d.picasasource.PicasaSource.getDateTaken(com.android.gallery3d.data.MediaObject)");throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("long com.android.gallery3d.picasasource.PicasaSource.getDateTaken(com.android.gallery3d.data.MediaObject)",throwable);throw throwable;}
    }

    public static double getLatitude(MediaObject image) {
        com.mijack.Xlog.logStaticMethodEnter("double com.android.gallery3d.picasasource.PicasaSource.getLatitude(com.android.gallery3d.data.MediaObject)",image);try{com.mijack.Xlog.logStaticMethodExit("double com.android.gallery3d.picasasource.PicasaSource.getLatitude(com.android.gallery3d.data.MediaObject)");throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("double com.android.gallery3d.picasasource.PicasaSource.getLatitude(com.android.gallery3d.data.MediaObject)",throwable);throw throwable;}
    }

    public static double getLongitude(MediaObject image) {
        com.mijack.Xlog.logStaticMethodEnter("double com.android.gallery3d.picasasource.PicasaSource.getLongitude(com.android.gallery3d.data.MediaObject)",image);try{com.mijack.Xlog.logStaticMethodExit("double com.android.gallery3d.picasasource.PicasaSource.getLongitude(com.android.gallery3d.data.MediaObject)");throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("double com.android.gallery3d.picasasource.PicasaSource.getLongitude(com.android.gallery3d.data.MediaObject)",throwable);throw throwable;}
    }

    public static int getRotation(MediaObject image) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.picasasource.PicasaSource.getRotation(com.android.gallery3d.data.MediaObject)",image);try{com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.picasasource.PicasaSource.getRotation(com.android.gallery3d.data.MediaObject)");throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.picasasource.PicasaSource.getRotation(com.android.gallery3d.data.MediaObject)",throwable);throw throwable;}
    }

    public static ParcelFileDescriptor openFile(Context context, MediaObject image, String mode)
            throws FileNotFoundException {
        com.mijack.Xlog.logStaticMethodEnter("android.os.ParcelFileDescriptor com.android.gallery3d.picasasource.PicasaSource.openFile(android.content.Context,com.android.gallery3d.data.MediaObject,java.lang.String)",context,image,mode);try{com.mijack.Xlog.logStaticMethodExit("android.os.ParcelFileDescriptor com.android.gallery3d.picasasource.PicasaSource.openFile(android.content.Context,com.android.gallery3d.data.MediaObject,java.lang.String)");throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.os.ParcelFileDescriptor com.android.gallery3d.picasasource.PicasaSource.openFile(android.content.Context,com.android.gallery3d.data.MediaObject,java.lang.String)",throwable);throw throwable;}
    }

    public static void initialize(Context context) {com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.picasasource.PicasaSource.initialize(android.content.Context)",context);try{/*do nothing*/com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.picasasource.PicasaSource.initialize(android.content.Context)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.picasasource.PicasaSource.initialize(android.content.Context)",throwable);throw throwable;}}

    public static void requestSync(Context context) {com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.picasasource.PicasaSource.requestSync(android.content.Context)",context);try{/*do nothing*/com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.picasasource.PicasaSource.requestSync(android.content.Context)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.picasasource.PicasaSource.requestSync(android.content.Context)",throwable);throw throwable;}}

    public static void showSignInReminder(Activity context) {com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.picasasource.PicasaSource.showSignInReminder(android.app.Activity)",context);try{/*do nothing*/com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.picasasource.PicasaSource.showSignInReminder(android.app.Activity)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.picasasource.PicasaSource.showSignInReminder(android.app.Activity)",throwable);throw throwable;}}

    public static void onPackageAdded(Context context, String packageName) {com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.picasasource.PicasaSource.onPackageAdded(android.content.Context,java.lang.String)",context,packageName);try{/*do nothing*/com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.picasasource.PicasaSource.onPackageAdded(android.content.Context,java.lang.String)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.picasasource.PicasaSource.onPackageAdded(android.content.Context,java.lang.String)",throwable);throw throwable;}}

    public static void onPackageRemoved(Context context, String packageName) {com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.picasasource.PicasaSource.onPackageRemoved(android.content.Context,java.lang.String)",context,packageName);try{/*do nothing*/com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.picasasource.PicasaSource.onPackageRemoved(android.content.Context,java.lang.String)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.picasasource.PicasaSource.onPackageRemoved(android.content.Context,java.lang.String)",throwable);throw throwable;}}

    public static void extractExifValues(MediaObject item, ExifInterface exif) {com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.picasasource.PicasaSource.extractExifValues(com.android.gallery3d.data.MediaObject,android.media.ExifInterface)",item,exif);try{/*do nothing*/com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.picasasource.PicasaSource.extractExifValues(com.android.gallery3d.data.MediaObject,android.media.ExifInterface)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.picasasource.PicasaSource.extractExifValues(com.android.gallery3d.data.MediaObject,android.media.ExifInterface)",throwable);throw throwable;}}
}
