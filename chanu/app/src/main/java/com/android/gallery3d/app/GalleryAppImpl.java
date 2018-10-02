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

package com.android.gallery3d.app;

import java.io.File;

import android.app.Application;
import android.content.Context;

import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.DownloadCache;
import com.android.gallery3d.data.ImageCacheService;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.ThreadPool;

public class GalleryAppImpl extends Application implements GalleryApp {

    private static final String DOWNLOAD_FOLDER = "download";
    private static final long DOWNLOAD_CAPACITY = 64 * 1024 * 1024; /*// 64M*/

    private ImageCacheService mImageCacheService;
    protected DataManager mDataManager;
    private ThreadPool mThreadPool;
    private DownloadCache mDownloadCache;

    @Override
    public void onCreate() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.GalleryAppImpl.onCreate()",this);try{super.onCreate();
        GalleryUtils.initialize(this);
        /*//WidgetUtils.initialize(this);*/
        /*//PicasaSource.initialize(this);*/com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.GalleryAppImpl.onCreate()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.GalleryAppImpl.onCreate()",this,throwable);throw throwable;}
    }

    public Context getAndroidContext() {
        com.mijack.Xlog.logMethodEnter("android.content.Context com.android.gallery3d.app.GalleryAppImpl.getAndroidContext()",this);try{com.mijack.Xlog.logMethodExit("android.content.Context com.android.gallery3d.app.GalleryAppImpl.getAndroidContext()",this);return this;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.content.Context com.android.gallery3d.app.GalleryAppImpl.getAndroidContext()",this,throwable);throw throwable;}
    }

    public synchronized DataManager getDataManager() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.DataManager com.android.gallery3d.app.GalleryAppImpl.getDataManager()",this);try{if (mDataManager == null) {
            mDataManager = new DataManager(this);
            mDataManager.initializeSourceMap();
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.DataManager com.android.gallery3d.app.GalleryAppImpl.getDataManager()",this);return mDataManager;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.DataManager com.android.gallery3d.app.GalleryAppImpl.getDataManager()",this,throwable);throw throwable;}
    }

    public synchronized ImageCacheService getImageCacheService() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.ImageCacheService com.android.gallery3d.app.GalleryAppImpl.getImageCacheService()",this);try{if (mImageCacheService == null) {
            mImageCacheService = new ImageCacheService(getAndroidContext());
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.ImageCacheService com.android.gallery3d.app.GalleryAppImpl.getImageCacheService()",this);return mImageCacheService;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.ImageCacheService com.android.gallery3d.app.GalleryAppImpl.getImageCacheService()",this,throwable);throw throwable;}
    }

    public synchronized ThreadPool getThreadPool() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.util.ThreadPool com.android.gallery3d.app.GalleryAppImpl.getThreadPool()",this);try{if (mThreadPool == null) {
            mThreadPool = new ThreadPool();
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.ThreadPool com.android.gallery3d.app.GalleryAppImpl.getThreadPool()",this);return mThreadPool;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.util.ThreadPool com.android.gallery3d.app.GalleryAppImpl.getThreadPool()",this,throwable);throw throwable;}
    }

    public synchronized DownloadCache getDownloadCache() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.DownloadCache com.android.gallery3d.app.GalleryAppImpl.getDownloadCache()",this);try{if (mDownloadCache == null) {
            File cacheDir = new File(getExternalCacheDir(), DOWNLOAD_FOLDER);

            if (!cacheDir.isDirectory()) {cacheDir.mkdirs();}

            if (!cacheDir.isDirectory()) {
                throw new RuntimeException(
                        "fail to create: " + cacheDir.getAbsolutePath());
            }
            mDownloadCache = new DownloadCache(this, cacheDir, DOWNLOAD_CAPACITY);
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.DownloadCache com.android.gallery3d.app.GalleryAppImpl.getDownloadCache()",this);return mDownloadCache;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.DownloadCache com.android.gallery3d.app.GalleryAppImpl.getDownloadCache()",this,throwable);throw throwable;}
    }
}
