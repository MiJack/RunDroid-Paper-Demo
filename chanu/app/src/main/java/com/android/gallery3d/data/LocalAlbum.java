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
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.GalleryUtils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Video;
import android.provider.MediaStore.Video.VideoColumns;

import java.util.ArrayList;

/*// LocalAlbumSet lists all media items in one bucket on local storage.*/
/*// The media items need to be all images or all videos, but not both.*/
public class LocalAlbum extends MediaSet {
    private static final String TAG = "LocalAlbum";
    private static final String[] COUNT_PROJECTION = { "count(*)" };

    private static final int INVALID_COUNT = -1;
    private final String mWhereClause;
    private final String mOrderClause;
    private final Uri mBaseUri;
    private final String[] mProjection;

    private final GalleryApp mApplication;
    private final ContentResolver mResolver;
    private final int mBucketId;
    private final String mBucketName;
    private final boolean mIsImage;
    private final ChangeNotifier mNotifier;
    private final Path mItemPath;
    private int mCachedCount = INVALID_COUNT;

    public LocalAlbum(Path path, GalleryApp application, int bucketId,
            boolean isImage, String name) {
        super(path, nextVersionNumber());
        mApplication = application;
        mResolver = application.getContentResolver();
        mBucketId = bucketId;
        mBucketName = name;
        mIsImage = isImage;

        if (isImage) {
            mWhereClause = ImageColumns.BUCKET_ID + " = ?";
            mOrderClause = ImageColumns.DATE_TAKEN + " DESC, "
                    + ImageColumns._ID + " DESC";
            mBaseUri = Images.Media.EXTERNAL_CONTENT_URI;
            mProjection = LocalImage.PROJECTION;
            mItemPath = LocalImage.ITEM_PATH;
        } else {
            mWhereClause = VideoColumns.BUCKET_ID + " = ?";
            mOrderClause = VideoColumns.DATE_TAKEN + " DESC, "
                    + VideoColumns._ID + " DESC";
            mBaseUri = Video.Media.EXTERNAL_CONTENT_URI;
            mProjection = LocalVideo.PROJECTION;
            mItemPath = LocalVideo.ITEM_PATH;
        }

        mNotifier = new ChangeNotifier(this, mBaseUri, application);
    }

    public LocalAlbum(Path path, GalleryApp application, int bucketId,
            boolean isImage) {
        this(path, application, bucketId, isImage,
                LocalAlbumSet.getBucketName(application.getContentResolver(),
                bucketId));
    }

    @Override
    public ArrayList<MediaItem> getMediaItem(int start, int count) {
        com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.android.gallery3d.data.LocalAlbum.getMediaItem(int,int)",this,start,count);try{DataManager dataManager = mApplication.getDataManager();
        Uri uri = mBaseUri.buildUpon()
                .appendQueryParameter("limit", start + "," + count).build();
        ArrayList<MediaItem> list = new ArrayList<MediaItem>();
        GalleryUtils.assertNotInRenderThread();
        Cursor cursor = mResolver.query(
                uri, mProjection, mWhereClause,
                new String[]{String.valueOf(mBucketId)},
                mOrderClause);
        if (cursor == null) {
            Log.w(TAG, "query fail: " + uri);
            {com.mijack.Xlog.logMethodExit("java.util.ArrayList com.android.gallery3d.data.LocalAlbum.getMediaItem(int,int)",this);return list;}
        }

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);  /*// _id must be in the first column*/
                Path childPath = mItemPath.getChild(id);
                MediaItem item = loadOrUpdateItem(childPath, cursor,
                        dataManager, mApplication, mIsImage);
                list.add(item);
            }
        } finally {
            cursor.close();
        }
        {com.mijack.Xlog.logMethodExit("java.util.ArrayList com.android.gallery3d.data.LocalAlbum.getMediaItem(int,int)",this);return list;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.android.gallery3d.data.LocalAlbum.getMediaItem(int,int)",this,throwable);throw throwable;}
    }

    private static MediaItem loadOrUpdateItem(Path path, Cursor cursor,
            DataManager dataManager, GalleryApp app, boolean isImage) {
        com.mijack.Xlog.logStaticMethodEnter("com.android.gallery3d.data.MediaItem com.android.gallery3d.data.LocalAlbum.loadOrUpdateItem(com.android.gallery3d.data.Path,android.database.Cursor,com.android.gallery3d.data.DataManager,com.android.gallery3d.app.GalleryApp,boolean)",path,cursor,dataManager,app,isImage);try{LocalMediaItem item = (LocalMediaItem) dataManager.peekMediaObject(path);
        if (item == null) {
            if (isImage) {
                item = new LocalImage(path, app, cursor);
            } else {
                item = new LocalVideo(path, app, cursor);
            }
        } else {
            item.updateContent(cursor);
        }
        {com.mijack.Xlog.logStaticMethodExit("com.android.gallery3d.data.MediaItem com.android.gallery3d.data.LocalAlbum.loadOrUpdateItem(com.android.gallery3d.data.Path,android.database.Cursor,com.android.gallery3d.data.DataManager,com.android.gallery3d.app.GalleryApp,boolean)");return item;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.android.gallery3d.data.MediaItem com.android.gallery3d.data.LocalAlbum.loadOrUpdateItem(com.android.gallery3d.data.Path,android.database.Cursor,com.android.gallery3d.data.DataManager,com.android.gallery3d.app.GalleryApp,boolean)",throwable);throw throwable;}
    }

    /*// The pids array are sorted by the (path) id.*/
    public static MediaItem[] getMediaItemById(
            GalleryApp application, boolean isImage, ArrayList<Integer> ids) {
        com.mijack.Xlog.logStaticMethodEnter("[com.android.gallery3d.data.MediaItem com.android.gallery3d.data.LocalAlbum.getMediaItemById(com.android.gallery3d.app.GalleryApp,boolean,java.util.ArrayList)",application,isImage,ids);try{/*// get the lower and upper bound of (path) id*/
        MediaItem[] result = new MediaItem[ids.size()];
        if (ids.isEmpty()) {{com.mijack.Xlog.logStaticMethodExit("[com.android.gallery3d.data.MediaItem com.android.gallery3d.data.LocalAlbum.getMediaItemById(com.android.gallery3d.app.GalleryApp,boolean,java.util.ArrayList)");return result;}}
        int idLow = ids.get(0);
        int idHigh = ids.get(ids.size() - 1);

        /*// prepare the query parameters*/
        Uri baseUri;
        String[] projection;
        Path itemPath;
        if (isImage) {
            baseUri = Images.Media.EXTERNAL_CONTENT_URI;
            projection = LocalImage.PROJECTION;
            itemPath = LocalImage.ITEM_PATH;
        } else {
            baseUri = Video.Media.EXTERNAL_CONTENT_URI;
            projection = LocalVideo.PROJECTION;
            itemPath = LocalVideo.ITEM_PATH;
        }

        ContentResolver resolver = application.getContentResolver();
        DataManager dataManager = application.getDataManager();
        Cursor cursor = resolver.query(baseUri, projection, "_id BETWEEN ? AND ?",
                new String[]{String.valueOf(idLow), String.valueOf(idHigh)},
                "_id");
        if (cursor == null) {
            Log.w(TAG, "query fail" + baseUri);
            {com.mijack.Xlog.logStaticMethodExit("[com.android.gallery3d.data.MediaItem com.android.gallery3d.data.LocalAlbum.getMediaItemById(com.android.gallery3d.app.GalleryApp,boolean,java.util.ArrayList)");return result;}
        }
        try {
            int n = ids.size();
            int i = 0;

            while (i < n && cursor.moveToNext()) {
                int id = cursor.getInt(0);  /*// _id must be in the first column*/

                /*// Match id with the one on the ids list.*/
                if (ids.get(i) > id) {
                    continue;
                }

                while (ids.get(i) < id) {
                    if (++i >= n) {
                        {com.mijack.Xlog.logStaticMethodExit("[com.android.gallery3d.data.MediaItem com.android.gallery3d.data.LocalAlbum.getMediaItemById(com.android.gallery3d.app.GalleryApp,boolean,java.util.ArrayList)");return result;}
                    }
                }

                Path childPath = itemPath.getChild(id);
                MediaItem item = loadOrUpdateItem(childPath, cursor, dataManager,
                        application, isImage);
                result[i] = item;
                ++i;
            }
            {com.mijack.Xlog.logStaticMethodExit("[com.android.gallery3d.data.MediaItem com.android.gallery3d.data.LocalAlbum.getMediaItemById(com.android.gallery3d.app.GalleryApp,boolean,java.util.ArrayList)");return result;}
        } finally {
            cursor.close();
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("[com.android.gallery3d.data.MediaItem com.android.gallery3d.data.LocalAlbum.getMediaItemById(com.android.gallery3d.app.GalleryApp,boolean,java.util.ArrayList)",throwable);throw throwable;}
    }

    public static Cursor getItemCursor(ContentResolver resolver, Uri uri,
            String[] projection, int id) {
        com.mijack.Xlog.logStaticMethodEnter("android.database.Cursor com.android.gallery3d.data.LocalAlbum.getItemCursor(android.content.ContentResolver,android.net.Uri,[java.lang.String,int)",resolver,uri,projection,id);try{com.mijack.Xlog.logStaticMethodExit("android.database.Cursor com.android.gallery3d.data.LocalAlbum.getItemCursor(android.content.ContentResolver,android.net.Uri,[java.lang.String,int)");return resolver.query(uri, projection, "_id=?",
                new String[]{String.valueOf(id)}, null);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.database.Cursor com.android.gallery3d.data.LocalAlbum.getItemCursor(android.content.ContentResolver,android.net.Uri,[java.lang.String,int)",throwable);throw throwable;}
    }

    @Override
    public int getMediaItemCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.LocalAlbum.getMediaItemCount()",this);try{if (mCachedCount == INVALID_COUNT) {
            Cursor cursor = mResolver.query(
                    mBaseUri, COUNT_PROJECTION, mWhereClause,
                    new String[]{String.valueOf(mBucketId)}, null);
            if (cursor == null) {
                Log.w(TAG, "query fail");
                {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.LocalAlbum.getMediaItemCount()",this);return 0;}
            }
            try {
                Utils.assertTrue(cursor.moveToNext());
                mCachedCount = cursor.getInt(0);
            } finally {
                cursor.close();
            }
        }
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.LocalAlbum.getMediaItemCount()",this);return mCachedCount;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.LocalAlbum.getMediaItemCount()",this,throwable);throw throwable;}
    }

    @Override
    public String getName() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.LocalAlbum.getName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.LocalAlbum.getName()",this);return mBucketName;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.LocalAlbum.getName()",this,throwable);throw throwable;}
    }

    @Override
    public long reload() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.LocalAlbum.reload()",this);try{if (mNotifier.isDirty()) {
            mDataVersion = nextVersionNumber();
            mCachedCount = INVALID_COUNT;
        }
        {com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.LocalAlbum.reload()",this);return mDataVersion;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.LocalAlbum.reload()",this,throwable);throw throwable;}
    }

    @Override
    public int getSupportedOperations() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.LocalAlbum.getSupportedOperations()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.LocalAlbum.getSupportedOperations()",this);return SUPPORT_DELETE | SUPPORT_SHARE | SUPPORT_INFO;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.LocalAlbum.getSupportedOperations()",this,throwable);throw throwable;}
    }

    @Override
    public void delete() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.LocalAlbum.delete()",this);try{GalleryUtils.assertNotInRenderThread();
        mResolver.delete(mBaseUri, mWhereClause,
                new String[]{String.valueOf(mBucketId)});com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.LocalAlbum.delete()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.LocalAlbum.delete()",this,throwable);throw throwable;}
    }

    @Override
    public boolean isLeafAlbum() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.data.LocalAlbum.isLeafAlbum()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.LocalAlbum.isLeafAlbum()",this);return true;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.data.LocalAlbum.isLeafAlbum()",this,throwable);throw throwable;}
    }
}
