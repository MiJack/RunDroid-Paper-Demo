/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.android.gallery3d.gadget;

import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.ContentListener;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.util.GalleryUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Images.Media;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class LocalPhotoSource implements WidgetSource {

    private static final String TAG = "LocalPhotoSource";

    private static final int MAX_PHOTO_COUNT = 128;

    /* Static fields used to query for the correct set of images */
    private static final Uri CONTENT_URI = Media.EXTERNAL_CONTENT_URI;
    private static final String DATE_TAKEN = Media.DATE_TAKEN;
    private static final String[] PROJECTION = {Media._ID};
    private static final String[] COUNT_PROJECTION = {"count(*)"};
    /* We don't want to include the download directory */
    private static final String SELECTION =
            String.format("%s != %s", Media.BUCKET_ID, getDownloadBucketId());
    private static final String ORDER = String.format("%s DESC", DATE_TAKEN);

    private Context mContext;
    private ArrayList<Long> mPhotos = new ArrayList<Long>();
    private ContentListener mContentListener;
    private ContentObserver mContentObserver;
    private boolean mContentDirty = true;
    private DataManager mDataManager;
    private static final Path LOCAL_IMAGE_ROOT = Path.fromString("/local/image/item");

    public LocalPhotoSource(Context context) {
        mContext = context;
        mDataManager = ((GalleryApp) context.getApplicationContext()).getDataManager();
        mContentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.gadget.LocalPhotoSource$1.onChange(boolean)",this,selfChange);try{mContentDirty = true;
                if (mContentListener != null) {mContentListener.onContentDirty();}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.LocalPhotoSource$1.onChange(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.gadget.LocalPhotoSource$1.onChange(boolean)",this,throwable);throw throwable;}
            }
        };
        mContext.getContentResolver()
                .registerContentObserver(CONTENT_URI, true, mContentObserver);
    }

    public void close() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.gadget.LocalPhotoSource.close()",this);try{mContext.getContentResolver().unregisterContentObserver(mContentObserver);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.LocalPhotoSource.close()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.gadget.LocalPhotoSource.close()",this,throwable);throw throwable;}
    }

    @Override
    public Uri getContentUri(int index) {
        com.mijack.Xlog.logMethodEnter("android.net.Uri com.android.gallery3d.gadget.LocalPhotoSource.getContentUri(int)",this,index);try{if (index < mPhotos.size()) {
            {com.mijack.Xlog.logMethodExit("android.net.Uri com.android.gallery3d.gadget.LocalPhotoSource.getContentUri(int)",this);return CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(mPhotos.get(index)))
                    .build();}
        }
        {com.mijack.Xlog.logMethodExit("android.net.Uri com.android.gallery3d.gadget.LocalPhotoSource.getContentUri(int)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.net.Uri com.android.gallery3d.gadget.LocalPhotoSource.getContentUri(int)",this,throwable);throw throwable;}
    }

    @Override
    public Bitmap getImage(int index) {
        com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.gadget.LocalPhotoSource.getImage(int)",this,index);try{if (index >= mPhotos.size()) {{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.gadget.LocalPhotoSource.getImage(int)",this);return null;}}
        long id = mPhotos.get(index);
        MediaItem image = (MediaItem)
                mDataManager.getMediaObject(LOCAL_IMAGE_ROOT.getChild(id));
        if (image == null) {{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.gadget.LocalPhotoSource.getImage(int)",this);return null;}}

        {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.gadget.LocalPhotoSource.getImage(int)",this);return WidgetUtils.createWidgetBitmap(image);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.gadget.LocalPhotoSource.getImage(int)",this,throwable);throw throwable;}
    }

    private int[] getExponentialIndice(int total, int count) {
        com.mijack.Xlog.logMethodEnter("[int com.android.gallery3d.gadget.LocalPhotoSource.getExponentialIndice(int,int)",this,total,count);try{Random random = new Random();
        if (count > total) {count = total;}
        HashSet<Integer> selected = new HashSet<Integer>(count);
        while (selected.size() < count) {
            int row = (int)(-Math.log(random.nextDouble()) * total / 2);
            if (row < total) {selected.add(row);}
        }
        int values[] = new int[count];
        int index = 0;
        for (int value : selected) {
            values[index++] = value;
        }
        {com.mijack.Xlog.logMethodExit("[int com.android.gallery3d.gadget.LocalPhotoSource.getExponentialIndice(int,int)",this);return values;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[int com.android.gallery3d.gadget.LocalPhotoSource.getExponentialIndice(int,int)",this,throwable);throw throwable;}
    }

    private int getPhotoCount(ContentResolver resolver) {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.gadget.LocalPhotoSource.getPhotoCount(android.content.ContentResolver)",this,resolver);try{Cursor cursor = resolver.query(
                CONTENT_URI, COUNT_PROJECTION, SELECTION, null, null);
        if (cursor == null) {{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.gadget.LocalPhotoSource.getPhotoCount(android.content.ContentResolver)",this);return 0;}}
        try {
            Utils.assertTrue(cursor.moveToNext());
            {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.gadget.LocalPhotoSource.getPhotoCount(android.content.ContentResolver)",this);return cursor.getInt(0);}
        } finally {
            cursor.close();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.gadget.LocalPhotoSource.getPhotoCount(android.content.ContentResolver)",this,throwable);throw throwable;}
    }

    private boolean isContentSound(int totalCount) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.gadget.LocalPhotoSource.isContentSound(int)",this,totalCount);try{if (mPhotos.size() < Math.min(totalCount, MAX_PHOTO_COUNT)) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.gadget.LocalPhotoSource.isContentSound(int)",this);return false;}}
        if (mPhotos.size() == 0) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.gadget.LocalPhotoSource.isContentSound(int)",this);return true;}} /*// totalCount is also 0*/

        StringBuilder builder = new StringBuilder();
        for (Long imageId : mPhotos) {
            if (builder.length() > 0) {builder.append(",");}
            builder.append(imageId);
        }
        Cursor cursor = mContext.getContentResolver().query(
                CONTENT_URI, COUNT_PROJECTION,
                String.format("%s in (%s)", Media._ID, builder.toString()),
                null, null);
        if (cursor == null) {{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.gadget.LocalPhotoSource.isContentSound(int)",this);return false;}}
        try {
            Utils.assertTrue(cursor.moveToNext());
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.gadget.LocalPhotoSource.isContentSound(int)",this);return cursor.getInt(0) == mPhotos.size();}
        } finally {
            cursor.close();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.gadget.LocalPhotoSource.isContentSound(int)",this,throwable);throw throwable;}
    }

    public void reload() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.gadget.LocalPhotoSource.reload()",this);try{if (!mContentDirty) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.LocalPhotoSource.reload()",this);return;}}
        mContentDirty = false;

        ContentResolver resolver = mContext.getContentResolver();
        int photoCount = getPhotoCount(resolver);
        if (isContentSound(photoCount)) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.LocalPhotoSource.reload()",this);return;}}

        int choosedIds[] = getExponentialIndice(photoCount, MAX_PHOTO_COUNT);
        Arrays.sort(choosedIds);

        mPhotos.clear();
        Cursor cursor = mContext.getContentResolver().query(
                CONTENT_URI, PROJECTION, SELECTION, null, ORDER);
        if (cursor == null) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.LocalPhotoSource.reload()",this);return;}}
        try {
            for (int index : choosedIds) {
                if (cursor.moveToPosition(index)) {
                    mPhotos.add(cursor.getLong(0));
                }
            }
        } finally {
            cursor.close();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.gadget.LocalPhotoSource.reload()",this,throwable);throw throwable;}
    }

    @Override
    public int size() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.gadget.LocalPhotoSource.size()",this);try{reload();
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.gadget.LocalPhotoSource.size()",this);return mPhotos.size();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.gadget.LocalPhotoSource.size()",this,throwable);throw throwable;}
    }

    /**
     * Builds the bucket ID for the public external storage Downloads directory
     * @return the bucket ID
     */
    private static int getDownloadBucketId() {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.gadget.LocalPhotoSource.getDownloadBucketId()");try{String downloadsPath = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath();
        {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.gadget.LocalPhotoSource.getDownloadBucketId()");return GalleryUtils.getBucketId(downloadsPath);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.gadget.LocalPhotoSource.getDownloadBucketId()",throwable);throw throwable;}
    }

    @Override
    public void setContentListener(ContentListener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.gadget.LocalPhotoSource.setContentListener(com.android.gallery3d.data.ContentListener)",this,listener);try{mContentListener = listener;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.LocalPhotoSource.setContentListener(com.android.gallery3d.data.ContentListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.gadget.LocalPhotoSource.setContentListener(com.android.gallery3d.data.ContentListener)",this,throwable);throw throwable;}
    }
}
