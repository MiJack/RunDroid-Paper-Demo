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

package com.android.gallery3d.data;

import com.android.gallery3d.util.Future;

import java.util.ArrayList;

/*// ComboAlbum combines multiple media sets into one. It lists all media items*/
/*// from the input albums.*/
/*// This only handles SubMediaSets, not MediaItems. (That's all we need now)*/
public class ComboAlbum extends MediaSet implements ContentListener {
    private static final String TAG = "ComboAlbum";
    private final MediaSet[] mSets;
    private final String mName;

    public ComboAlbum(Path path, MediaSet[] mediaSets, String name) {
        super(path, nextVersionNumber());
        mSets = mediaSets;
        for (MediaSet set : mSets) {
            set.addContentListener(this);
        }
        mName = name;
    }

    @Override
    public ArrayList<MediaItem> getMediaItem(int start, int count) {
        com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.android.gallery3d.data.ComboAlbum.getMediaItem(int,int)",this,start,count);try{ArrayList<MediaItem> items = new ArrayList<MediaItem>();
        for (MediaSet set : mSets) {
            int size = set.getMediaItemCount();
            if (count < 1) {break;}
            if (start < size) {
                int fetchCount = (start + count <= size) ? count : size - start;
                ArrayList<MediaItem> fetchItems = set.getMediaItem(start, fetchCount);
                items.addAll(fetchItems);
                count -= fetchItems.size();
                start = 0;
            } else {
                start -= size;
            }
        }
        {com.mijack.Xlog.logMethodExit("java.util.ArrayList com.android.gallery3d.data.ComboAlbum.getMediaItem(int,int)",this);return items;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.android.gallery3d.data.ComboAlbum.getMediaItem(int,int)",this,throwable);throw throwable;}
    }

    @Override
    public int getMediaItemCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.ComboAlbum.getMediaItemCount()",this);try{int count = 0;
        for (MediaSet set : mSets) {
            count += set.getMediaItemCount();
        }
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.ComboAlbum.getMediaItemCount()",this);return count;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.ComboAlbum.getMediaItemCount()",this,throwable);throw throwable;}
    }

    @Override
    public String getName() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.ComboAlbum.getName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.ComboAlbum.getName()",this);return mName;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.ComboAlbum.getName()",this,throwable);throw throwable;}
    }

    @Override
    public long reload() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.ComboAlbum.reload()",this);try{boolean changed = false;
        for (int i = 0, n = mSets.length; i < n; ++i) {
            long version = mSets[i].reload();
            if (version > mDataVersion) {changed = true;}
        }
        if (changed) {mDataVersion = nextVersionNumber();}
        {com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.ComboAlbum.reload()",this);return mDataVersion;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.ComboAlbum.reload()",this,throwable);throw throwable;}
    }

    public void onContentDirty() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.ComboAlbum.onContentDirty()",this);try{notifyContentChanged();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.ComboAlbum.onContentDirty()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.ComboAlbum.onContentDirty()",this,throwable);throw throwable;}
    }

    @Override
    public Future<Integer> requestSync(SyncListener listener) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.util.Future com.android.gallery3d.data.ComboAlbum.requestSync(SyncListener)",this,listener);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.Future com.android.gallery3d.data.ComboAlbum.requestSync(SyncListener)",this);return requestSyncOnEmptySets(mSets, listener);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.util.Future com.android.gallery3d.data.ComboAlbum.requestSync(SyncListener)",this,throwable);throw throwable;}
    }
}
