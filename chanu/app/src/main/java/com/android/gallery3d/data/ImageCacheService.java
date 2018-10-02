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

import com.android.gallery3d.common.BlobCache;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.CacheManager;
import com.android.gallery3d.util.GalleryUtils;

import android.content.Context;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageCacheService {
    @SuppressWarnings("unused")
    private static final String TAG = "ImageCacheService";

    private static final String IMAGE_CACHE_FILE = "imgcache";
    private static final int IMAGE_CACHE_MAX_ENTRIES = 5000;
    private static final int IMAGE_CACHE_MAX_BYTES = 200 * 1024 * 1024;
    private static final int IMAGE_CACHE_VERSION = 3;

    private BlobCache mCache;

    public ImageCacheService(Context context) {
        mCache = CacheManager.getCache(context, IMAGE_CACHE_FILE,
                IMAGE_CACHE_MAX_ENTRIES, IMAGE_CACHE_MAX_BYTES,
                IMAGE_CACHE_VERSION);
    }

    public static class ImageData {
        public ImageData(byte[] data, int offset) {
            mData = data;
            mOffset = offset;
        }
        public byte[] mData;
        public int mOffset;
    }

    public ImageData getImageData(Path path, int type) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.ImageCacheService$ImageData com.android.gallery3d.data.ImageCacheService.getImageData(com.android.gallery3d.data.Path,int)",this,path,type);try{byte[] key = makeKey(path, type);
        long cacheKey = Utils.crc64Long(key);
        try {
            byte[] value = null;
            synchronized (mCache) {
                value = mCache.lookup(cacheKey);
            }
            if (value == null) {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.ImageCacheService$ImageData com.android.gallery3d.data.ImageCacheService.getImageData(com.android.gallery3d.data.Path,int)",this);return null;}}
            if (isSameKey(key, value)) {
                int offset = key.length;
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.ImageCacheService$ImageData com.android.gallery3d.data.ImageCacheService.getImageData(com.android.gallery3d.data.Path,int)",this);return new ImageData(value, offset);}
            }
        } catch (IOException ex) {
            /*// ignore.*/
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.ImageCacheService$ImageData com.android.gallery3d.data.ImageCacheService.getImageData(com.android.gallery3d.data.Path,int)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.ImageCacheService$ImageData com.android.gallery3d.data.ImageCacheService.getImageData(com.android.gallery3d.data.Path,int)",this,throwable);throw throwable;}
    }

    public void putImageData(Path path, int type, byte[] value) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.ImageCacheService.putImageData(com.android.gallery3d.data.Path,int,[byte)",this,path,type,value);try{byte[] key = makeKey(path, type);
        long cacheKey = Utils.crc64Long(key);
        ByteBuffer buffer = ByteBuffer.allocate(key.length + value.length);
        buffer.put(key);
        buffer.put(value);
        synchronized (mCache) {
            try {
                mCache.insert(cacheKey, buffer.array());
            } catch (IOException ex) {
                /*// ignore.*/
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.ImageCacheService.putImageData(com.android.gallery3d.data.Path,int,[byte)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.ImageCacheService.putImageData(com.android.gallery3d.data.Path,int,[byte)",this,throwable);throw throwable;}
    }

    private static byte[] makeKey(Path path, int type) {
        com.mijack.Xlog.logStaticMethodEnter("[byte com.android.gallery3d.data.ImageCacheService.makeKey(com.android.gallery3d.data.Path,int)",path,type);try{com.mijack.Xlog.logStaticMethodExit("[byte com.android.gallery3d.data.ImageCacheService.makeKey(com.android.gallery3d.data.Path,int)");return GalleryUtils.getBytes(path.toString() + "+" + type);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("[byte com.android.gallery3d.data.ImageCacheService.makeKey(com.android.gallery3d.data.Path,int)",throwable);throw throwable;}
    }

    private static boolean isSameKey(byte[] key, byte[] buffer) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.android.gallery3d.data.ImageCacheService.isSameKey([byte,[byte)",key,buffer);try{int n = key.length;
        if (buffer.length < n) {
            {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.data.ImageCacheService.isSameKey([byte,[byte)");return false;}
        }
        for (int i = 0; i < n; ++i) {
            if (key[i] != buffer[i]) {
                {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.data.ImageCacheService.isSameKey([byte,[byte)");return false;}
            }
        }
        {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.data.ImageCacheService.isSameKey([byte,[byte)");return true;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.android.gallery3d.data.ImageCacheService.isSameKey([byte,[byte)",throwable);throw throwable;}
    }
}
