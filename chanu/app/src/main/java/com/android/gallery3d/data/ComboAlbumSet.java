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

import com.chanapps.four.gallery3d.R;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.util.Future;

/*// ComboAlbumSet combines multiple media sets into one. It lists all sub*/
/*// media sets from the input album sets.*/
/*// This only handles SubMediaSets, not MediaItems. (That's all we need now)*/
public class ComboAlbumSet extends MediaSet implements ContentListener {
    private static final String TAG = "ComboAlbumSet";
    private final MediaSet[] mSets;
    private final String mName;

    public ComboAlbumSet(Path path, GalleryApp application, MediaSet[] mediaSets) {
        super(path, nextVersionNumber());
        mSets = mediaSets;
        for (MediaSet set : mSets) {
            set.addContentListener(this);
        }
        mName = application.getResources().getString(
                R.string.set_label_all_albums);
    }

    @Override
    public MediaSet getSubMediaSet(int index) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.ComboAlbumSet.getSubMediaSet(int)",this,index);try{for (MediaSet set : mSets) {
            int size = set.getSubMediaSetCount();
            if (index < size) {
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.ComboAlbumSet.getSubMediaSet(int)",this);return set.getSubMediaSet(index);}
            }
            index -= size;
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.ComboAlbumSet.getSubMediaSet(int)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.MediaSet com.android.gallery3d.data.ComboAlbumSet.getSubMediaSet(int)",this,throwable);throw throwable;}
    }

    @Override
    public int getSubMediaSetCount() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.data.ComboAlbumSet.getSubMediaSetCount()",this);try{int count = 0;
        for (MediaSet set : mSets) {
            count += set.getSubMediaSetCount();
        }
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.data.ComboAlbumSet.getSubMediaSetCount()",this);return count;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.data.ComboAlbumSet.getSubMediaSetCount()",this,throwable);throw throwable;}
    }

    @Override
    public String getName() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.data.ComboAlbumSet.getName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.data.ComboAlbumSet.getName()",this);return mName;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.data.ComboAlbumSet.getName()",this,throwable);throw throwable;}
    }

    @Override
    public long reload() {
        com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.data.ComboAlbumSet.reload()",this);try{boolean changed = false;
        for (int i = 0, n = mSets.length; i < n; ++i) {
            long version = mSets[i].reload();
            if (version > mDataVersion) {changed = true;}
        }
        if (changed) {mDataVersion = nextVersionNumber();}
        {com.mijack.Xlog.logMethodExit("long com.android.gallery3d.data.ComboAlbumSet.reload()",this);return mDataVersion;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.data.ComboAlbumSet.reload()",this,throwable);throw throwable;}
    }

    public void onContentDirty() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.data.ComboAlbumSet.onContentDirty()",this);try{notifyContentChanged();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.data.ComboAlbumSet.onContentDirty()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.data.ComboAlbumSet.onContentDirty()",this,throwable);throw throwable;}
    }

    @Override
    public Future<Integer> requestSync(SyncListener listener) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.util.Future com.android.gallery3d.data.ComboAlbumSet.requestSync(SyncListener)",this,listener);try{com.mijack.Xlog.logMethodExit("com.android.gallery3d.util.Future com.android.gallery3d.data.ComboAlbumSet.requestSync(SyncListener)",this);return requestSyncOnEmptySets(mSets, listener);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.util.Future com.android.gallery3d.data.ComboAlbumSet.requestSync(SyncListener)",this,throwable);throw throwable;}
    }
}
