/*
 * Copyright (C) 2009 The Android Open Source Project
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

package com.android.gallery3d.provider;

import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.DownloadCache;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.MtpImage;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.picasasource.PicasaSource;
import com.android.gallery3d.util.GalleryUtils;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class GalleryProvider extends ContentProvider {
    private static final String TAG = "GalleryProvider";

    public static final String AUTHORITY = "com.android.gallery3d.provider";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    private static final String[] SUPPORTED_PICASA_COLUMNS = {
            ImageColumns.DISPLAY_NAME,
            ImageColumns.SIZE,
            ImageColumns.MIME_TYPE,
            ImageColumns.DATE_TAKEN,
            ImageColumns.LATITUDE,
            ImageColumns.LONGITUDE,
            ImageColumns.ORIENTATION};

    private DataManager mDataManager;
    private DownloadCache mDownloadCache;
    private static Uri sBaseUri;

    public static String getAuthority(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.android.gallery3d.provider.GalleryProvider.getAuthority(android.content.Context)",context);try{com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.provider.GalleryProvider.getAuthority(android.content.Context)");return context.getPackageName() + ".provider";}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.android.gallery3d.provider.GalleryProvider.getAuthority(android.content.Context)",throwable);throw throwable;}
    }

    public static Uri getUriFor(Context context, Path path) {
        com.mijack.Xlog.logStaticMethodEnter("android.net.Uri com.android.gallery3d.provider.GalleryProvider.getUriFor(android.content.Context,com.android.gallery3d.data.Path)",context,path);try{if (sBaseUri == null) {
            sBaseUri = Uri.parse("content://" + context.getPackageName() + ".provider");
        }
        {com.mijack.Xlog.logStaticMethodExit("android.net.Uri com.android.gallery3d.provider.GalleryProvider.getUriFor(android.content.Context,com.android.gallery3d.data.Path)");return sBaseUri.buildUpon()
                .appendEncodedPath(path.toString().substring(1)) /*// ignore the leading '/'*/
                .build();}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.net.Uri com.android.gallery3d.provider.GalleryProvider.getUriFor(android.content.Context,com.android.gallery3d.data.Path)",throwable);throw throwable;}
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.provider.GalleryProvider.delete(android.net.Uri,java.lang.String,[java.lang.String)",this,uri,selection,selectionArgs);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.provider.GalleryProvider.delete(android.net.Uri,java.lang.String,[java.lang.String)",this);throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.provider.GalleryProvider.delete(android.net.Uri,java.lang.String,[java.lang.String)",this,throwable);throw throwable;}
    }

    /*// TODO: consider concurrent access*/
    @Override
    public String getType(Uri uri) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.provider.GalleryProvider.getType(android.net.Uri)",this,uri);try{long token = Binder.clearCallingIdentity();
        try {
            Path path = Path.fromString(uri.getPath());
            MediaItem item = (MediaItem) mDataManager.getMediaObject(path);
            {com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.provider.GalleryProvider.getType(android.net.Uri)",this);return item != null ? item.getMimeType() : null;}
        } finally {
            Binder.restoreCallingIdentity(token);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.provider.GalleryProvider.getType(android.net.Uri)",this,throwable);throw throwable;}
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        com.mijack.Xlog.logMethodEnter("android.net.Uri com.android.gallery3d.provider.GalleryProvider.insert(android.net.Uri,android.content.ContentValues)",this,uri,values);try{com.mijack.Xlog.logMethodExit("android.net.Uri com.android.gallery3d.provider.GalleryProvider.insert(android.net.Uri,android.content.ContentValues)",this);throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.net.Uri com.android.gallery3d.provider.GalleryProvider.insert(android.net.Uri,android.content.ContentValues)",this,throwable);throw throwable;}
    }

    @Override
    public boolean onCreate() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.provider.GalleryProvider.onCreate()",this);try{GalleryApp app = (GalleryApp) getContext().getApplicationContext();
        mDataManager = app.getDataManager();
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.provider.GalleryProvider.onCreate()",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.provider.GalleryProvider.onCreate()",this,throwable);throw throwable;}
    }

    private DownloadCache getDownloadCache() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.DownloadCache com.android.gallery3d.provider.GalleryProvider.getDownloadCache()",this);try{if (mDownloadCache == null) {
            GalleryApp app = (GalleryApp) getContext().getApplicationContext();
            mDownloadCache = app.getDownloadCache();
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.DownloadCache com.android.gallery3d.provider.GalleryProvider.getDownloadCache()",this);return mDownloadCache;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.DownloadCache com.android.gallery3d.provider.GalleryProvider.getDownloadCache()",this,throwable);throw throwable;}
    }

    /*// TODO: consider concurrent access*/
    @Override
    public Cursor query(Uri uri, String[] projection,
            String selection, String[] selectionArgs, String sortOrder) {
        com.mijack.Xlog.logMethodEnter("android.database.Cursor com.android.gallery3d.provider.GalleryProvider.query(android.net.Uri,[java.lang.String,java.lang.String,[java.lang.String,java.lang.String)",this,uri,projection,selection,selectionArgs,sortOrder);try{long token = Binder.clearCallingIdentity();
        try {
            Path path = Path.fromString(uri.getPath());
            MediaObject object = mDataManager.getMediaObject(path);
            if (object == null) {
                Log.w(TAG, "cannot find: " + uri);
                {com.mijack.Xlog.logMethodExit("android.database.Cursor com.android.gallery3d.provider.GalleryProvider.query(android.net.Uri,[java.lang.String,java.lang.String,[java.lang.String,java.lang.String)",this);return null;}
            }
            if (PicasaSource.isPicasaImage(object)) {
                {com.mijack.Xlog.logMethodExit("android.database.Cursor com.android.gallery3d.provider.GalleryProvider.query(android.net.Uri,[java.lang.String,java.lang.String,[java.lang.String,java.lang.String)",this);return queryPicasaItem(object,
                        projection, selection, selectionArgs, sortOrder);}
            } else if (object instanceof MtpImage) {
                {com.mijack.Xlog.logMethodExit("android.database.Cursor com.android.gallery3d.provider.GalleryProvider.query(android.net.Uri,[java.lang.String,java.lang.String,[java.lang.String,java.lang.String)",this);return queryMtpItem((MtpImage) object,
                        projection, selection, selectionArgs, sortOrder);}
            } else {
                    {com.mijack.Xlog.logMethodExit("android.database.Cursor com.android.gallery3d.provider.GalleryProvider.query(android.net.Uri,[java.lang.String,java.lang.String,[java.lang.String,java.lang.String)",this);return null;}
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.database.Cursor com.android.gallery3d.provider.GalleryProvider.query(android.net.Uri,[java.lang.String,java.lang.String,[java.lang.String,java.lang.String)",this,throwable);throw throwable;}
    }

    private Cursor queryMtpItem(MtpImage image, String[] projection,
            String selection, String[] selectionArgs, String sortOrder) {
        com.mijack.Xlog.logMethodEnter("android.database.Cursor com.android.gallery3d.provider.GalleryProvider.queryMtpItem(com.android.gallery3d.data.MtpImage,[java.lang.String,java.lang.String,[java.lang.String,java.lang.String)",this,image,projection,selection,selectionArgs,sortOrder);try{Object[] columnValues = new Object[projection.length];
        for (int i = 0, n = projection.length; i < n; ++i) {
            String column = projection[i];
            if (ImageColumns.DISPLAY_NAME.equals(column)) {
                columnValues[i] = image.getName();
            } else if (ImageColumns.SIZE.equals(column)){
                columnValues[i] = image.getSize();
            } else if (ImageColumns.MIME_TYPE.equals(column)) {
                columnValues[i] = image.getMimeType();
            } else if (ImageColumns.DATE_TAKEN.equals(column)) {
                columnValues[i] = image.getDateInMs();
            } else {
                Log.w(TAG, "unsupported column: " + column);
            }
        }
        MatrixCursor cursor = new MatrixCursor(projection);
        cursor.addRow(columnValues);
        {com.mijack.Xlog.logMethodExit("android.database.Cursor com.android.gallery3d.provider.GalleryProvider.queryMtpItem(com.android.gallery3d.data.MtpImage,[java.lang.String,java.lang.String,[java.lang.String,java.lang.String)",this);return cursor;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.database.Cursor com.android.gallery3d.provider.GalleryProvider.queryMtpItem(com.android.gallery3d.data.MtpImage,[java.lang.String,java.lang.String,[java.lang.String,java.lang.String)",this,throwable);throw throwable;}
    }

    private Cursor queryPicasaItem(MediaObject image, String[] projection,
            String selection, String[] selectionArgs, String sortOrder) {
        com.mijack.Xlog.logMethodEnter("android.database.Cursor com.android.gallery3d.provider.GalleryProvider.queryPicasaItem(com.android.gallery3d.data.MediaObject,[java.lang.String,java.lang.String,[java.lang.String,java.lang.String)",this,image,projection,selection,selectionArgs,sortOrder);try{if (projection == null) {projection = SUPPORTED_PICASA_COLUMNS;}
        Object[] columnValues = new Object[projection.length];
        double latitude = PicasaSource.getLatitude(image);
        double longitude = PicasaSource.getLongitude(image);
        boolean isValidLatlong = GalleryUtils.isValidLocation(latitude, longitude);

        for (int i = 0, n = projection.length; i < n; ++i) {
            String column = projection[i];
            if (ImageColumns.DISPLAY_NAME.equals(column)) {
                columnValues[i] = PicasaSource.getImageTitle(image);
            } else if (ImageColumns.SIZE.equals(column)){
                columnValues[i] = PicasaSource.getImageSize(image);
            } else if (ImageColumns.MIME_TYPE.equals(column)) {
                columnValues[i] = PicasaSource.getContentType(image);
            } else if (ImageColumns.DATE_TAKEN.equals(column)) {
                columnValues[i] = PicasaSource.getDateTaken(image);
            } else if (ImageColumns.LATITUDE.equals(column)) {
                columnValues[i] = isValidLatlong ? latitude : null;
            } else if (ImageColumns.LONGITUDE.equals(column)) {
                columnValues[i] = isValidLatlong ? longitude : null;
            } else if (ImageColumns.ORIENTATION.equals(column)) {
                columnValues[i] = PicasaSource.getRotation(image);
            } else {
                Log.w(TAG, "unsupported column: " + column);
            }
        }
        MatrixCursor cursor = new MatrixCursor(projection);
        cursor.addRow(columnValues);
        {com.mijack.Xlog.logMethodExit("android.database.Cursor com.android.gallery3d.provider.GalleryProvider.queryPicasaItem(com.android.gallery3d.data.MediaObject,[java.lang.String,java.lang.String,[java.lang.String,java.lang.String)",this);return cursor;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.database.Cursor com.android.gallery3d.provider.GalleryProvider.queryPicasaItem(com.android.gallery3d.data.MediaObject,[java.lang.String,java.lang.String,[java.lang.String,java.lang.String)",this,throwable);throw throwable;}
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
            throws FileNotFoundException {
        com.mijack.Xlog.logMethodEnter("android.os.ParcelFileDescriptor com.android.gallery3d.provider.GalleryProvider.openFile(android.net.Uri,java.lang.String)",this,uri,mode);try{long token = Binder.clearCallingIdentity();
        try {
            if (mode.contains("w")) {
                throw new FileNotFoundException("cannot open file for write");
            }
            Path path = Path.fromString(uri.getPath());
            MediaObject object = mDataManager.getMediaObject(path);
            if (object == null) {
                throw new FileNotFoundException(uri.toString());
            }
            if (PicasaSource.isPicasaImage(object)) {
                {com.mijack.Xlog.logMethodExit("android.os.ParcelFileDescriptor com.android.gallery3d.provider.GalleryProvider.openFile(android.net.Uri,java.lang.String)",this);return PicasaSource.openFile(getContext(), object, mode);}
            } else if (object instanceof MtpImage) {
                {com.mijack.Xlog.logMethodExit("android.os.ParcelFileDescriptor com.android.gallery3d.provider.GalleryProvider.openFile(android.net.Uri,java.lang.String)",this);return openPipeHelper(uri, null, null, null,
                        new MtpPipeDataWriter((MtpImage) object));}
            } else {
                throw new FileNotFoundException("unspported type: " + object);
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.os.ParcelFileDescriptor com.android.gallery3d.provider.GalleryProvider.openFile(android.net.Uri,java.lang.String)",this,throwable);throw throwable;}
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.provider.GalleryProvider.update(android.net.Uri,android.content.ContentValues,java.lang.String,[java.lang.String)",this,uri,values,selection,selectionArgs);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.provider.GalleryProvider.update(android.net.Uri,android.content.ContentValues,java.lang.String,[java.lang.String)",this);throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.provider.GalleryProvider.update(android.net.Uri,android.content.ContentValues,java.lang.String,[java.lang.String)",this,throwable);throw throwable;}
    }

    private final class MtpPipeDataWriter implements PipeDataWriter<Object> {
        private final MtpImage mImage;

        private MtpPipeDataWriter(MtpImage image) {
            mImage = image;
        }

        @Override
        public void writeDataToPipe(ParcelFileDescriptor output,
                Uri uri, String mimeType, Bundle opts, Object args) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.provider.GalleryProvider$MtpPipeDataWriter.writeDataToPipe(android.os.ParcelFileDescriptor,android.net.Uri,java.lang.String,android.os.Bundle,com.android.gallery3d.data.MediaObject)",this,output,uri,mimeType,opts,args);try{OutputStream os = null;
            try {
                os = new ParcelFileDescriptor.AutoCloseOutputStream(output);
                os.write(mImage.getImageData());
            } catch (IOException e) {
                Log.w(TAG, "fail to download: " + uri, e);
            } finally {
                Utils.closeSilently(os);
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.provider.GalleryProvider$MtpPipeDataWriter.writeDataToPipe(android.os.ParcelFileDescriptor,android.net.Uri,java.lang.String,android.os.Bundle,com.android.gallery3d.data.MediaObject)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.provider.GalleryProvider$MtpPipeDataWriter.writeDataToPipe(android.os.ParcelFileDescriptor,android.net.Uri,java.lang.String,android.os.Bundle,com.android.gallery3d.data.MediaObject)",this,throwable);throw throwable;}
        }
    }
}
