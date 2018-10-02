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

import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.common.BitmapUtils;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import com.android.gallery3d.util.UpdateHelper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/*// LocalImage represents an image in the local storage.*/
public class LocalImage extends LocalMediaItem {
    private static final int THUMBNAIL_TARGET_SIZE = 640;
    private static final int MICROTHUMBNAIL_TARGET_SIZE = 200;

    private static final String TAG = "LocalImage";

    static final Path ITEM_PATH = Path.fromString("/local/image/item");

    /*// Must preserve order between these indices and the order of the terms in*/
    /*// the following PROJECTION array.*/
    private static final int INDEX_ID = 0;
    private static final int INDEX_CAPTION = 1;
    private static final int INDEX_MIME_TYPE = 2;
    private static final int INDEX_LATITUDE = 3;
    private static final int INDEX_LONGITUDE = 4;
    private static final int INDEX_DATE_TAKEN = 5;
    private static final int INDEX_DATE_ADDED = 6;
    private static final int INDEX_DATE_MODIFIED = 7;
    private static final int INDEX_DATA = 8;
    private static final int INDEX_ORIENTATION = 9;
    private static final int INDEX_BUCKET_ID = 10;
    private static final int INDEX_SIZE_ID = 11;
    private static final int INDEX_WIDTH = 12;
    private static final int INDEX_HEIGHT = 13;

    static final String[] PROJECTION =  {
            ImageColumns._ID,           /*// 0*/
            ImageColumns.TITLE,         /*// 1*/
            ImageColumns.MIME_TYPE,     /*// 2*/
            ImageColumns.LATITUDE,      /*// 3*/
            ImageColumns.LONGITUDE,     /*// 4*/
            ImageColumns.DATE_TAKEN,    /*// 5*/
            ImageColumns.DATE_ADDED,    /*// 6*/
            ImageColumns.DATE_MODIFIED, /*// 7*/
            ImageColumns.DATA,          /*// 8*/
            ImageColumns.ORIENTATION,   /*// 9*/
            ImageColumns.BUCKET_ID,     /*// 10*/
            ImageColumns.SIZE,          /*// 11*/
            /*// These should be changed to proper names after they are made public.*/
            "width", /*// ImageColumns.WIDTH,         // 12*/
            "height", /*// ImageColumns.HEIGHT         // 13*/
    };

    private final GalleryApp mApplication;

    public int rotation;
    public int width;
    public int height;

    public LocalImage(Path path, GalleryApp application, Cursor cursor) {
        super(path, nextVersionNumber());
        mApplication = application;
        loadFromCursor(cursor);
    }

    public LocalImage(Path path, GalleryApp application, int id) {
        super(path, nextVersionNumber());
        mApplication = application;
        ContentResolver resolver = mApplication.getContentResolver();
        Uri uri = Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = LocalAlbum.getItemCursor(resolver, uri, PROJECTION, id);
        if (cursor == null) {
            throw new RuntimeException("cannot get cursor for: " + path);
        }
        try {
            if (cursor.moveToNext()) {
                loadFromCursor(cursor);
            } else {
                throw new RuntimeException("cannot find data for: " + path);
            }
        } finally {
            cursor.close();
        }
    }

    private void loadFromCursor(Cursor cursor) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.LocalImage.loadFromCursor(android.database.Cursor)",this,cursor);try{id = cursor.getInt(INDEX_ID);
        caption = cursor.getString(INDEX_CAPTION);
        mimeType = cursor.getString(INDEX_MIME_TYPE);
        latitude = cursor.getDouble(INDEX_LATITUDE);
        longitude = cursor.getDouble(INDEX_LONGITUDE);
        dateTakenInMs = cursor.getLong(INDEX_DATE_TAKEN);
        filePath = cursor.getString(INDEX_DATA);
        rotation = cursor.getInt(INDEX_ORIENTATION);
        bucketId = cursor.getInt(INDEX_BUCKET_ID);
        fileSize = cursor.getLong(INDEX_SIZE_ID);
        width = cursor.getInt(INDEX_WIDTH);
        height = cursor.getInt(INDEX_HEIGHT);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.LocalImage.loadFromCursor(android.database.Cursor)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.LocalImage.loadFromCursor(android.database.Cursor)",this,throwable);throw throwable;}
    }

    @Override
    protected boolean updateFromCursor(Cursor cursor) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.data.LocalImage.updateFromCursor(android.database.Cursor)",this,cursor);try{UpdateHelper uh = new UpdateHelper();
        id = uh.update(id, cursor.getInt(INDEX_ID));
        caption = uh.update(caption, cursor.getString(INDEX_CAPTION));
        mimeType = uh.update(mimeType, cursor.getString(INDEX_MIME_TYPE));
        latitude = uh.update(latitude, cursor.getDouble(INDEX_LATITUDE));
        longitude = uh.update(longitude, cursor.getDouble(INDEX_LONGITUDE));
        dateTakenInMs = uh.update(
                dateTakenInMs, cursor.getLong(INDEX_DATE_TAKEN));
        dateAddedInSec = uh.update(
                dateAddedInSec, cursor.getLong(INDEX_DATE_ADDED));
        dateModifiedInSec = uh.update(
                dateModifiedInSec, cursor.getLong(INDEX_DATE_MODIFIED));
        filePath = uh.update(filePath, cursor.getString(INDEX_DATA));
        rotation = uh.update(rotation, cursor.getInt(INDEX_ORIENTATION));
        bucketId = uh.update(bucketId, cursor.getInt(INDEX_BUCKET_ID));
        fileSize = uh.update(fileSize, cursor.getLong(INDEX_SIZE_ID));
        width = uh.update(width, cursor.getInt(INDEX_WIDTH));
        height = uh.update(height, cursor.getInt(INDEX_HEIGHT));
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.LocalImage.updateFromCursor(android.database.Cursor)",this);return uh.isUpdated();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.data.LocalImage.updateFromCursor(android.database.Cursor)",this,throwable);throw throwable;}
    }

    @Override
    public Job<Bitmap> requestImage(int type) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.util.ThreadPool.Job com.android.gallery3d.data.LocalImage.requestImage(int)",this,type);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.ThreadPool.Job com.android.gallery3d.data.LocalImage.requestImage(int)",this);return new LocalImageRequest(mApplication, mPath, type, filePath);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.util.ThreadPool.Job com.android.gallery3d.data.LocalImage.requestImage(int)",this,throwable);throw throwable;}
    }

    public static class LocalImageRequest extends ImageCacheRequest {
        private String mLocalFilePath;

        LocalImageRequest(GalleryApp application, Path path, int type,
                String localFilePath) {
            super(application, path, type, getTargetSize(type));
            mLocalFilePath = localFilePath;
        }

        @Override
        public Bitmap onDecodeOriginal(JobContext jc, int type) {
            com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.data.LocalImage$LocalImageRequest.onDecodeOriginal(com.android.gallery3d.util.ThreadPool.JobContext,int)",this,jc,type);try{BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            /*// try to decode from JPEG EXIF*/
            if (type == MediaItem.TYPE_MICROTHUMBNAIL) {
                ExifInterface exif = null;
                byte [] thumbData = null;
                try {
                    exif = new ExifInterface(mLocalFilePath);
                    if (exif != null) {
                        thumbData = exif.getThumbnail();
                    }
                } catch (Throwable t) {
                    Log.w(TAG, "fail to get exif thumb", t);
                }
                if (thumbData != null) {
                    Bitmap bitmap = DecodeUtils.requestDecodeIfBigEnough(
                            jc, thumbData, options, getTargetSize(type));
                    if (bitmap != null) {{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.data.LocalImage$LocalImageRequest.onDecodeOriginal(com.android.gallery3d.util.ThreadPool.JobContext,int)",this);return bitmap;}}
                }
            }
            {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.data.LocalImage$LocalImageRequest.onDecodeOriginal(com.android.gallery3d.util.ThreadPool.JobContext,int)",this);return DecodeUtils.requestDecode(
                    jc, mLocalFilePath, options, getTargetSize(type));}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.data.LocalImage$LocalImageRequest.onDecodeOriginal(com.android.gallery3d.util.ThreadPool.JobContext,int)",this,throwable);throw throwable;}
        }
    }

    static int getTargetSize(int type) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.data.LocalImage.getTargetSize(int)",type);try{switch (type) {
            case TYPE_THUMBNAIL:
                {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.data.LocalImage.getTargetSize(int)");return THUMBNAIL_TARGET_SIZE;}
            case TYPE_MICROTHUMBNAIL:
                {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.data.LocalImage.getTargetSize(int)");return MICROTHUMBNAIL_TARGET_SIZE;}
            default:
                throw new RuntimeException(
                    "should only request thumb/microthumb from cache");
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.data.LocalImage.getTargetSize(int)",throwable);throw throwable;}
    }

    @Override
    public Job<BitmapRegionDecoder> requestLargeImage() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.util.ThreadPool.Job com.android.gallery3d.data.LocalImage.requestLargeImage()",this);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.ThreadPool.Job com.android.gallery3d.data.LocalImage.requestLargeImage()",this);return new LocalLargeImageRequest(filePath);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.util.ThreadPool.Job com.android.gallery3d.data.LocalImage.requestLargeImage()",this,throwable);throw throwable;}
    }

    public static class LocalLargeImageRequest
            implements Job<BitmapRegionDecoder> {
        String mLocalFilePath;

        public LocalLargeImageRequest(String localFilePath) {
            mLocalFilePath = localFilePath;
        }

        public BitmapRegionDecoder run(JobContext jc) {
            com.mijack.Xlog.logMethodEnter("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.LocalImage$LocalLargeImageRequest.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{com.mijack.Xlog.logMethodExit("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.LocalImage$LocalLargeImageRequest.run(com.android.gallery3d.util.ThreadPool.JobContext)",this);return DecodeUtils.requestCreateBitmapRegionDecoder(
                    jc, mLocalFilePath, false);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.BitmapRegionDecoder com.android.gallery3d.data.LocalImage$LocalLargeImageRequest.run(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
        }
    }

    @Override
    public int getSupportedOperations() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.LocalImage.getSupportedOperations()",this);try{int operation = SUPPORT_DELETE | SUPPORT_SHARE | SUPPORT_CROP
                | SUPPORT_SETAS | SUPPORT_EDIT | SUPPORT_INFO;
        if (BitmapUtils.isSupportedByRegionDecoder(mimeType)) {
            operation |= SUPPORT_FULL_IMAGE;
        }

        if (BitmapUtils.isRotationSupported(mimeType)) {
            operation |= SUPPORT_ROTATE;
        }

        if (GalleryUtils.isValidLocation(latitude, longitude)) {
            operation |= SUPPORT_SHOW_ON_MAP;
        }
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.LocalImage.getSupportedOperations()",this);return operation;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.LocalImage.getSupportedOperations()",this,throwable);throw throwable;}
    }

    @Override
    public void delete() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.LocalImage.delete()",this);try{GalleryUtils.assertNotInRenderThread();
        Uri baseUri = Images.Media.EXTERNAL_CONTENT_URI;
        mApplication.getContentResolver().delete(baseUri, "_id=?",
                new String[]{String.valueOf(id)});com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.LocalImage.delete()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.LocalImage.delete()",this,throwable);throw throwable;}
    }

    private static String getExifOrientation(int orientation) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.android.gallery3d.data.LocalImage.getExifOrientation(int)",orientation);try{switch (orientation) {
            case 0:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.data.LocalImage.getExifOrientation(int)");return String.valueOf(ExifInterface.ORIENTATION_NORMAL);}
            case 90:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.data.LocalImage.getExifOrientation(int)");return String.valueOf(ExifInterface.ORIENTATION_ROTATE_90);}
            case 180:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.data.LocalImage.getExifOrientation(int)");return String.valueOf(ExifInterface.ORIENTATION_ROTATE_180);}
            case 270:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.data.LocalImage.getExifOrientation(int)");return String.valueOf(ExifInterface.ORIENTATION_ROTATE_270);}
            default:
                throw new AssertionError("invalid: " + orientation);
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.LocalImage.getExifOrientation(int)",throwable);throw throwable;}
    }

    @Override
    public void rotate(int degrees) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.LocalImage.rotate(int)",this,degrees);try{GalleryUtils.assertNotInRenderThread();
        Uri baseUri = Images.Media.EXTERNAL_CONTENT_URI;
        ContentValues values = new ContentValues();
        int rotation = (this.rotation + degrees) % 360;
        if (rotation < 0) {rotation += 360;}

        if (mimeType.equalsIgnoreCase("image/jpeg")) {
            try {
                ExifInterface exif = new ExifInterface(filePath);
                exif.setAttribute(ExifInterface.TAG_ORIENTATION,
                        getExifOrientation(rotation));
                exif.saveAttributes();
            } catch (IOException e) {
                Log.w(TAG, "cannot set exif data: " + filePath);
            }

            /*// We need to update the filesize as well*/
            fileSize = new File(filePath).length();
            values.put(Images.Media.SIZE, fileSize);
        }

        values.put(Images.Media.ORIENTATION, rotation);
        mApplication.getContentResolver().update(baseUri, values, "_id=?",
                new String[]{String.valueOf(id)});com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.LocalImage.rotate(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.LocalImage.rotate(int)",this,throwable);throw throwable;}
    }

    @Override
    public Uri getContentUri() {
        com.mijack.Xlog.logMethodEnter("android.net.Uri com.android.gallery3d.data.LocalImage.getContentUri()",this);try{Uri baseUri = Images.Media.EXTERNAL_CONTENT_URI;
        {com.mijack.Xlog.logMethodExit("android.net.Uri com.android.gallery3d.data.LocalImage.getContentUri()",this);return baseUri.buildUpon().appendPath(String.valueOf(id)).build();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.net.Uri com.android.gallery3d.data.LocalImage.getContentUri()",this,throwable);throw throwable;}
    }

    @Override
    public int getMediaType() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.LocalImage.getMediaType()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.LocalImage.getMediaType()",this);return MEDIA_TYPE_IMAGE;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.LocalImage.getMediaType()",this,throwable);throw throwable;}
    }

    @Override
    public MediaDetails getDetails() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaDetails com.android.gallery3d.data.LocalImage.getDetails()",this);try{MediaDetails details = super.getDetails();
        details.addDetail(MediaDetails.INDEX_ORIENTATION, Integer.valueOf(rotation));
        MediaDetails.extractExifInfo(details, filePath);
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaDetails com.android.gallery3d.data.LocalImage.getDetails()",this);return details;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaDetails com.android.gallery3d.data.LocalImage.getDetails()",this,throwable);throw throwable;}
    }

    @Override
    public int getRotation() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.LocalImage.getRotation()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.LocalImage.getRotation()",this);return rotation;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.LocalImage.getRotation()",this,throwable);throw throwable;}
    }

    @Override
    public int getWidth() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.LocalImage.getWidth()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.LocalImage.getWidth()",this);return width;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.LocalImage.getWidth()",this,throwable);throw throwable;}
    }

    @Override
    public int getHeight() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.LocalImage.getHeight()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.LocalImage.getHeight()",this);return height;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.LocalImage.getHeight()",this,throwable);throw throwable;}
    }
}
