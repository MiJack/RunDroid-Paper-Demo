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

import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.ContentListener;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.MediaSet;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;

import java.util.ArrayList;
import java.util.Arrays;

public class MediaSetSource implements WidgetSource, ContentListener {
    private static final int CACHE_SIZE = 32;

    private static final String TAG = "MediaSetSource";

    private MediaSet mSource;
    private MediaItem mCache[] = new MediaItem[CACHE_SIZE];
    private int mCacheStart;
    private int mCacheEnd;
    private long mSourceVersion = MediaObject.INVALID_DATA_VERSION;

    private ContentListener mContentListener;

    public MediaSetSource(MediaSet source) {
        mSource = Utils.checkNotNull(source);
        mSource.addContentListener(this);
    }

    @Override
    public void close() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.gadget.MediaSetSource.close()",this);try{mSource.removeContentListener(this);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.MediaSetSource.close()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.gadget.MediaSetSource.close()",this,throwable);throw throwable;}
    }

    private void ensureCacheRange(int index) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.gadget.MediaSetSource.ensureCacheRange(int)",this,index);try{if (index >= mCacheStart && index < mCacheEnd) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.MediaSetSource.ensureCacheRange(int)",this);return;}}

        long token = Binder.clearCallingIdentity();
        try {
            mCacheStart = index;
            ArrayList<MediaItem> items = mSource.getMediaItem(mCacheStart, CACHE_SIZE);
            mCacheEnd = mCacheStart + items.size();
            items.toArray(mCache);
        } finally {
            Binder.restoreCallingIdentity(token);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.gadget.MediaSetSource.ensureCacheRange(int)",this,throwable);throw throwable;}
    }

    @Override
    public synchronized Uri getContentUri(int index) {
        com.mijack.Xlog.logMethodEnter("android.net.Uri com.android.gallery3d.gadget.MediaSetSource.getContentUri(int)",this,index);try{ensureCacheRange(index);
        if (index < mCacheStart || index >= mCacheEnd) {{com.mijack.Xlog.logMethodExit("android.net.Uri com.android.gallery3d.gadget.MediaSetSource.getContentUri(int)",this);return null;}}
        {com.mijack.Xlog.logMethodExit("android.net.Uri com.android.gallery3d.gadget.MediaSetSource.getContentUri(int)",this);return mCache[index - mCacheStart].getContentUri();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.net.Uri com.android.gallery3d.gadget.MediaSetSource.getContentUri(int)",this,throwable);throw throwable;}
    }

    @Override
    public synchronized Bitmap getImage(int index) {
        com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.gadget.MediaSetSource.getImage(int)",this,index);try{ensureCacheRange(index);
        if (index < mCacheStart || index >= mCacheEnd) {{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.gadget.MediaSetSource.getImage(int)",this);return null;}}
        {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.gadget.MediaSetSource.getImage(int)",this);return WidgetUtils.createWidgetBitmap(mCache[index - mCacheStart]);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.gadget.MediaSetSource.getImage(int)",this,throwable);throw throwable;}
    }

    @Override
    public void reload() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.gadget.MediaSetSource.reload()",this);try{long version = mSource.reload();
        if (mSourceVersion != version) {
            mSourceVersion = version;
            mCacheStart = 0;
            mCacheEnd = 0;
            Arrays.fill(mCache, null);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.MediaSetSource.reload()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.gadget.MediaSetSource.reload()",this,throwable);throw throwable;}
    }

    @Override
    public void setContentListener(ContentListener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.gadget.MediaSetSource.setContentListener(com.android.gallery3d.data.ContentListener)",this,listener);try{mContentListener = listener;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.MediaSetSource.setContentListener(com.android.gallery3d.data.ContentListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.gadget.MediaSetSource.setContentListener(com.android.gallery3d.data.ContentListener)",this,throwable);throw throwable;}
    }

    @Override
    public int size() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.gadget.MediaSetSource.size()",this);try{long token = Binder.clearCallingIdentity();
        try {
            {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.gadget.MediaSetSource.size()",this);return mSource.getMediaItemCount();}
        } finally {
            Binder.restoreCallingIdentity(token);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.gadget.MediaSetSource.size()",this,throwable);throw throwable;}
    }

    @Override
    public void onContentDirty() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.gadget.MediaSetSource.onContentDirty()",this);try{if (mContentListener != null) {mContentListener.onContentDirty();}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.MediaSetSource.onContentDirty()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.gadget.MediaSetSource.onContentDirty()",this,throwable);throw throwable;}
    }
}
