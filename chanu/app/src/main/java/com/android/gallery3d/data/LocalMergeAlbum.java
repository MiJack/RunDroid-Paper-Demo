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

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

/*// MergeAlbum merges items from two or more MediaSets. It uses a Comparator to*/
/*// determine the order of items. The items are assumed to be sorted in the input*/
/*// media sets (with the same order that the Comparator uses).*/
/*//*/
/*// This only handles MediaItems, not SubMediaSets.*/
public class LocalMergeAlbum extends MediaSet implements ContentListener {
    @SuppressWarnings("unused")
    private static final String TAG = "LocalMergeAlbum";
    private static final int PAGE_SIZE = 64;

    private final Comparator<MediaItem> mComparator;
    private final MediaSet[] mSources;

    private String mName;
    private FetchCache[] mFetcher;
    private int mSupportedOperation;

    /*// mIndex maps global position to the position of each underlying media sets.*/
    private TreeMap<Integer, int[]> mIndex = new TreeMap<Integer, int[]>();

    public LocalMergeAlbum(
            Path path, Comparator<MediaItem> comparator, MediaSet[] sources) {
        super(path, INVALID_DATA_VERSION);
        mComparator = comparator;
        mSources = sources;
        mName = sources.length == 0 ? "" : sources[0].getName();
        for (MediaSet set : mSources) {
            set.addContentListener(this);
        }
    }

    private void updateData() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.LocalMergeAlbum.updateData()",this);try{ArrayList<MediaSet> matches = new ArrayList<MediaSet>();
        int supported = mSources.length == 0 ? 0 : MediaItem.SUPPORT_ALL;
        mFetcher = new FetchCache[mSources.length];
        for (int i = 0, n = mSources.length; i < n; ++i) {
            mFetcher[i] = new FetchCache(mSources[i]);
            supported &= mSources[i].getSupportedOperations();
        }
        mSupportedOperation = supported;
        mIndex.clear();
        mIndex.put(0, new int[mSources.length]);
        mName = mSources.length == 0 ? "" : mSources[0].getName();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.LocalMergeAlbum.updateData()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.LocalMergeAlbum.updateData()",this,throwable);throw throwable;}
    }

    private void invalidateCache() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.LocalMergeAlbum.invalidateCache()",this);try{for (int i = 0, n = mSources.length; i < n; i++) {
            mFetcher[i].invalidate();
        }
        mIndex.clear();
        mIndex.put(0, new int[mSources.length]);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.LocalMergeAlbum.invalidateCache()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.LocalMergeAlbum.invalidateCache()",this,throwable);throw throwable;}
    }

    @Override
    public String getName() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.LocalMergeAlbum.getName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.LocalMergeAlbum.getName()",this);return mName;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.LocalMergeAlbum.getName()",this,throwable);throw throwable;}
    }

    @Override
    public int getMediaItemCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.LocalMergeAlbum.getMediaItemCount()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.LocalMergeAlbum.getMediaItemCount()",this);return getTotalMediaItemCount();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.LocalMergeAlbum.getMediaItemCount()",this,throwable);throw throwable;}
    }

    @Override
    public ArrayList<MediaItem> getMediaItem(int start, int count) {

        com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.android.gallery3d.data.LocalMergeAlbum.getMediaItem(int,int)",this,start,count);try{/*// First find the nearest mark position <= start.*/
        SortedMap<Integer, int[]> head = mIndex.headMap(start + 1);
        int markPos = head.lastKey();
        int[] subPos = head.get(markPos).clone();
        MediaItem[] slot = new MediaItem[mSources.length];

        int size = mSources.length;

        /*// fill all slots*/
        for (int i = 0; i < size; i++) {
            slot[i] = mFetcher[i].getItem(subPos[i]);
        }

        ArrayList<MediaItem> result = new ArrayList<MediaItem>();

        for (int i = markPos; i < start + count; i++) {
            int k = -1;  /*// k points to the best slot up to now.*/
            for (int j = 0; j < size; j++) {
                if (slot[j] != null) {
                    if (k == -1 || mComparator.compare(slot[j], slot[k]) < 0) {
                        k = j;
                    }
                }
            }

            /*// If we don't have anything, all streams are exhausted.*/
            if (k == -1) {break;}

            /*// Pick the best slot and refill it.*/
            subPos[k]++;
            if (i >= start) {
                result.add(slot[k]);
            }
            slot[k] = mFetcher[k].getItem(subPos[k]);

            /*// Periodically leave a mark in the index, so we can come back later.*/
            if ((i + 1) % PAGE_SIZE == 0) {
                mIndex.put(i + 1, subPos.clone());
            }
        }

        {com.mijack.Xlog.logMethodExit("java.util.ArrayList com.android.gallery3d.data.LocalMergeAlbum.getMediaItem(int,int)",this);return result;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.android.gallery3d.data.LocalMergeAlbum.getMediaItem(int,int)",this,throwable);throw throwable;}
    }

    @Override
    public int getTotalMediaItemCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.LocalMergeAlbum.getTotalMediaItemCount()",this);try{int count = 0;
        for (MediaSet set : mSources) {
            count += set.getTotalMediaItemCount();
        }
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.LocalMergeAlbum.getTotalMediaItemCount()",this);return count;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.LocalMergeAlbum.getTotalMediaItemCount()",this,throwable);throw throwable;}
    }

    @Override
    public long reload() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.LocalMergeAlbum.reload()",this);try{boolean changed = false;
        for (int i = 0, n = mSources.length; i < n; ++i) {
            if (mSources[i].reload() > mDataVersion) {changed = true;}
        }
        if (changed) {
            mDataVersion = nextVersionNumber();
            updateData();
            invalidateCache();
        }
        {com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.LocalMergeAlbum.reload()",this);return mDataVersion;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.LocalMergeAlbum.reload()",this,throwable);throw throwable;}
    }

    @Override
    public void onContentDirty() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.LocalMergeAlbum.onContentDirty()",this);try{notifyContentChanged();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.LocalMergeAlbum.onContentDirty()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.LocalMergeAlbum.onContentDirty()",this,throwable);throw throwable;}
    }

    @Override
    public int getSupportedOperations() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.LocalMergeAlbum.getSupportedOperations()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.LocalMergeAlbum.getSupportedOperations()",this);return mSupportedOperation;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.LocalMergeAlbum.getSupportedOperations()",this,throwable);throw throwable;}
    }

    @Override
    public void delete() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.LocalMergeAlbum.delete()",this);try{for (MediaSet set : mSources) {
            set.delete();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.LocalMergeAlbum.delete()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.LocalMergeAlbum.delete()",this,throwable);throw throwable;}
    }

    @Override
    public void rotate(int degrees) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.LocalMergeAlbum.rotate(int)",this,degrees);try{for (MediaSet set : mSources) {
            set.rotate(degrees);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.LocalMergeAlbum.rotate(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.LocalMergeAlbum.rotate(int)",this,throwable);throw throwable;}
    }

    private static class FetchCache {
        private MediaSet mBaseSet;
        private SoftReference<ArrayList<MediaItem>> mCacheRef;
        private int mStartPos;

        public FetchCache(MediaSet baseSet) {
            mBaseSet = baseSet;
        }

        public void invalidate() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.LocalMergeAlbum$FetchCache.invalidate()",this);try{mCacheRef = null;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.LocalMergeAlbum$FetchCache.invalidate()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.LocalMergeAlbum$FetchCache.invalidate()",this,throwable);throw throwable;}
        }

        public MediaItem getItem(int index) {
            com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaItem com.android.gallery3d.data.LocalMergeAlbum$FetchCache.getItem(int)",this,index);try{boolean needLoading = false;
            ArrayList<MediaItem> cache = null;
            if (mCacheRef == null
                    || index < mStartPos || index >= mStartPos + PAGE_SIZE) {
                needLoading = true;
            } else {
                cache = mCacheRef.get();
                if (cache == null) {
                    needLoading = true;
                }
            }

            if (needLoading) {
                cache = mBaseSet.getMediaItem(index, PAGE_SIZE);
                mCacheRef = new SoftReference<ArrayList<MediaItem>>(cache);
                mStartPos = index;
            }

            if (index < mStartPos || index >= mStartPos + cache.size()) {
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaItem com.android.gallery3d.data.LocalMergeAlbum$FetchCache.getItem(int)",this);return null;}
            }

            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaItem com.android.gallery3d.data.LocalMergeAlbum$FetchCache.getItem(int)",this);return cache.get(index - mStartPos);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaItem com.android.gallery3d.data.LocalMergeAlbum$FetchCache.getItem(int)",this,throwable);throw throwable;}
        }
    }

    @Override
    public boolean isLeafAlbum() {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.data.LocalMergeAlbum.isLeafAlbum()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.data.LocalMergeAlbum.isLeafAlbum()",this);return true;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.data.LocalMergeAlbum.isLeafAlbum()",this,throwable);throw throwable;}
    }
}
