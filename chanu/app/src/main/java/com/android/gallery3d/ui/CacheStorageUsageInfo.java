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

import android.os.Build;
import com.android.gallery3d.app.GalleryActivity;
import com.android.gallery3d.util.ThreadPool.JobContext;

import android.content.Context;
import android.os.StatFs;

import java.io.File;

public class CacheStorageUsageInfo {
    private static final String TAG = "CacheStorageUsageInfo";

    /*// number of bytes the storage has.*/
    private long mTotalBytes;

    /*// number of bytes already used.*/
    private long mUsedBytes;

    /*// number of bytes used for the cache (should be less then usedBytes).*/
    private long mUsedCacheBytes;

    /*// number of bytes used for the cache if all pending downloads (and removals) are completed.*/
    private long mTargetCacheBytes;

    private GalleryActivity mActivity;
    private Context mContext;
    private long mUserChangeDelta;

    public CacheStorageUsageInfo(GalleryActivity activity) {
        mActivity = activity;
        mContext = activity.getAndroidContext();
    }

    public void increaseTargetCacheSize(long delta) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.CacheStorageUsageInfo.increaseTargetCacheSize(long)",this,delta);try{mUserChangeDelta += delta;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.CacheStorageUsageInfo.increaseTargetCacheSize(long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.CacheStorageUsageInfo.increaseTargetCacheSize(long)",this,throwable);throw throwable;}
    }

    public void loadStorageInfo(JobContext jc) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.CacheStorageUsageInfo.loadStorageInfo(com.android.gallery3d.util.ThreadPool.JobContext)",this,jc);try{File cacheDir = mContext.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = mContext.getCacheDir();
        }

        String path = cacheDir.getAbsolutePath();
        StatFs stat = new StatFs(path);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            {deprecatedSetTotalUsedBytes(stat);}
        else
            {setTotalUsedBytes(stat);}

        mUsedCacheBytes = mActivity.getDataManager().getTotalUsedCacheSize();
        mTargetCacheBytes = mActivity.getDataManager().getTotalTargetCacheSize();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.CacheStorageUsageInfo.loadStorageInfo(com.android.gallery3d.util.ThreadPool.JobContext)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.CacheStorageUsageInfo.loadStorageInfo(com.android.gallery3d.util.ThreadPool.JobContext)",this,throwable);throw throwable;}
    }

    @SuppressWarnings("deprecation")
    protected void deprecatedSetTotalUsedBytes(StatFs stat) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.CacheStorageUsageInfo.deprecatedSetTotalUsedBytes(android.os.StatFs)",this,stat);try{long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        long totalBlocks = stat.getBlockCount();
        mTotalBytes = blockSize * totalBlocks;
        mUsedBytes = blockSize * (totalBlocks - availableBlocks);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.CacheStorageUsageInfo.deprecatedSetTotalUsedBytes(android.os.StatFs)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.CacheStorageUsageInfo.deprecatedSetTotalUsedBytes(android.os.StatFs)",this,throwable);throw throwable;}
    }

    protected void setTotalUsedBytes(StatFs stat) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.CacheStorageUsageInfo.setTotalUsedBytes(android.os.StatFs)",this,stat);try{long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        long totalBlocks = stat.getBlockCountLong();
        mTotalBytes = blockSize * totalBlocks;
        mUsedBytes = blockSize * (totalBlocks - availableBlocks);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.CacheStorageUsageInfo.setTotalUsedBytes(android.os.StatFs)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.CacheStorageUsageInfo.setTotalUsedBytes(android.os.StatFs)",this,throwable);throw throwable;}
    }

    public long getTotalBytes() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.ui.CacheStorageUsageInfo.getTotalBytes()",this);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.ui.CacheStorageUsageInfo.getTotalBytes()",this);return mTotalBytes;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.ui.CacheStorageUsageInfo.getTotalBytes()",this,throwable);throw throwable;}
    }

    public long getExpectedUsedBytes() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.ui.CacheStorageUsageInfo.getExpectedUsedBytes()",this);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.ui.CacheStorageUsageInfo.getExpectedUsedBytes()",this);return mUsedBytes - mUsedCacheBytes + mTargetCacheBytes + mUserChangeDelta;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.ui.CacheStorageUsageInfo.getExpectedUsedBytes()",this,throwable);throw throwable;}
    }

    public long getUsedBytes() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.ui.CacheStorageUsageInfo.getUsedBytes()",this);try{/*// Should it be usedBytes - usedCacheBytes + targetCacheBytes ?*/
        {com.mijack.Xlog.logMethodExit("long com.android.gallery3d.ui.CacheStorageUsageInfo.getUsedBytes()",this);return mUsedBytes;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.ui.CacheStorageUsageInfo.getUsedBytes()",this,throwable);throw throwable;}
    }

    public long getFreeBytes() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.ui.CacheStorageUsageInfo.getFreeBytes()",this);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.ui.CacheStorageUsageInfo.getFreeBytes()",this);return mTotalBytes - mUsedBytes;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.ui.CacheStorageUsageInfo.getFreeBytes()",this,throwable);throw throwable;}
    }
}
