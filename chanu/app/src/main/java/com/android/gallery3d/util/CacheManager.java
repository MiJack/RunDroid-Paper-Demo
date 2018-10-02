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

package com.android.gallery3d.util;

import com.android.gallery3d.common.BlobCache;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class CacheManager {
    private static final String TAG = "CacheManager";
    private static final String KEY_CACHE_UP_TO_DATE = "cache-up-to-date";
    private static HashMap<String, BlobCache> sCacheMap =
            new HashMap<String, BlobCache>();
    private static boolean sOldCheckDone = false;

    /*// Return null when we cannot instantiate a BlobCache, e.g.:*/
    /*// there is no SD card found.*/
    /*// This can only be called from data thread.*/
    public static BlobCache getCache(Context context, String filename,
            int maxEntries, int maxBytes, int version) {
        com.mijack.Xlog.logStaticMethodEnter("com.android.gallery3d.common.BlobCache com.android.gallery3d.util.CacheManager.getCache(android.content.Context,java.lang.String,int,int,int)",context,filename,maxEntries,maxBytes,version);try{com.mijack.Xlog.logStaticMethodExit("com.android.gallery3d.common.BlobCache com.android.gallery3d.util.CacheManager.getCache(android.content.Context,java.lang.String,int,int,int)");synchronized (sCacheMap) {
            if (!sOldCheckDone) {
                removeOldFilesIfNecessary(context);
                sOldCheckDone = true;
            }
            BlobCache cache = sCacheMap.get(filename);
            if (cache == null) {
                File cacheDir = context.getExternalCacheDir();
                String path = cacheDir.getAbsolutePath() + "/" + filename;
                try {
                    cache = new BlobCache(path, maxEntries, maxBytes, false,
                            version);
                    sCacheMap.put(filename, cache);
                } catch (IOException e) {
                    Log.e(TAG, "Cannot instantiate cache!", e);
                }
            }
            return cache;
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.android.gallery3d.common.BlobCache com.android.gallery3d.util.CacheManager.getCache(android.content.Context,java.lang.String,int,int,int)",throwable);throw throwable;}
    }

    /*// Removes the old files if the data is wiped.*/
    private static void removeOldFilesIfNecessary(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.util.CacheManager.removeOldFilesIfNecessary(android.content.Context)",context);try{SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        int n = 0;
        try {
            n = pref.getInt(KEY_CACHE_UP_TO_DATE, 0);
        } catch (Throwable t) {
            /*// ignore.*/
        }
        if (n != 0) {{com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.util.CacheManager.removeOldFilesIfNecessary(android.content.Context)");return;}}
        pref.edit().putInt(KEY_CACHE_UP_TO_DATE, 1).commit();

        File cacheDir = context.getExternalCacheDir();
        String prefix = cacheDir.getAbsolutePath() + "/";

        BlobCache.deleteFiles(prefix + "imgcache");
        BlobCache.deleteFiles(prefix + "rev_geocoding");
        BlobCache.deleteFiles(prefix + "bookmark");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.util.CacheManager.removeOldFilesIfNecessary(android.content.Context)",throwable);throw throwable;}
    }
}
